package com.application.coletorplus.ui.produto;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.application.coletorplus.R;
import com.application.coletorplus.data.database.AppDatabase;
import com.application.coletorplus.data.model.Produto;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

public class NovoProdutoFragment extends BottomSheetDialogFragment {

    // 🔑 Chave para passar o EAN via Bundle
    private static final String ARG_EAN = "arg_codigo_ean";

    public NovoProdutoFragment() {
    }

    // 1️⃣ Mantém o construtor padrão sem parâmetros
    public static NovoProdutoFragment newInstance() {
        return new NovoProdutoFragment();
    }

    // 2️⃣ NOVO: Construtor que recebe o EAN e salva no Bundle
    public static NovoProdutoFragment newInstance(String ean) {
        NovoProdutoFragment fragment = new NovoProdutoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EAN, ean);
        fragment.setArguments(args);
        return fragment;
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

        // 📥 Preenche o EAN automaticamente se ele foi passado pelo alerta
        if (getArguments() != null && getArguments().containsKey(ARG_EAN)) {
            String eanRecebido = getArguments().getString(ARG_EAN);
            if (etEan != null && eanRecebido != null) {
                etEan.setText(eanRecebido);
            }
        }

        btnSalvar.setOnClickListener(v -> {
            String nome = etNome.getText() != null ? etNome.getText().toString().trim() : "";
            String ean = etEan.getText() != null ? etEan.getText().toString().trim() : "";
            boolean isCritico = false; // Defina como preferir ou via CheckBox

            if (nome.isEmpty()) {
                etNome.setError("Informe o nome do produto");
                return;
            }

            Context appContext = requireContext().getApplicationContext();

            // 🔍 1. Primeiro verifica em Thread se o EAN já existe
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(appContext);

                if (!ean.isEmpty()) {
                    Produto produtoExistente = db.produtoDao().buscarPorEan(ean);

                    if (produtoExistente != null) {
                        if (isAdded() && getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                etEan.setError("Este produto já está cadastrado no sistema!");
                                Toast.makeText(appContext, "Produto já existe!", Toast.LENGTH_LONG).show();
                            });
                        }
                        return; // Interrompe se já existir
                    }
                }

                // 💬 2. Se o EAN não existe, abre o Alerta na Interface Principal (UI Thread)
                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        exibirAlertaAdicionarNaReposicao(nome, ean, isCritico);
                    });
                }
            }).start();
        });
    }

    private void exibirAlertaAdicionarNaReposicao(String nome, String ean, boolean isCritico) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Adicionar à Lista?")
                .setMessage("Deseja adicionar o produto \"" + nome + "\" na lista de reposição agora?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    salvarProdutoNoBanco(nome, ean, isCritico, false);
                })
                .setNegativeButton("Não", (dialog, which) -> {
                    salvarProdutoNoBanco(nome, ean, isCritico, true);
                })
                .setCancelable(false)
                .show();
    }

    private void salvarProdutoNoBanco(String nome, String ean, boolean isCritico, boolean isReposto) {
        Context appContext = requireContext().getApplicationContext();

        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(appContext);

                // Instancia o produto definindo se ele vai pra reposição (false) ou não (true)
                Produto novoProduto = new Produto(nome, ean, isCritico, isReposto);
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