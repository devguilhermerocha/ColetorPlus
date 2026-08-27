package com.application.coletorplus.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.coletorplus.R;
import com.application.coletorplus.data.model.Auditoria;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AuditoriaAdapter extends RecyclerView.Adapter<AuditoriaAdapter.ViewHolder> {

    private List<Auditoria> logs = new ArrayList<>();
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public void setLogs(List<Auditoria> logs) {
        this.logs = logs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_auditoria, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Auditoria log = logs.get(position);
        holder.tvUsuario.setText(log.getUsuarioNome());
        holder.tvAcao.setText(log.getTipoAcao());
        holder.tvDescricao.setText(log.getDescricao());
        holder.tvData.setText(dateTimeFormat.format(new Date(log.getTimestamp())));
        
        // Colorir o tipo de ação para facilitar a leitura
        switch (log.getTipoAcao()) {
            case "ENTRADA": holder.tvAcao.setTextColor(0xFF4CAF50); break; // Verde
            case "SAÍDA": holder.tvAcao.setTextColor(0xFF2196F3); break;   // Azul
            case "AVARIA": 
            case "ZERAMENTO": holder.tvAcao.setTextColor(0xFFD32F2F); break; // Vermelho
        }
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsuario, tvAcao, tvDescricao, tvData;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsuario = itemView.findViewById(R.id.tvLogUsuario);
            tvAcao = itemView.findViewById(R.id.tvLogAcao);
            tvDescricao = itemView.findViewById(R.id.tvLogDescricao);
            tvData = itemView.findViewById(R.id.tvLogData);
        }
    }
}
