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

public class EntradaBatchAdapter extends RecyclerView.Adapter<EntradaBatchAdapter.ViewHolder> {

    private final List<Produto> produtos = new ArrayList<>();
    private final OnItemRemovedListener listener;

    public interface OnItemRemovedListener {
        void onRemoved(int position);
    }

    public EntradaBatchAdapter(OnItemRemovedListener listener) {
        this.listener = listener;
    }

    public void addProduto(Produto produto) {
        produtos.add(0, produto);
        notifyItemInserted(0);
    }

    public void removerProduto(int position) {
        if (position >= 0 && position < produtos.size()) {
            produtos.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, produtos.size());
        }
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public void limpar() {
        int size = produtos.size();
        produtos.clear();
        notifyItemRangeRemoved(0, size);
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
        
        holder.btnRemover.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoved(holder.getAdapterPosition());
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
