package com.application.coletorplus.ui.reposicao;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.application.coletorplus.databinding.FragmentAjusteBinding;

public class AjusteFragment extends Fragment {

    private FragmentAjusteBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAjusteBinding.inflate(inflater, container, false);

        binding.btnConfirmarAvaria.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Avaria registrada com sucesso!", Toast.LENGTH_SHORT).show();
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}