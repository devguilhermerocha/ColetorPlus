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
        // Construtor público vazio obrigatório
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
        CheckBox cbCritico = view.findViewById(R.id.cbCritico);
        Button btnSalvar = view.findViewById(R.id.btnSalvarProduto);

        btnSalvar.setOnClickListener(v -> {
            String nome = etNome.getText() != null ? etNome.getText().toString().trim() : "";
            String ean = etEan.getText() != null ? etEan.getText().toString().trim() : "";
            boolean isCritico = cbCritico.isChecked();

            if (nome.isEmpty()) {
                etNome.setError("Informe o nome do produto");
                return;
            }

            // Gravação no Room Database
            Produto produto = new Produto(nome, ean, isCritico, false);
            AppDatabase.getInstance(requireContext()).produtoDao().inserir(produto);

            Toast.makeText(requireContext(), "Produto cadastrado com sucesso!", Toast.LENGTH_SHORT).show();

            // Fecha o BottomSheet
            dismiss();
        });
    }
}