package com.application.coletorplus.ui.reposicao;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.application.coletorplus.databinding.FragmentEntradaBinding;

public class EntradaFragment extends Fragment {

    private FragmentEntradaBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEntradaBinding.inflate(inflater, container, false);

        binding.btnConfirmarEntrada.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Endereçamento realizado!", Toast.LENGTH_SHORT).show();
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}