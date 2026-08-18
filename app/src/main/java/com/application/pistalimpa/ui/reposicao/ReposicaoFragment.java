package com.application.pistalimpa.ui.reposicao;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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

    private EditText etCodigoOuNome;
    private CheckBox cbCritico;
    private Button btnAdicionar;
    private ImageButton btnAbrirCamera;
    private RecyclerView rvProdutos;
    private ProdutoAdapter adapter;

    public ReposicaoFragment() {
        // Construtor público vazio obrigatório
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

        // 1. Mapeamento dos componentes com os IDs exatos do XML
        etCodigoOuNome = view.findViewById(R.id.etCodigoOuNome);
        cbCritico = view.findViewById(R.id.cbCritico);
        btnAdicionar = view.findViewById(R.id.btnAdicionar);
        btnAbrirCamera = view.findViewById(R.id.btnAbrirCamera);
        rvProdutos = view.findViewById(R.id.rvProdutos);

        // 2. Configura a RecyclerView
        rvProdutos.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new ProdutoAdapter((produto, isReposto) -> {
            // Quando o repositor marcar o CheckBox no item da lista
            produto.setReposto(isReposto);
            AppDatabase.getInstance(requireContext()).produtoDao().atualizar(produto);

            // Recarrega os dados do banco para atualizar a lista na tela
            carregarProdutosDoBanco();
        });

        rvProdutos.setAdapter(adapter);

        // 3. Ação do Botão "Adicionar" (Salvar direto no Room)
        btnAdicionar.setOnClickListener(v -> adicionarProduto());

        // 4. Ação do Botão da Câmera (Para leitor de código de barras futuro)
        btnAbrirCamera.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Abrindo leitor de código de barras...", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Busca os produtos do banco sempre que a tela é exibida
        carregarProdutosDoBanco();
    }

    private void adicionarProduto() {
        String entradaText = etCodigoOuNome.getText() != null ? etCodigoOuNome.getText().toString().trim() : "";
        boolean isCritico = cbCritico.isChecked();

        if (entradaText.isEmpty()) {
            etCodigoOuNome.setError("Digite o nome ou EAN do produto");
            return;
        }

        // Lógica para separar EAN numérico de Nome em texto
        String nomeProduto = entradaText;
        String eanProduto = "";

        if (entradaText.matches("\\d+")) {
            // Se forem só números, consideramos como EAN
            eanProduto = entradaText;
            nomeProduto = "Produto " + entradaText; // Nome temporário até consultar API/Base
        }

        // Cria o novo produto e salva no Room Database
        Produto novoProduto = new Produto(nomeProduto, eanProduto, isCritico, false);
        AppDatabase.getInstance(requireContext()).produtoDao().inserir(novoProduto);

        // Limpa os campos de digitação
        etCodigoOuNome.setText("");
        cbCritico.setChecked(false);

        Toast.makeText(requireContext(), "Produto adicionado!", Toast.LENGTH_SHORT).show();

        // Atualiza a lista exibida no app
        carregarProdutosDoBanco();
    }

    private void carregarProdutosDoBanco() {
        // Busca os produtos cadastrados no banco local
        List<Produto> lista = AppDatabase.getInstance(requireContext())
                .produtoDao()
                .getProdutosPendentes();

        adapter.setListaProdutos(lista);
    }
}