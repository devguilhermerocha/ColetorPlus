package com.application.coletorplus.ui.admin;

import android.app.AlertDialog;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.coletorplus.R;
import com.application.coletorplus.data.database.AppDatabase;
import com.application.coletorplus.data.model.Produto;
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
    private CheckBox cbDialogCritico;
    private TextView tvDialogAviso;

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
        binding.btnScanProductCat.setOnClickListener(v -> {
            isScanningForDialog = false;
            ScannerHelper.escanearProduto(barcodeLauncher);
        });

        // Ícone de scanner na busca
        binding.tilSearchProduct.setEndIconOnClickListener(v -> {
            isScanningForDialog = false;
            ScannerHelper.escanearProduto(barcodeLauncher);
        });

        binding.etSearchProduct.addTextChangedListener(new TextWatcher() {
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
        cbDialogCritico = view.findViewById(R.id.cbNovoCritico);
        tvDialogAviso = view.findViewById(R.id.tvAvisoExistente);
        com.google.android.material.textfield.TextInputLayout tilEan = view.findViewById(R.id.tilNovoEan);
        com.google.android.material.button.MaterialButton btnScanDialog = view.findViewById(R.id.btnScanNoDialog);

        // Ação do botão de scanner GRANDE no diálogo
        if (btnScanDialog != null) {
            btnScanDialog.setOnClickListener(v -> {
                isScanningForDialog = true;
                ScannerHelper.escanearProduto(barcodeLauncher);
            });
        }

        // Ação do ícone de leitor dentro do TextInputLayout
        tilEan.setEndIconOnClickListener(v -> {
            isScanningForDialog = true;
            ScannerHelper.escanearProduto(barcodeLauncher);
        });

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
            boolean isCritico = cbDialogCritico.isChecked();

            if (!nome.isEmpty() && !ean.isEmpty()) {
                new Thread(() -> {
                    Produto existente = AppDatabase.getInstance(requireContext()).produtoDao().buscarPorEan(ean);
                    if (existente != null) {
                        // Atualiza existente
                        existente.setNome(nome);
                        existente.setCritico(isCritico);
                        AppDatabase.getInstance(requireContext()).produtoDao().atualizar(existente);
                    } else {
                        // Insere novo
                        Produto novo = new Produto(nome, ean, isCritico, false);
                        AppDatabase.getInstance(requireContext()).produtoDao().inserir(novo);
                    }
                    
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Produto salvo com sucesso!", Toast.LENGTH_SHORT).show();
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

    private void buscarProdutoParaDialogo(String ean) {
        new Thread(() -> {
            Produto p = AppDatabase.getInstance(requireContext()).produtoDao().buscarPorEan(ean);
            if (p != null && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    etDialogNome.setText(p.getNome());
                    cbDialogCritico.setChecked(p.isCritico());
                    tvDialogAviso.setVisibility(View.VISIBLE);
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
                        binding.etSearchProduct.setText(ean);
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
            
            holder.binding.tvTagCritico.setVisibility(p.isCritico() ? View.VISIBLE : View.GONE);
            
            // Na área de admin, escondemos o checkbox de reposição
            holder.binding.cbReposto.setVisibility(View.GONE);
            
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