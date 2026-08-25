package com.application.coletorplus.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.application.coletorplus.databinding.FragmentConsultaProdutoBinding;
import com.application.coletorplus.ui.scanner.ScannerHelper;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class ConsultaProdutoFragment extends Fragment {

    private FragmentConsultaProdutoBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentConsultaProdutoBinding.inflate(inflater, container, false);

        binding.btnScannerConsulta.setOnClickListener(v -> {
            ScannerHelper.escanearProduto(barcodeLauncher);
        });

        return binding.getRoot();
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    String codigoLido = result.getContents();
                    binding.etBuscaGenerica.setText(codigoLido);
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