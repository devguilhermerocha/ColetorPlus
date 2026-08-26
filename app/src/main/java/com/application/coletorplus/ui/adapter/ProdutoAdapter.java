package com.application.coletorplus.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.coletorplus.R;
import com.application.coletorplus.data.model.Produto;

import java.util.ArrayList;
import java.util.List;

public class ProdutoAdapter extends RecyclerView.Adapter<ProdutoAdapter.ProdutoViewHolder> {

    private List<Produto> listaCompleta = new ArrayList<>();
    private List<Produto> listaExibida = new ArrayList<>();

    public ProdutoAdapter() {
    }

    public void setListaProdutos(List<Produto> produtos) {
        this.listaCompleta = new ArrayList<>(produtos);
        this.listaExibida = new ArrayList<>(produtos);
        notifyDataSetChanged();
    }

    // Método de filtro guardado (desativado por enquanto, mas funcional)
    public void filtrar(String texto) {
        listaExibida.clear();

        if (texto == null || texto.trim().isEmpty()) {
            listaExibida.addAll(listaCompleta);
        } else {
            String termo = texto.toLowerCase().trim();

            for (Produto produto : listaCompleta) {
                boolean bateComNome = produto.getNome() != null && produto.getNome().toLowerCase().contains(termo);
                boolean bateComEan = produto.getCodigoEan() != null && produto.getCodigoEan().contains(termo);

                if (bateComNome || bateComEan) {
                    listaExibida.add(produto);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProdutoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_produto, parent, false);
        return new ProdutoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdutoViewHolder holder, int position) {
        Produto produto = listaExibida.get(position);
        holder.tvNome.setText(produto.getNome());

        if (produto.getCodigoEan() != null && !produto.getCodigoEan().isEmpty()) {
            holder.tvEan.setText("EAN: " + produto.getCodigoEan());
            holder.tvEan.setVisibility(View.VISIBLE);
        } else {
            holder.tvEan.setVisibility(View.GONE);
        }

        holder.tvQuantidade.setText("Qtd: " + produto.getQuantidadeTotal());
    }

    @Override
    public int getItemCount() {
        return listaExibida.size();
    }

    static class ProdutoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNome, tvEan, tvQuantidade;

        public ProdutoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvNomeProduto);
            tvEan = itemView.findViewById(R.id.tvEanProduto);
            tvQuantidade = itemView.findViewById(R.id.tvQuantidadeProduto);
        }
    }
}