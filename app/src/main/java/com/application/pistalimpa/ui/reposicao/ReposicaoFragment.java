package com.application.pistalimpa.ui.reposicao;

import android.content.Context;
import android.os.Bundle;
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

import com.application.pistalimpa.CaptureActivityPortrait;
import com.application.pistalimpa.R;
import com.application.pistalimpa.data.database.AppDatabase;
import com.application.pistalimpa.data.model.Produto;
import com.application.pistalimpa.ui.produto.ProdutoAdapter;
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

    public ReposicaoFragment() {
    }

    public static ReposicaoFragment newInstance() {
        return new ReposicaoFragment();
    }

    // Dentro do ProdutoAdapter.java


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

        adapter = new ProdutoAdapter(produto -> marcarComoReposto(produto, true));
        rvProdutos.setAdapter(adapter);

        getParentFragmentManager().setFragmentResultListener(
                "chave_novo_produto",
                getViewLifecycleOwner(),
                (requestKey, result) -> {
                    if (result.getBoolean("produto_cadastrado", false)) {
                        carregarProdutosPendentes();
                    }
                }
        );

        if (btnAbrirCamera != null) {
            btnAbrirCamera.setOnClickListener(v -> abrirScannerCamera());
        }

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


        btnAdicionar.setOnClickListener(v -> {
            String ean = etPesquisa.getText() != null ? etPesquisa.getText().toString().trim() : "";

            if (ean.isEmpty()) {
                Toast.makeText(requireContext(), "Digite para adicionar", Toast.LENGTH_SHORT).show();
            } else {
                // Guardamos o ApplicationContext para garantir estabilidade na Thread
                Context appContext = requireContext().getApplicationContext();

                new Thread(() -> {
                    Produto produto = AppDatabase.getInstance(appContext).produtoDao().buscarPorEan(ean);

                    if (isAdded() && getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            if (produto != null) {
                                marcarComoReposto(produto, false);
                                etPesquisa.setText("");
                                Toast.makeText(appContext, "Produto adicionado à lista!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(appContext, "Produto não encontrado", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        carregarProdutosPendentes();
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    String eanLido = result.getContents();
                    // Coloca o EAN lido direto na caixa de texto
                    etPesquisa.setText(eanLido);
                    // Executa a busca do produto
                    buscarEAdicionarPorEan(eanLido);
                } else {
                    Toast.makeText(requireContext(), "Leitura cancelada", Toast.LENGTH_SHORT).show();
                }
            }
    );

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

    private void buscarEAdicionarPorEan(String ean) {
        if (getContext() == null) return;
        Context appContext = getContext().getApplicationContext();

        new Thread(() -> {
            Produto produto = AppDatabase.getInstance(appContext).produtoDao().buscarPorEan(ean);

            if (isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (produto != null) {
                        marcarComoReposto(produto, false); // Coloca isReposto = false para entrar na lista
                        etPesquisa.setText("");
                        Toast.makeText(appContext, "Produto " + produto.getNome() + " adicionado à lista!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(appContext, "Produto com EAN " + ean + " não cadastrado!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
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