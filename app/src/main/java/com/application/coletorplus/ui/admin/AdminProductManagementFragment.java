package com.application.coletorplus.ui.admin;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.coletorplus.R;
import com.application.coletorplus.data.database.AppDatabase;
import com.application.coletorplus.data.model.Produto;
import com.application.coletorplus.data.model.Validade;
import com.application.coletorplus.databinding.FragmentAdminProductsBinding;
import com.application.coletorplus.databinding.ItemProdutoBinding;
import com.application.coletorplus.ui.scanner.ScannerHelper;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.List;

public class AdminProductManagementFragment extends Fragment {

    private FragmentAdminProductsBinding binding;
    private ProductAdminAdapter adapter;

    // Campos temporários para o diálogo de edição/cadastro via scanner
    private EditText etDialogEan;
    private EditText etDialogNome;
    private EditText etDialogQuantidade;
    private EditText etDialogValidade;
    private TextView tvDialogAviso;

    private long selectedValidadeTimestamp = 0;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    // Flag para saber de onde veio o scan
    private boolean isScanningForDialog = false;

    private final androidx.activity.result.ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    String eanLido = result.getContents().trim();
                    if (isScanningForDialog && etDialogEan != null) {
                        etDialogEan.setText(eanLido);
                        buscarProdutoParaDialogo(eanLido);
                    } else {
                        processarScanCatalogo(eanLido);
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminProductsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rvProductManagement.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductAdminAdapter(this::confirmarRemocao);
        binding.rvProductManagement.setAdapter(adapter);

        binding.btnNovoProduto.setOnClickListener(v -> showAddProductDialog());
        
        // Scanner direto no catálogo
        binding.btnBarcodeScanner.setOnClickListener(v -> {
            isScanningForDialog = false;
            ScannerHelper.escanearProduto(barcodeLauncher);
        });

        binding.etSearchInventory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buscarProdutos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        carregarProdutos();
    }

    private void carregarProdutos() {
        new Thread(() -> {
            List<Produto> produtos = AppDatabase.getInstance(requireContext()).produtoDao().listarTodos();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> adapter.setProdutos(produtos));
            }
        }).start();
    }

    private void buscarProdutos(String termo) {
        if (termo.isEmpty()) {
            carregarProdutos();
            return;
        }
        new Thread(() -> {
            List<Produto> produtos = AppDatabase.getInstance(requireContext()).produtoDao().buscarPorTermo(termo);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> adapter.setProdutos(produtos));
            }
        }).start();
    }

    private void confirmarRemocao(Produto produto) {
        new AlertDialog.Builder(getContext())
                .setTitle("Remover Produto")
                .setMessage("Deseja realmente remover " + produto.getNome() + "?\nIsso removerá também todos os seus vínculos de endereço.")
                .setPositiveButton("Remover", (dialog, which) -> {
                    new Thread(() -> {
                        AppDatabase.getInstance(requireContext()).produtoDao().deletar(produto);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Produto removido", Toast.LENGTH_SHORT).show();
                                carregarProdutos();
                            });
                        }
                    }).start();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showAddProductDialog() {
        isScanningForDialog = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Novo/Editar Produto");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_admin_novo_produto, null);
        etDialogEan = view.findViewById(R.id.etNovoEan);
        etDialogNome = view.findViewById(R.id.etNovoNome);
        etDialogQuantidade = view.findViewById(R.id.etNovoQuantidade);
        etDialogValidade = view.findViewById(R.id.etNovoValidade);
        tvDialogAviso = view.findViewById(R.id.tvAvisoExistente);
        android.widget.ImageButton btnScanDialog = view.findViewById(R.id.btnScanEanDialog);

        selectedValidadeTimestamp = 0;

        // Configurar DatePicker para Validade
        if (etDialogValidade != null) {
            etDialogValidade.setOnClickListener(v -> showDatePicker());
        }

        // Ação do botão de scanner ao lado do EAN no diálogo
        if (btnScanDialog != null) {
            btnScanDialog.setOnClickListener(v -> {
                isScanningForDialog = true;
                ScannerHelper.escanearProduto(barcodeLauncher);
            });
        }

        // Busca automática ao digitar o EAN completo
        etDialogEan.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (s.length() >= 8) { // EANs costumam ter 8, 13 ou 14 dígitos
                    buscarProdutoParaDialogo(s.toString());
                }
            }
        });

        builder.setView(view);

        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String nome = etDialogNome.getText().toString().trim();
            String ean = etDialogEan.getText().toString().trim();
            String qtdStr = etDialogQuantidade.getText().toString().trim();
            int quantidade = qtdStr.isEmpty() ? 0 : Integer.parseInt(qtdStr);

            if (!nome.isEmpty() && !ean.isEmpty()) {
                new Thread(() -> {
                    Produto existente = AppDatabase.getInstance(requireContext()).produtoDao().buscarPorEan(ean);
                    long produtoId;
                    
                    if (existente != null) {
                        // Atualiza existente
                        existente.setNome(nome);
                        AppDatabase.getInstance(requireContext()).produtoDao().atualizar(existente);
                        produtoId = existente.getId();
                    } else {
                        // Insere novo
                        Produto novo = new Produto(nome, ean, 0); // Quantidade inicial será calculada abaixo
                        produtoId = AppDatabase.getInstance(requireContext()).produtoDao().inserir(novo);
                    }

                    // Se uma validade e quantidade foram informadas, adiciona como um novo lote
                    if (selectedValidadeTimestamp > 0 && quantidade > 0) {
                        Validade novoLote = new Validade(produtoId, selectedValidadeTimestamp, quantidade);
                        AppDatabase.getInstance(requireContext()).validadeDao().inserirValidade(novoLote);
                        
                        // Recalcula a quantidade total do produto baseada em todos os lotes (validades)
                        int novaSomaTotal = AppDatabase.getInstance(requireContext()).validadeDao().getSomaQuantidades(produtoId);
                        
                        // Busca o produto atualizado para garantir que temos o objeto correto para update
                        Produto pParaUpdate = AppDatabase.getInstance(requireContext()).produtoDao().buscarPorEan(ean);
                        if (pParaUpdate != null) {
                            pParaUpdate.setQuantidadeTotal(novaSomaTotal);
                            AppDatabase.getInstance(requireContext()).produtoDao().atualizar(pParaUpdate);
                        }
                    } else if (existente == null && quantidade > 0) {
                        // Caso especial: Novo produto sem validade mas com quantidade (adiciona validade genérica ou apenas seta)
                        // Para este fluxo, vamos forçar que quantidade venha com validade ou tratar apenas a soma.
                        // Se não tem validade, a quantidade total pode ser setada diretamente se for novo.
                        Produto pParaUpdate = AppDatabase.getInstance(requireContext()).produtoDao().buscarPorEan(ean);
                        if (pParaUpdate != null) {
                            pParaUpdate.setQuantidadeTotal(quantidade);
                            AppDatabase.getInstance(requireContext()).produtoDao().atualizar(pParaUpdate);
                        }
                    }
                    
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Dados salvos com sucesso!", Toast.LENGTH_SHORT).show();
                            carregarProdutos();
                        });
                    }
                }).start();
            } else {
                Toast.makeText(getContext(), "Preencha Nome e EAN", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        if (selectedValidadeTimestamp > 0) {
            calendar.setTimeInMillis(selectedValidadeTimestamp);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);
                    selectedValidadeTimestamp = selected.getTimeInMillis();
                    etDialogValidade.setText(dateFormat.format(selected.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void buscarProdutoParaDialogo(String ean) {
        new Thread(() -> {
            Produto p = AppDatabase.getInstance(requireContext()).produtoDao().buscarPorEan(ean);
            if (p != null && getActivity() != null) {
                // Buscar validade vinculada
                List<Validade> validades = AppDatabase.getInstance(requireContext()).validadeDao().buscarPorProduto(p.getId());
                
                getActivity().runOnUiThread(() -> {
                    etDialogNome.setText(p.getNome());
                    etDialogQuantidade.setText(String.valueOf(p.getQuantidadeTotal()));
                    tvDialogAviso.setVisibility(View.VISIBLE);
                    
                    if (!validades.isEmpty()) {
                        Validade v = validades.get(validades.size() - 1); // Pega a última cadastrada
                        selectedValidadeTimestamp = v.getDataVencimento();
                        etDialogValidade.setText(dateFormat.format(v.getDataVencimento()));
                    } else {
                        selectedValidadeTimestamp = 0;
                        etDialogValidade.setText("");
                    }
                });
            } else if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    tvDialogAviso.setVisibility(View.GONE);
                });
            }
        }).start();
    }

    private void processarScanCatalogo(String ean) {
        new Thread(() -> {
            Produto p = AppDatabase.getInstance(requireContext()).produtoDao().buscarPorEan(ean);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (p != null) {
                        // Produto existe, filtra na lista
                        binding.etSearchInventory.setText(ean);
                        buscarProdutos(ean);
                    } else {
                        // Produto não existe, abre cadastro
                        Toast.makeText(getContext(), "Produto não cadastrado. Abrindo novo cadastro...", Toast.LENGTH_SHORT).show();
                        showAddProductDialog();
                        // Preenche o EAN no diálogo recém aberto
                        if (etDialogEan != null) {
                            etDialogEan.setText(ean);
                        }
                    }
                });
            }
        }).start();
    }

    private static class ProductAdminAdapter extends RecyclerView.Adapter<ProductAdminAdapter.ViewHolder> {
        private List<Produto> produtos = new ArrayList<>();
        private final OnProductDeleteListener deleteListener;

        public interface OnProductDeleteListener {
            void onDelete(Produto produto);
        }

        public ProductAdminAdapter(OnProductDeleteListener deleteListener) {
            this.deleteListener = deleteListener;
        }

        public void setProdutos(List<Produto> produtos) {
            this.produtos = produtos;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemProdutoBinding binding = ItemProdutoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Produto p = produtos.get(position);
            holder.binding.tvNomeProduto.setText(p.getNome());
            holder.binding.tvEanProduto.setText("EAN: " + p.getCodigoEan());
            
            holder.binding.tvQuantidadeProduto.setText("Qtd: " + p.getQuantidadeTotal());
            
            // Usamos o clique longo ou um botão para deletar
            // Como item_produto parece não ter botão de remover, vamos adicionar suporte a clique no item ou adaptar
            holder.itemView.setOnLongClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDelete(p);
                }
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return produtos.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ItemProdutoBinding binding;
            public ViewHolder(ItemProdutoBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}