package com.application.pistalimpa.ui.produto;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.pistalimpa.R;
import com.application.pistalimpa.data.model.Produto;

import java.util.ArrayList;
import java.util.List;

public class ProdutoAdapter extends RecyclerView.Adapter<ProdutoAdapter.ProdutoViewHolder> {

    private List<Produto> listaProdutos = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onStatusChanged(Produto produto, boolean isReposto);
    }

    public ProdutoAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setListaProdutos(List<Produto> produtos) {
        this.listaProdutos = produtos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProdutoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_produto, parent, false);
        return new ProdutoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdutoViewHolder holder, int position) {
        Produto produto = listaProdutos.get(position);

        holder.tvNome.setText(produto.getNome());

        if (produto.getCodigoEan() != null && !produto.getCodigoEan().isEmpty()) {
            holder.tvEan.setText("EAN: " + produto.getCodigoEan());
            holder.tvEan.setVisibility(View.VISIBLE);
        } else {
            holder.tvEan.setVisibility(View.GONE);
        }

        // Exibe tag de crítico se gôndola estiver zerada
        holder.tvCritico.setVisibility(produto.isCritico() ? View.VISIBLE : View.GONE);

        // Define status sem disparar o listener durante o scroll
        holder.cbReposto.setOnCheckedChangeListener(null);
        holder.cbReposto.setChecked(produto.isReposto());

        holder.cbReposto.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onStatusChanged(produto, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaProdutos.size();
    }

    static class ProdutoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNome, tvEan, tvCritico;
        CheckBox cbReposto;

        public ProdutoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvNomeProduto);
            tvEan = itemView.findViewById(R.id.tvEanProduto);
            tvCritico = itemView.findViewById(R.id.tvTagCritico);
            cbReposto = itemView.findViewById(R.id.cbReposto);
        }
    }
}