package com.application.coletorplus.ui.user;

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
import androidx.fragment.app.Fragment;

import com.application.coletorplus.data.database.AppDatabase;
import com.application.coletorplus.data.model.Endereco;
import com.application.coletorplus.data.model.Produto;
import com.application.coletorplus.databinding.FragmentConsultaProdutoBinding;
import com.application.coletorplus.ui.scanner.ScannerHelper;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.List;

public class ConsultaProdutoFragment extends Fragment {

    private FragmentConsultaProdutoBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentConsultaProdutoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnScannerConsulta.setOnClickListener(v -> {
            ScannerHelper.escanearProduto(barcodeLauncher);
        });

        binding.etBuscaGenerica.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                realizarConsulta(binding.etBuscaGenerica.getText().toString().toUpperCase());
                return true;
            }
            return false;
        });

        binding.etBuscaGenerica.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(android.text.Editable s) {
                if (s.length() == 0) {
                    binding.cardDetalhesProduto.setVisibility(View.GONE);
                }
            }
        });
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    String codigoLido = result.getContents().trim().toUpperCase();
                    binding.etBuscaGenerica.setText(codigoLido);
                    realizarConsulta(codigoLido);
                } else {
                    Toast.makeText(requireContext(), "Leitura cancelada", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private void realizarConsulta(String termo) {
        if (termo == null || termo.trim().isEmpty() || getContext() == null) return;

        String busca = termo.trim().toUpperCase();
        Context context = getContext().getApplicationContext();
        
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                Toast.makeText(context, "Buscando: " + busca, Toast.LENGTH_SHORT).show();
            });
        }
        
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            Produto p = db.produtoDao().buscarPorEan(busca);
            
            // Se não achar por EAN exato, tenta por nome
            if (p == null) {
                List<Produto> porNome = db.produtoDao().buscarPorTermo(busca);
                if (!porNome.isEmpty()) {
                    p = porNome.get(0); 
                }
            }

            final Produto finalProduto = p;
            if (finalProduto != null) {
                List<Endereco> enderecos = db.enderecoDao().buscarEnderecosPorProduto(finalProduto.getId());
                exibirDetalhes(finalProduto, enderecos);
            } else {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (binding != null) {
                            binding.cardDetalhesProduto.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Produto não encontrado", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void exibirDetalhes(Produto p, List<Endereco> enderecos) {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            if (binding == null) return;
            
            binding.cardDetalhesProduto.setVisibility(View.VISIBLE);
            binding.tvNomeConsulta.setText(p.getNome());
            binding.tvEanConsulta.setText("EAN: " + p.getCodigoEan());
            binding.tvQuantidadeConsulta.setText("Quantidade Total: " + p.getQuantidadeTotal());

            if (enderecos != null && !enderecos.isEmpty()) {
                StringBuilder sb = new StringBuilder("Endereços: ");
                for (int i = 0; i < enderecos.size(); i++) {
                    sb.append(enderecos.get(i).getDescricao());
                    if (i < enderecos.size() - 1) {
                        sb.append(", ");
                    }
                }
                binding.tvEnderecoConsulta.setText(sb.toString());
            } else {
                binding.tvEnderecoConsulta.setText("Endereço: Não Vinculado");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}