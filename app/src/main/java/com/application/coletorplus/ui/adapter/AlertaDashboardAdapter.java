package com.application.coletorplus.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.coletorplus.R;
import com.application.coletorplus.ui.admin.DashboardAlerta;

import java.util.ArrayList;
import java.util.List;

public class AlertaDashboardAdapter extends RecyclerView.Adapter<AlertaDashboardAdapter.ViewHolder> {

    private List<DashboardAlerta> alertas = new ArrayList<>();

    public void setAlertas(List<DashboardAlerta> alertas) {
        this.alertas = alertas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dashboard_alerta, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DashboardAlerta alerta = alertas.get(position);
        holder.tvTitulo.setText(alerta.getTitulo());
        holder.tvSubtitulo.setText(alerta.getSubtitulo());

        if (alerta.getTipo() == DashboardAlerta.TipoAlerta.VENCIMENTO) {
            holder.ivIcon.setImageResource(R.drawable.etiqueta);
            holder.ivIcon.setColorFilter(0xFFF44336); // Vermelho
            holder.tvTag.setText("VENCIMENTO");
            holder.tvTag.setBackgroundColor(0xFFFF9800); // Laranja
        } else {
            holder.ivIcon.setImageResource(R.drawable.remover);
            holder.ivIcon.setColorFilter(0xFF757575); // Cinza
            holder.tvTag.setText("ESTOQUE ZERO");
            holder.tvTag.setBackgroundColor(0xFF616161); // Cinza Escuro
        }
    }

    @Override
    public int getItemCount() {
        return alertas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvSubtitulo, tvTag;
        ImageView ivIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTituloAlerta);
            tvSubtitulo = itemView.findViewById(R.id.tvSubtituloAlerta);
            tvTag = itemView.findViewById(R.id.tvTagTipoAlerta);
            ivIcon = itemView.findViewById(R.id.ivIconAlerta);
        }
    }
}
