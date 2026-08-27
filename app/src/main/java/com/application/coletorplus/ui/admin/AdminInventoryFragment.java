package com.application.coletorplus.ui.admin;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import com.application.coletorplus.databinding.FragmentAdminInventoryBinding;
import com.application.coletorplus.ui.adapter.EnderecoAdapter;
import com.application.coletorplus.ui.adapter.ProdutoAdapter;
import com.application.coletorplus.ui.scanner.ScannerHelper;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.List;

public class AdminInventoryFragment extends Fragment {

    private FragmentAdminInventoryBinding binding;
    private EnderecoAdapter enderecoAdapter;
    private ProdutoAdapter produtoAdapter;

    // 📸 Scanner de Câmera para QR Code de Rua
    private final ActivityResultLauncher<ScanOptions> barcodeRua = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    String codigoLido = result.getContents().trim().toUpperCase();
                    binding.etSearchInventory.setText(codigoLido);
                    buscarRua();
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminInventoryBinding.inflate(inflater, container, false);

        // 1. Inicializa a RecyclerView e o EnderecoAdapter
        binding.rvInventoryManagement.setLayoutManager(new LinearLayoutManager(requireContext()));
        enderecoAdapter = new EnderecoAdapter(this::carregarDadosDaRua);
        binding.rvInventoryManagement.setAdapter(enderecoAdapter);

        // 2. Ação de buscar ao apertar Enter/Busca no teclado
        binding.etSearchInventory.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                buscarRua();
                return true;
            }
            return false;
        });

        // 3. Ação do Botão da Câmera
        binding.btnBarcodeScanner.setOnClickListener(v ->
                ScannerHelper.escanearEndereco(barcodeRua)
        );

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        // ⚡ Ao abrir/retornar à tela, carrega todas as ruas do banco
        carregarTodasAsRuas();
    }

    /**
     * 🏛️ Carrega todas as ruas cadastradas no Room Database
     */
    private void carregarTodasAsRuas() {
        if (getContext() == null) return;
        Context appContext = requireContext().getApplicationContext();

        new Thread(() -> {
            List<Endereco> listaRuas = AppDatabase.getInstance(appContext)
                    .enderecoDao()
                    .listarTodos();

            if (isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    binding.cardInventoryDetails.setVisibility(View.GONE);
                    binding.rvInventoryManagement.setAdapter(enderecoAdapter);
                    enderecoAdapter.setListaEnderecos(listaRuas);

                    if (listaRuas.isEmpty()) {
                        Toast.makeText(appContext, "Nenhuma rua cadastrada.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    /**
     * 🔍 Pesquisa exata de rua
     */
    private void buscarRua() {
        if (binding == null || binding.etSearchInventory == null) return;

        String termoRua = binding.etSearchInventory.getText().toString().trim().toUpperCase();

        if (termoRua.isEmpty()) {
            carregarTodasAsRuas();
            return;
        }

        if (getContext() == null) return;
        Context appContext = requireContext().getApplicationContext();

        new Thread(() -> {
            Endereco enderecoExistente = AppDatabase.getInstance(appContext)
                    .enderecoDao()
                    .buscarRuaExata(termoRua);

            if (isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (enderecoExistente != null) {
                        carregarDadosDaRua(enderecoExistente);
                    } else {
                        solicitarCriacaoDeRua(termoRua);
                    }
                });
            }
        }).start();
    }

    /**
     * ➕ Diálogo para criar a rua se ela não existir
     */
    private void solicitarCriacaoDeRua(String nomeNovaRua) {
        if (getContext() == null) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("Rua não encontrada")
                .setMessage("A rua '" + nomeNovaRua + "' não existe. Deseja cadastrá-la agora?")
                .setPositiveButton("Criar Rua", (dialog, which) -> cadastrarNovaRua(nomeNovaRua))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void cadastrarNovaRua(String nomeRua) {
        if (getContext() == null) return;
        Context appContext = requireContext().getApplicationContext();

        new Thread(() -> {
            Endereco novoEndereco = new Endereco(nomeRua);
            long idInserido = AppDatabase.getInstance(appContext)
                    .enderecoDao()
                    .inserir(novoEndereco);

            if (isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (idInserido > 0) {
                        Toast.makeText(appContext, "Rua criada com sucesso!", Toast.LENGTH_SHORT).show();
                        novoEndereco.setId((int) idInserido);
                        carregarDadosDaRua(novoEndereco);
                    } else {
                        Toast.makeText(appContext, "Erro ao criar rua.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    /**
     * 📦 Abre os detalhes e lista os produtos vinculados à rua selecionada
     */
    private void carregarDadosDaRua(Endereco endereco) {
        if (binding == null || getContext() == null) return;
        Context appContext = requireContext().getApplicationContext();

        binding.tvSelectedStreetHeader.setText("Rua: " + endereco.getDescricao());
        binding.cardInventoryDetails.setVisibility(View.VISIBLE);

        if (produtoAdapter == null) {
            produtoAdapter = new ProdutoAdapter();
        }
        binding.rvInventoryManagement.setAdapter(produtoAdapter);

        new Thread(() -> {
            List<Produto> produtosDaRua = AppDatabase.getInstance(appContext)
                    .enderecoDao()
                    .buscarProdutosPorEndereco(endereco.getId());

            if (isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    produtoAdapter.setListaProdutos(produtosDaRua);

                    if (produtosDaRua.isEmpty()) {
                        Toast.makeText(appContext, "Nenhum produto vinculado a esta rua.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}