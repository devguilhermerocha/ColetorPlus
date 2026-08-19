package com.application.pistalimpa.ui.produto;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

            Context appContext = requireContext().getApplicationContext();

            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(appContext);

                // 1. Valida se o EAN já existe
                if (!ean.isEmpty()) {
                    Produto produtoExistente = db.produtoDao().buscarPorEan(ean);

                    if (produtoExistente != null) {
                        if (isAdded() && getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                etEan.setError("Este produto já está cadastrado no sistema!");
                                Toast.makeText(appContext, "Produto já existe!", Toast.LENGTH_LONG).show();
                            });
                        }
                        return;
                    }
                }

                try {
                    Produto novoProduto = new Produto(nome, ean, isCritico, true);
                    db.produtoDao().inserir(novoProduto);

                    if (isAdded() && getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(appContext, "Produto cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                            notificarEFechar();
                        });
                    }
                } catch (Exception e) {
                    if (isAdded() && getActivity() != null) {
                        getActivity().runOnUiThread(() ->
                                Toast.makeText(appContext, "Erro ao cadastrar produto!", Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            }).start();
        });
    }

    private void notificarEFechar() {
        if (!isAdded()) return;

        Bundle result = new Bundle();
        result.putBoolean("produto_cadastrado", true);

        getParentFragmentManager().setFragmentResult("chave_novo_produto", result);

        requireActivity().getSupportFragmentManager().setFragmentResult("chave_novo_produto", result);

        dismiss();
    }
}