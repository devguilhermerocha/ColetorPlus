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
import com.application.coletorplus.data.model.DashboardAlerta;
import com.application.coletorplus.data.model.Produto;
import com.application.coletorplus.data.dao.ValidadeDao;
import com.application.coletorplus.databinding.FragmentAdminDashboardBinding;
import com.application.coletorplus.ui.adapter.AlertaDashboardAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminDashboardFragment extends Fragment {

    private FragmentAdminDashboardBinding binding;
    private AlertaDashboardAdapter adapter;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new AlertaDashboardAdapter();
        binding.rvAlertasAdmin.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvAlertasAdmin.setAdapter(adapter);

        carregarDados();
    }

    private void carregarDados() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext().getApplicationContext());
            
            // 1. Contagem Geral (KPIs)
            int criticosCount = db.produtoDao().countProdutosEsgotados();
            
            Calendar cal30 = Calendar.getInstance();
            cal30.add(Calendar.DAY_OF_YEAR, 30);
            int vencendo30Count = db.validadeDao().countValidadesVencendo(cal30.getTimeInMillis());
            
            // 2. Inteligência de Alertas (Prioridade: Vencimento em 7 dias > Estoque Zero)
            List<DashboardAlerta> listaAlertas = new ArrayList<>();
            
            // A. Buscar vencimentos em 7 dias
            Calendar cal7 = Calendar.getInstance();
            cal7.add(Calendar.DAY_OF_YEAR, 7);
            List<ValidadeDao.ValidadeComProduto> v7 = db.validadeDao().getValidadesVencendoComProduto(cal7.getTimeInMillis());
            for (ValidadeDao.ValidadeComProduto vcp : v7) {
                String dataStr = dateFormat.format(new Date(vcp.validade.getDataVencimento()));
                listaAlertas.add(new DashboardAlerta(
                    vcp.produtoNome,
                    "Vence em " + dataStr + " (" + vcp.validade.getQuantidade() + " unid.)",
                    DashboardAlerta.TipoAlerta.VENCIMENTO,
                    1
                ));
            }
            
            // B. Buscar produtos sem estoque
            List<Produto> esgotados = db.produtoDao().getProdutosSemEstoque();
            for (Produto p : esgotados) {
                listaAlertas.add(new DashboardAlerta(
                    p.getNome(),
                    "Estoque totalmente esgotado no sistema.",
                    DashboardAlerta.TipoAlerta.ESTOQUE_ZERO,
                    2
                ));
            }

            // Ordenar por prioridade
            Collections.sort(listaAlertas, (a1, a2) -> Integer.compare(a1.getPrioridade(), a2.getPrioridade()));

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    binding.tvCountCriticos.setText(String.valueOf(criticosCount));
                    binding.tvCountValidades.setText(String.valueOf(vencendo30Count));
                    adapter.setAlertas(listaAlertas);
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
