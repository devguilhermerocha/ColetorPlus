package com.application.coletorplus.ui.reposicao;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.application.coletorplus.databinding.FragmentEntradaBinding;
import com.application.coletorplus.ui.scanner.ScannerHelper;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class EntradaFragment extends Fragment {

    private FragmentEntradaBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEntradaBinding.inflate(inflater, container, false);

        binding.btnConfirmarEntrada.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Endereçamento realizado!", Toast.LENGTH_SHORT).show();
        });

        binding.btnScannerProdutoEntrada.setOnClickListener(v -> {
            ScannerHelper.escanearProduto(barcodeProduto);
        });

        binding.btnScannerRua.setOnClickListener(v ->{
            ScannerHelper.escanearProduto(barcodeRua);
        });

        return binding.getRoot();
    }

    private final ActivityResultLauncher<ScanOptions> barcodeProduto= registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    String codigoLido = result.getContents();
                    binding.etCodigoProdutoEntrada.setText(codigoLido);
                } else {
                    Toast.makeText(requireContext(), "Leitura cancelada", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private final ActivityResultLauncher<ScanOptions> barcodeRua = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    String codigoLido = result.getContents();
                    binding.etCodigoRua.setText(codigoLido);
                } else {
                    Toast.makeText(requireContext(), "Leitura cancelada", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}