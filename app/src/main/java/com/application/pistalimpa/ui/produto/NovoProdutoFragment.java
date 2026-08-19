package com.application.pistalimpa.ui.produto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.application.pistalimpa.R;
import com.application.pistalimpa.data.database.AppDatabase;
import com.application.pistalimpa.data.model.Produto;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

public class NovoProdutoFragment extends BottomSheetDialogFragment {

    public NovoProdutoFragment() {
        // Construtor público vazio
    }

    public static NovoProdutoFragment newInstance() {
        return new NovoProdutoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_novo_produto, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputEditText etNome = view.findViewById(R.id.etNomeProduto);
        TextInputEditText etEan = view.findViewById(R.id.etCodigoEan);
        Button btnSalvar = view.findViewById(R.id.btnSalvarProduto);

        btnSalvar.setOnClickListener(v -> {
            String nome = etNome.getText() != null ? etNome.getText().toString().trim() : "";
            String ean = etEan.getText() != null ? etEan.getText().toString().trim() : "";
            boolean isCritico = false;

            if (nome.isEmpty()) {
                etNome.setError("Informe o nome do produto");
                return;
            }

            // Executa a validação e inserção em Thread secundária
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(requireContext());

                // 1. Verifica se o EAN já existe no banco
                if (!ean.isEmpty()) {
                    Produto produtoExistente = db.produtoDao().buscarPorEan(ean);

                    if (produtoExistente != null) {
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() -> {
                                etEan.setError("Este produto já está cadastrado no sistema!");
                                Toast.makeText(requireContext(), "Produto já existe! Use a busca para reativá-lo.", Toast.LENGTH_LONG).show();
                            });
                        }
                        return; // Aborta a operação de cadastro
                    }
                }

                // 2. Se o produto realmente não existe, cadastra como NOVO
                try {
                    Produto novoProduto = new Produto(nome, ean, isCritico, false);
                    db.produtoDao().inserir(novoProduto);

                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Produto cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                            notificarEFechar();
                        });
                    }
                } catch (Exception e) {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> etEan.setError("Erro ao cadastrar produto!"));
                    }
                }
            }).start();
        });
    }

    private void notificarEFechar() {
        Bundle result = new Bundle();
        result.putBoolean("produto_cadastrado", true);
        getParentFragmentManager().setFragmentResult("chave_novo_produto", result);
        dismiss();
    }
}