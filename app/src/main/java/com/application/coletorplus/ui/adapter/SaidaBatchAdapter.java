package com.application.coletorplus.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.coletorplus.R;
import com.application.coletorplus.data.model.Produto;

import java.util.ArrayList;
import java.util.List;

public class SaidaBatchAdapter extends RecyclerView.Adapter<SaidaBatchAdapter.ViewHolder> {

    private final List<Produto> produtos = new ArrayList<>();
    private final OnItemRemovedListener listener;

    public interface OnItemRemovedListener {
        void onRemove(Produto produto);
    }

    public SaidaBatchAdapter(OnItemRemovedListener listener) {
        this.listener = listener;
    }

    public void setProdutos(List<Produto> novaLista) {
        produtos.clear();
        produtos.addAll(novaLista);
        notifyDataSetChanged();
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entrada_batch, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Produto p = produtos.get(position);
        holder.tvNome.setText(p.getNome());
        holder.tvEan.setText("EAN: " + p.getCodigoEan());
        
        // Usamos o ícone de remover padrão que já configuramos no layout item_entrada_batch
        holder.btnRemover.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemove(p);
            }
        });
    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNome, tvEan;
        ImageButton btnRemover;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvNomeEntradaItem);
            tvEan = itemView.findViewById(R.id.tvEanEntradaItem);
            btnRemover = itemView.findViewById(R.id.btnRemoverEntradaItem);
        }
    }
}
