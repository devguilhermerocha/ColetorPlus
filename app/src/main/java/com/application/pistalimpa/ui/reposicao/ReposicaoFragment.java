package com.application.pistalimpa.ui.reposicao;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.pistalimpa.R;
import com.application.pistalimpa.data.database.AppDatabase;
import com.application.pistalimpa.data.model.Produto;
import com.application.pistalimpa.ui.produto.ProdutoAdapter;

import java.util.List;

public class ReposicaoFragment extends Fragment {

    private EditText etPesquisa;
    private ImageButton btnAbrirCamera;
    private RecyclerView rvProdutos;
    private ProdutoAdapter adapter;

    public ReposicaoFragment() {
    }

    public static ReposicaoFragment newInstance() {
        return new ReposicaoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reposicao, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etPesquisa = view.findViewById(R.id.etCodigoOuNome);
        btnAbrirCamera = view.findViewById(R.id.btnAbrirCamera);
        rvProdutos = view.findViewById(R.id.rvProdutos);

        rvProdutos.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new ProdutoAdapter((produto, isReposto) -> {
            produto.setReposto(isReposto);
            AppDatabase.getInstance(requireContext()).produtoDao().atualizar(produto);
            carregarProdutosDoBanco();
        });

        rvProdutos.setAdapter(adapter);

        if (etPesquisa != null) {
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
        }

        if (btnAbrirCamera != null) {
            btnAbrirCamera.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "Abrindo leitor de código de barras...", Toast.LENGTH_SHORT).show();
            });
        }

        getParentFragmentManager().setFragmentResultListener(
                "chave_novo_produto",
                getViewLifecycleOwner(),
                (requestKey, result) -> {
                    boolean atualizou = result.getBoolean("produto_cadastrado", false);
                    if (atualizou) {
                        carregarProdutosDoBanco();
                    }
                }
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        carregarProdutosDoBanco();
    }

    public void carregarProdutosDoBanco() {
        List<Produto> lista = AppDatabase.getInstance(requireContext())
                .produtoDao()
                .getProdutosPendentes();

        if (adapter != null) {
            adapter.setListaProdutos(lista);
        }
    }
}