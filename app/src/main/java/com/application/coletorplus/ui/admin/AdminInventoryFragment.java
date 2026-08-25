package com.application.coletorplus.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.application.coletorplus.databinding.FragmentAdminInventoryBinding;

public class AdminInventoryFragment extends Fragment {

    private FragmentAdminInventoryBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminInventoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void buscarRua() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}