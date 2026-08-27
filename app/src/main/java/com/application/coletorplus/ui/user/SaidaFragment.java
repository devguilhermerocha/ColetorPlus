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
import com.application.coletorplus.data.model.Auditoria;
import com.application.coletorplus.data.model.Endereco;
import com.application.coletorplus.data.model.Produto;
import com.application.coletorplus.data.session.SessionManager;
import com.application.coletorplus.databinding.FragmentSaidaBinding;
import com.application.coletorplus.ui.adapter.SaidaBatchAdapter;
import com.application.coletorplus.ui.scanner.ScannerHelper;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.List;

public class SaidaFragment extends Fragment {

    private FragmentSaidaBinding binding;
    private SaidaBatchAdapter adapter;
    private Endereco enderecoSelecionado;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSaidaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();

        // Scanner/Busca da Rua
        binding.btnScannerSaida.setOnClickListener(v -> ScannerHelper.escanearEndereco(barcodeRua));

        binding.etCodigoSaida.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (binding.etCodigoSaida.getText() != null) {
                    buscarEndereco(binding.etCodigoSaida.getText().toString().trim().toUpperCase());
                }
                return true;
            }
            return false;
        });

        binding.etCodigoSaida.setOnItemClickListener((parent, view1, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            buscarEndereco(selected);
        });

        // Botão para limpar todo o local
        binding.btnLimparLocalCompleto.setOnClickListener(v -> confirmarLimpezaTotal());

        carregarSugestoesDeRua();
    }

    private void setupRecyclerView() {
        adapter = new SaidaBatchAdapter(this::removerProdutoIndividual);
        binding.rvSaidaItens.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSaidaItens.setAdapter(adapter);
    }

    private void carregarSugestoesDeRua() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext().getApplicationContext());
            List<Endereco> enderecos = db.enderecoDao().listarTodos();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    List<String> nomes = new ArrayList<>();
                    for (Endereco e : enderecos) nomes.add(e.getDescricao());
                    ArrayAdapter<String> adapterRuas = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, nomes);
                    binding.etCodigoSaida.setAdapter(adapterRuas);
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
                        carregarProdutosDoLocal(e);
                    } else {
                        enderecoSelecionado = null;
                        binding.cardInfoRuaSaida.setVisibility(View.GONE);
                        adapter.setProdutos(new ArrayList<>());
                        Toast.makeText(context, "Local não encontrado", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    private void setEnderecoSelecionado(Endereco e) {
        enderecoSelecionado = e;
        binding.cardInfoRuaSaida.setVisibility(View.VISIBLE);
        binding.tvRuaIdentificadaSaida.setText(String.format("Local: %s", e.getDescricao()));
        binding.etCodigoSaida.setText("");
    }

    private void carregarProdutosDoLocal(Endereco e) {
        if (getContext() == null) return;
        final android.content.Context context = getContext().getApplicationContext();

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            List<Produto> produtos = db.enderecoDao().buscarProdutosPorEndereco(e.getId());
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    adapter.setProdutos(produtos);
                    if (produtos.isEmpty()) {
                        Toast.makeText(context, "Nenhum produto vinculado a este local.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    private void removerProdutoIndividual(Produto p) {
        if (enderecoSelecionado == null) return;
        
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirmar Retirada")
                .setMessage("Deseja desvincular o produto " + p.getNome() + " deste local?")
                .setPositiveButton("Sim, Retirar", (dialog, which) -> {
                    new Thread(() -> {
                        AppDatabase db = AppDatabase.getInstance(requireContext().getApplicationContext());
                        db.enderecoDao().desenderecarProduto(p.getId(), enderecoSelecionado.getId());
                        
                        // REGISTRO DE AUDITORIA
                        String nomeUsuario = SessionManager.getInstance().getUsuarioLogado() != null 
                                ? SessionManager.getInstance().getUsuarioLogado().getNome() : "Desconhecido";
                        String logDesc = "Produto " + p.getNome() + " retirado de " + enderecoSelecionado.getDescricao();
                        db.auditoriaDao().inserir(new Auditoria(nomeUsuario, "SAÍDA", logDesc, System.currentTimeMillis()));

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Produto retirado!", Toast.LENGTH_SHORT).show();
                                carregarProdutosDoLocal(enderecoSelecionado);
                            });
                        }
                    }).start();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarLimpezaTotal() {
        if (enderecoSelecionado == null) {
            Toast.makeText(getContext(), "Identifique um local primeiro", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("LIMPAR TODO LOCAL")
                .setMessage("TEM CERTEZA que deseja desvincular TODOS os produtos de " + enderecoSelecionado.getDescricao() + "?")
                .setPositiveButton("SIM, LIMPAR TUDO", (dialog, which) -> {
                    new Thread(() -> {
                        AppDatabase db = AppDatabase.getInstance(requireContext().getApplicationContext());
                        db.enderecoDao().desenderecarTodosProdutos(enderecoSelecionado.getId());
                        
                        // REGISTRO DE AUDITORIA
                        String nomeUsuario = SessionManager.getInstance().getUsuarioLogado() != null 
                                ? SessionManager.getInstance().getUsuarioLogado().getNome() : "Desconhecido";
                        String logDesc = "LIMPEZA TOTAL realizada em " + enderecoSelecionado.getDescricao();
                        db.auditoriaDao().inserir(new Auditoria(nomeUsuario, "SAÍDA", logDesc, System.currentTimeMillis()));

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Local limpo com sucesso!", Toast.LENGTH_LONG).show();
                                carregarProdutosDoLocal(enderecoSelecionado);
                            });
                        }
                    }).start();
                })
                .setNegativeButton("Não", null)
                .show();
    }

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
