package com.application.coletorplus.ui.produto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.application.coletorplus.data.database.AppDatabase;
import com.application.coletorplus.data.model.Produto;
import com.application.coletorplus.databinding.FragmentConsultaProdutoBinding;

public class ConsultaProdutoFragment extends Fragment {

    private FragmentConsultaProdutoBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentConsultaProdutoBinding.inflate(inflater, container, false);

        binding.btnScannerConsulta.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Scanner em desenvolvimento...", Toast.LENGTH_SHORT).show();
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}