package com.application.coletorplus.ui.user;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.application.coletorplus.data.database.AppDatabase;
import com.application.coletorplus.data.model.Auditoria;
import com.application.coletorplus.data.model.Produto;
import com.application.coletorplus.data.model.Validade;
import com.application.coletorplus.data.session.SessionManager;
import com.application.coletorplus.databinding.FragmentAjusteBinding;
import com.application.coletorplus.ui.scanner.ScannerHelper;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AjusteFragment extends Fragment {

    private FragmentAjusteBinding binding;
    private Produto produtoAtual;
    private List<Validade> lotesAtuais = new ArrayList<>();
    private long selectedValidadeTimestamp = 0;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private final Handler handlerHold = new Handler(Looper.getMainLooper());
    private Runnable runnableHold;
    private boolean isHoldCompleted = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAjusteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tilCodigoAvaria.setEndIconOnClickListener(v -> escanear());

        binding.etCodigoAvaria.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                buscarProduto(binding.etCodigoAvaria.getText().toString().trim().toUpperCase());
                return true;
            }
            return false;
        });

        binding.etCodigoAvaria.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    limparInformacoes();
                }
            }
        });

        // Configuração do Dropdown de Validade
        binding.etDataLoteAvaria.setOnItemClickListener((parent, view1, position, id) -> {
            if (position < lotesAtuais.size()) {
                selectedValidadeTimestamp = lotesAtuais.get(position).getDataVencimento();
            }
        });

        // ⏱️ Configura a mecânica de segurar por 3 segundos (Obrigatório)
        setupRemovalHoldButton();
    }

    private void escanear() {
        ScannerHelper.escanearProduto(barcodeProduto);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupRemovalHoldButton() {
        runnableHold = () -> {
            isHoldCompleted = true;
            confirmarBaixaAvaria();
        };

        binding.btnConfirmarAvaria.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isHoldCompleted = false;
                    handlerHold.postDelayed(runnableHold, 3000);
                    v.setPressed(true);
                    return true;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    handlerHold.removeCallbacks(runnableHold);
                    v.setPressed(false);

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        v.performClick();
                        if (!isHoldCompleted) {
                            if (selectedValidadeTimestamp == 0) {
                                Toast.makeText(getContext(), "Selecione o lote primeiro!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    return true;
            }
            return false;
        });
        
        // Listener vazio apenas para habilitar o feedback visual de clique/press do MaterialButton
        binding.btnConfirmarAvaria.setOnClickListener(v -> {});
    }

    private void buscarProduto(String ean) {
        if (ean == null || ean.trim().isEmpty() || getContext() == null) return;
        final String eanUpper = ean.trim().toUpperCase();

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            Produto p = db.produtoDao().buscarPorEan(eanUpper);

            if (p != null) {
                produtoAtual = p;
                List<Validade> validades = db.validadeDao().buscarPorProduto(p.getId());

                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        produtoAtual = p;
                        lotesAtuais = validades;

                        binding.cardInfoAvaria.setVisibility(View.VISIBLE);
                        binding.tvNomeInfoAvaria.setText(p.getNome());
                        binding.tvEstoqueInfoAvaria.setText(String.format(Locale.getDefault(), "Estoque Total: %d", p.getQuantidadeTotal()));

                        popularDropdownValidades(validades);
                        binding.etQuantidadeAvaria.requestFocus();
                    });
                }
            } else {
                produtoAtual = null;
                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        binding.cardInfoAvaria.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Produto não encontrado", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }

    private void popularDropdownValidades(List<Validade> validades) {
        List<String> datasFormatadas = new ArrayList<>();
        for (Validade v : validades) {
            datasFormatadas.add(dateFormat.format(v.getDataVencimento()) + " (" + v.getQuantidade() + " unid.)");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, datasFormatadas);
        binding.etDataLoteAvaria.setAdapter(adapter);

        binding.etDataLoteAvaria.setText("", false);
        selectedValidadeTimestamp = 0;
    }

    private void confirmarBaixaAvaria() {
        if (produtoAtual == null) {
            Toast.makeText(getContext(), "Busque um produto primeiro", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedValidadeTimestamp == 0) {
            Toast.makeText(getContext(), "Selecione o lote (data de validade)", Toast.LENGTH_SHORT).show();
            return;
        }

        String qtdStr = binding.etQuantidadeAvaria.getText().toString().trim();
        if (qtdStr.isEmpty()) {
            Toast.makeText(getContext(), "Informe a quantidade", Toast.LENGTH_SHORT).show();
            return;
        }

        int qtdARemover = Integer.parseInt(qtdStr);
        if (qtdARemover <= 0) {
            Toast.makeText(getContext(), "Quantidade inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());

            // Como a data é obrigatória, buscamos e descontamos do lote específico
            List<Validade> lotes = db.validadeDao().buscarPorProduto(produtoAtual.getId());
            Validade targetBatch = null;
            for (Validade v : lotes) {
                if (v.getDataVencimento() == selectedValidadeTimestamp) {
                    targetBatch = v;
                    break;
                }
            }

            if (targetBatch != null) {
                int novaQtdLote = targetBatch.getQuantidade() - qtdARemover;
                // Se o lote acabar, removemos o registro da validade, mas NUNCA o produto do catálogo.
                if (novaQtdLote <= 0) {
                    db.validadeDao().deletar(targetBatch);
                } else {
                    targetBatch.setQuantidade(novaQtdLote);
                    db.validadeDao().inserirValidade(targetBatch);
                }
            } else {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Lote não encontrado", Toast.LENGTH_LONG).show());
                }
                return;
            }

            atualizarEstoqueTotalERefrescarUI(db);

            // REGISTRO DE AUDITORIA
            String nomeUsuario = SessionManager.getInstance().getUsuarioLogado() != null 
                    ? SessionManager.getInstance().getUsuarioLogado().getNome() : "Desconhecido";
            String logDesc = "Baixa de " + qtdARemover + " unid. do produto " + produtoAtual.getNome();
            db.auditoriaDao().inserir(new Auditoria(nomeUsuario, "AVARIA", logDesc, System.currentTimeMillis()));
        }).start();
    }

    private void atualizarEstoqueTotalERefrescarUI(AppDatabase db) {
        int novoTotal = db.validadeDao().getSomaQuantidades(produtoAtual.getId());
        produtoAtual.setQuantidadeTotal(novoTotal);
        db.produtoDao().atualizar(produtoAtual);

        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "Baixa efetuada com sucesso!", Toast.LENGTH_SHORT).show();
                limparCampos();
                buscarProduto(produtoAtual.getCodigoEan());
            });
        }
    }

    private void limparInformacoes() {
        produtoAtual = null;
        lotesAtuais.clear();
        if (binding != null) {
            binding.cardInfoAvaria.setVisibility(View.GONE);
            limparCampos();
        }
    }

    private void limparCampos() {
        if (binding != null) {
            binding.etQuantidadeAvaria.setText("");
            binding.etDataLoteAvaria.setText("", false);
            binding.etMotivoAvaria.setText("");
            selectedValidadeTimestamp = 0;
        }
    }

    private final ActivityResultLauncher<ScanOptions> barcodeProduto = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    String codigoLido = result.getContents().trim().toUpperCase();
                    binding.etCodigoAvaria.setText(codigoLido);
                    buscarProduto(codigoLido);
                }
            }
    );

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handlerHold.removeCallbacks(runnableHold);
        binding = null;
    }
}