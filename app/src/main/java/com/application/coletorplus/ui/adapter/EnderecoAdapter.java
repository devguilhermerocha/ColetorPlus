package com.application.coletorplus.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.coletorplus.data.model.Endereco;

import java.util.ArrayList;
import java.util.List;

public class EnderecoAdapter extends RecyclerView.Adapter<EnderecoAdapter.EnderecoViewHolder> {

    private List<Endereco> listaEnderecos = new ArrayList<>();
    private final OnEnderecoClickListener listener;

    public interface OnEnderecoClickListener {
        void onEnderecoClick(Endereco endereco);
    }

    public EnderecoAdapter(OnEnderecoClickListener listener) {
        this.listener = listener;
    }

    public void setListaEnderecos(List<Endereco> enderecos) {
        this.listaEnderecos = enderecos != null ? enderecos : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EnderecoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new EnderecoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EnderecoViewHolder holder, int position) {
        Endereco endereco = listaEnderecos.get(position);
        holder.tvDescricao.setText(endereco.getDescricao());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEnderecoClick(endereco);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaEnderecos.size();
    }

    static class EnderecoViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescricao;

        public EnderecoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescricao = itemView.findViewById(android.R.id.text1);
        }
    }
}