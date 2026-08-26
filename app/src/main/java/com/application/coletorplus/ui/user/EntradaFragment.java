package com.application.coletorplus.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.application.coletorplus.data.database.AppDatabase;
import com.application.coletorplus.data.model.Endereco;
import com.application.coletorplus.data.model.Produto;
import com.application.coletorplus.data.model.ProdutoEndereco;
import com.application.coletorplus.databinding.FragmentEntradaBinding;
import com.application.coletorplus.ui.adapter.EntradaBatchAdapter;
import com.application.coletorplus.ui.scanner.ScannerHelper;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.List;

public class EntradaFragment extends Fragment {

    private FragmentEntradaBinding binding;
    private EntradaBatchAdapter adapter;
    private Endereco enderecoSelecionado;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEntradaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();

        // 1. Scanner/Busca de Produtos para a Lista
        binding.btnScannerProdutoEntrada.setOnClickListener(v -> ScannerHelper.escanearProduto(barcodeProduto));

        binding.etCodigoProdutoEntrada.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (binding.etCodigoProdutoEntrada.getText() != null) {
                    adicionarProdutoPorEan(binding.etCodigoProdutoEntrada.getText().toString().trim().toUpperCase());
                }
                return true;
            }
            return false;
        });

        // 2. Scanner/Busca da Rua de Destino
        binding.btnScannerRua.setOnClickListener(v -> ScannerHelper.escanearEndereco(barcodeRua));

        binding.etCodigoRua.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (binding.etCodigoRua.getText() != null) {
                    buscarEndereco(binding.etCodigoRua.getText().toString().trim().toUpperCase());
                }
                return true;
            }
            return false;
        });

        // 3. Gravação em lote no Room
        binding.btnConfirmarEntrada.setOnClickListener(v -> confirmarEnderecamento());

        carregarSugestoesDeRua();
    }

    private void carregarSugestoesDeRua() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext().getApplicationContext());
            List<Endereco> enderecos = db.enderecoDao().listarTodos();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    List<String> nomes = new java.util.ArrayList<>();
                    for (Endereco e : enderecos) nomes.add(e.getDescricao());
                    android.widget.ArrayAdapter<String> adapterRuas = new android.widget.ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, nomes);
                    binding.etCodigoRua.setAdapter(adapterRuas);
                });
            }
        }).start();
    }

    private void setupRecyclerView() {
        adapter = new EntradaBatchAdapter(position -> adapter.removerProduto(position));
        binding.rvEntradaProdutos.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvEntradaProdutos.setAdapter(adapter);
    }

    private void adicionarProdutoPorEan(String ean) {
        if (ean == null || ean.trim().isEmpty() || getContext() == null) return;
        final String eanFinal = ean.trim().toUpperCase();
        final android.content.Context context = getContext().getApplicationContext();

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            Produto p = db.produtoDao().buscarPorEan(eanFinal);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (p != null) {
                        adapter.addProduto(p);
                        binding.etCodigoProdutoEntrada.setText("");
                        binding.etCodigoProdutoEntrada.requestFocus();
                        binding.rvEntradaProdutos.scrollToPosition(0);
                    } else {
                        Toast.makeText(context, "Produto " + eanFinal + " não encontrado", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    private void buscarEndereco(String codigo) {
        if (codigo == null || codigo.trim().isEmpty() || getContext() == null) return;
        final String codigoFinal = codigo.trim().toUpperCase();
        final android.content.Context context = getContext().getApplicationContext();

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            Endereco e = db.enderecoDao().buscarRuaExata(codigoFinal);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (e != null) {
                        setEnderecoSelecionado(e);
                        binding.etCodigoRua.setText(e.getDescricao());
                    } else {
                        enderecoSelecionado = null;
                        binding.cardInfoRuaIdentificada.setVisibility(View.GONE);
                        solicitarCriacaoDeEndereco(codigoFinal);
                    }
                });
            }
        }).start();
    }

    private void setEnderecoSelecionado(Endereco e) {
        enderecoSelecionado = e;
        binding.cardInfoRuaIdentificada.setVisibility(View.VISIBLE);
        binding.tvRuaIdentificada.setText(String.format("Local: %s", e.getDescricao()));
        binding.etCodigoRua.setText("");
        Toast.makeText(getContext(), "Rua vinculada!", Toast.LENGTH_SHORT).show();
    }

    private void solicitarCriacaoDeEndereco(String codigo) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Local não encontrado")
                .setMessage("O endereço '" + codigo + "' não existe no banco de dados. Deseja cadastrá-lo agora?")
                .setPositiveButton("Cadastrar e Usar", (dialog, which) -> cadastrarNovoEndereco(codigo))
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    enderecoSelecionado = null;
                    binding.cardInfoRuaIdentificada.setVisibility(View.GONE);
                })
                .show();
    }

    private void cadastrarNovoEndereco(String codigo) {
        if (getContext() == null) return;
        final android.content.Context context = getContext().getApplicationContext();
        
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            Endereco novo = new Endereco(codigo);
            long id = db.enderecoDao().inserir(novo);
            novo.setId((int) id);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (id > 0) {
                        setEnderecoSelecionado(novo);
                    } else {
                        Toast.makeText(getContext(), "Erro ao cadastrar local", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    private void confirmarEnderecamento() {
        List<Produto> produtos = adapter.getProdutos();

        if (produtos.isEmpty()) {
            Toast.makeText(getContext(), "Adicione ao menos um produto à lista", Toast.LENGTH_SHORT).show();
            return;
        }

        // Se não foi identificado pelo scanner, tenta buscar pelo que está digitado no campo
        if (enderecoSelecionado == null) {
            String textoRua = binding.etCodigoRua.getText().toString().trim().toUpperCase();
            if (textoRua.isEmpty()) {
                Toast.makeText(getContext(), "Digite ou escaneie o local de destino", Toast.LENGTH_SHORT).show();
                return;
            }
            
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(requireContext().getApplicationContext());
                Endereco e = db.enderecoDao().buscarRuaExata(textoRua);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (e != null) {
                            enderecoSelecionado = e;
                            processarVinculoEmLote(produtos);
                        } else {
                            solicitarCriacaoDeEndereco(textoRua);
                        }
                    });
                }
            }).start();
        } else {
            processarVinculoEmLote(produtos);
        }
    }

    private void processarVinculoEmLote(List<Produto> produtos) {
        if (getContext() == null) return;
        final android.content.Context context = getContext().getApplicationContext();

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            for (Produto p : produtos) {
                db.enderecoDao().vincularProdutoEndereco(new ProdutoEndereco(p.getId(), enderecoSelecionado.getId()));
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), produtos.size() + " produtos endereçados em " + enderecoSelecionado.getDescricao() + "!", Toast.LENGTH_LONG).show();
                    limparTela();
                });
            }
        }).start();
    }

    private void limparTela() {
        adapter.limpar();
        enderecoSelecionado = null;
        if (binding != null) {
            binding.cardInfoRuaIdentificada.setVisibility(View.GONE);
            binding.etCodigoProdutoEntrada.setText("");
            binding.etCodigoRua.setText("");
        }
    }

    private final ActivityResultLauncher<ScanOptions> barcodeProduto = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    adicionarProdutoPorEan(result.getContents().trim().toUpperCase());
                }
            }
    );

    private final ActivityResultLauncher<ScanOptions> barcodeRua = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    buscarEndereco(result.getContents().trim().toUpperCase());
                }
            }
    );

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}