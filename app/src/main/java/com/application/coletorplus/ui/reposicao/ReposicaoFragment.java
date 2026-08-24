package com.application.coletorplus.ui.reposicao;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.coletorplus.ui.scanner.CaptureActivityPortrait;
import com.application.coletorplus.R;
import com.application.coletorplus.data.database.AppDatabase;
import com.application.coletorplus.data.model.Produto;
import com.application.coletorplus.ui.produto.NovoProdutoFragment;
import com.application.coletorplus.ui.produto.ProdutoAdapter;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.List;

public class ReposicaoFragment extends Fragment {

    private EditText etPesquisa;
    private CheckBox cbCritico;
    private Button btnAdicionar;
    private ImageButton btnAbrirCamera;
    private RecyclerView rvProdutos;
    private ProdutoAdapter adapter;
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    String eanLido = result.getContents();
                    etPesquisa.setText(eanLido);
                    buscarEAdicionarPorEan(eanLido);
                } else {
                    Toast.makeText(requireContext(), "Leitura cancelada", Toast.LENGTH_SHORT).show();
                }
            }
    );

    public ReposicaoFragment() {
    }

    public static ReposicaoFragment newInstance() {
        return new ReposicaoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reposicao, container, false);

        etPesquisa = view.findViewById(R.id.etCodigoOuNome);
        cbCritico = view.findViewById(R.id.cbCritico);
        btnAdicionar = view.findViewById(R.id.btnAdicionar);
        btnAbrirCamera = view.findViewById(R.id.btnAbrirCamera);
        rvProdutos = view.findViewById(R.id.rvProdutos);

        rvProdutos.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Adapter configurado para marcar como reposto (true) ao clicar no item
        adapter = new ProdutoAdapter(produto -> marcarComoReposto(produto, true));
        rvProdutos.setAdapter(adapter);

        // Listener do Fragment Result (quando salva um novo produto no modal)
        getParentFragmentManager().setFragmentResultListener(
                "chave_novo_produto",
                getViewLifecycleOwner(),
                (requestKey, result) -> {
                    if (result.getBoolean("produto_cadastrado", false)) {
                        carregarProdutosPendentes();
                    }
                }
        );

        etPesquisa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filtrar(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 📷 Botão da Câmera
        if (btnAbrirCamera != null) {
            btnAbrirCamera.setOnClickListener(v -> abrirScannerCamera());
        }

        // ➕ Botão Adicionar (Corrigido para apenas 1 listener)
        if (btnAdicionar != null) {
            btnAdicionar.setOnClickListener(v -> {
                String ean = etPesquisa.getText() != null ? etPesquisa.getText().toString().trim() : "";
                if (ean.isEmpty()) {
                    Toast.makeText(requireContext(), "Digite para adicionar", Toast.LENGTH_SHORT).show();
                } else {
                    buscarEAdicionarPorEan(ean);
                }
            });
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        carregarProdutosPendentes();
    }

    private void abrirScannerCamera() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Aponte para o código de barras (EAN)");
        options.setCameraId(0);
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(false);
        options.setOrientationLocked(false);
        options.setCaptureActivity(CaptureActivityPortrait.class);

        barcodeLauncher.launch(options);
    }

    // 🔍 Busca no banco e decide: Adiciona na lista ou abre Alerta de Cadastro
    private void buscarEAdicionarPorEan(String ean) {
        if (getContext() == null) return;
        Context appContext = getContext().getApplicationContext();

        new Thread(() -> {
            Produto produto = AppDatabase.getInstance(appContext).produtoDao().buscarPorEan(ean);

            if (isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (produto != null) {
                        marcarComoReposto(produto, false); // Coloca na lista de pendentes (isReposto = false)
                        etPesquisa.setText("");
                        Toast.makeText(appContext, "Produto " + produto.getNome() + " adicionado à lista!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Exibe alerta caso o produto não exista no banco
                        exibirAlertaCadastroProduto(ean);
                    }
                });
            }
        }).start();
    }

    private void exibirAlertaCadastroProduto(String ean) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Produto não encontrado")
                .setMessage("O código de barras " + ean + " não está cadastrado. Deseja cadastrar este produto agora?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    NovoProdutoFragment modal = NovoProdutoFragment.newInstance(ean);
                    modal.show(getChildFragmentManager(), "NovoProdutoModal");
                })
                .setNegativeButton("Não", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    private void marcarComoReposto(Produto produto, boolean isReposto) {
        if (getContext() == null) return;
        Context appContext = getContext().getApplicationContext();

        new Thread(() -> {
            produto.setReposto(isReposto);
            AppDatabase.getInstance(appContext).produtoDao().atualizar(produto);
            carregarProdutosPendentes();
        }).start();
    }

    private void carregarProdutosPendentes() {
        if (getContext() == null) return;
        Context appContext = getContext().getApplicationContext();

        new Thread(() -> {
            List<Produto> listaPendentes = AppDatabase.getInstance(appContext)
                    .produtoDao()
                    .getProdutosPendentes();

            if (isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(() -> adapter.setListaProdutos(listaPendentes));
            }
        }).start();
    }
}