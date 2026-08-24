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

import com.application.coletorplus.databinding.FragmentAjusteBinding;
import com.application.coletorplus.ui.scanner.ScannerHelper;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class AjusteFragment extends Fragment {

    private FragmentAjusteBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAjusteBinding.inflate(inflater, container, false);

        binding.btnConfirmarAvaria.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Avaria registrada com sucesso!", Toast.LENGTH_SHORT).show();
        });

        binding.btnScannerAvaria.setOnClickListener(v -> {
            ScannerHelper.escanearProduto(barcodeProduto);
        });

        return binding.getRoot();
    }

    private final ActivityResultLauncher<ScanOptions> barcodeProduto = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    String codigoLido = result.getContents();
                    binding.etCodigoAvaria.setText(codigoLido);
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