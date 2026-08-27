package com.application.coletorplus.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.application.coletorplus.data.database.AppDatabase;
import com.application.coletorplus.data.model.Auditoria;
import com.application.coletorplus.databinding.FragmentAdminAuditBinding;
import com.application.coletorplus.ui.adapter.AuditoriaAdapter;

import java.util.List;

public class AdminAuditFragment extends Fragment {

    private FragmentAdminAuditBinding binding;
    private AuditoriaAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminAuditBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new AuditoriaAdapter();
        binding.rvAuditoria.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvAuditoria.setAdapter(adapter);

        binding.btnGerarTesteAudit.setOnClickListener(v -> gerarLogsTeste());
        binding.btnLimparAudit.setOnClickListener(v -> confirmarLimpeza());

        carregarLogs();
    }

    private void gerarLogsTeste() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext().getApplicationContext());
            long now = System.currentTimeMillis();
            db.auditoriaDao().inserir(new Auditoria("Admin Teste", "ENTRADA", "Lote de teste (Entrada)", now));
            db.auditoriaDao().inserir(new Auditoria("Admin Teste", "SAÍDA", "Lote de teste (Saída)", now - 1000));
            db.auditoriaDao().inserir(new Auditoria("Admin Teste", "AVARIA", "Lote de teste (Avaria)", now - 2000));
            carregarLogs();
        }).start();
    }

    private void confirmarLimpeza() {
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Limpar Histórico")
                .setMessage("Deseja realmente apagar TODOS os logs de auditoria?")
                .setPositiveButton("Sim, Limpar", (dialog, which) -> {
                    new Thread(() -> {
                        AppDatabase.getInstance(requireContext().getApplicationContext()).auditoriaDao().deletarTudo();
                        carregarLogs();
                    }).start();
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void carregarLogs() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext().getApplicationContext());
            List<Auditoria> logs = db.auditoriaDao().listarTodas();
            
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    adapter.setLogs(logs);
                });
            }
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
