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

    private List<Produto> listaCompleta = new ArrayList<>(); // Cópia original dos dados
    private List<Produto> listaExibida = new ArrayList<>();  // Lista filtrada que aparece na tela
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Produto produto, boolean isChecked);
    }

    public ProdutoAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Atualiza a lista quando os dados mudam no banco de dados
    public void setListaProdutos(List<Produto> produtos) {
        this.listaCompleta = new ArrayList<>(produtos);
        this.listaExibida = new ArrayList<>(produtos);
        notifyDataSetChanged();
    }

    // 🔍 O MÉTODO FILTRAR QUE VOCÊ PERGUNTOU:
    public void filtrar(String texto) {
        listaExibida.clear();

        if (texto.trim().isEmpty()) {
            // Se a barra de pesquisa estiver vazia, mostra tudo
            listaExibida.addAll(listaCompleta);
        } else {
            String termo = texto.toLowerCase().trim();

            for (Produto produto : listaCompleta) {
                // Filtra por NOME ou por EAN (Código de Barras)
                boolean bateComNome = produto.getNome() != null && produto.getNome().toLowerCase().contains(termo);
                boolean bateComEan = produto.getCodigoEan() != null && produto.getCodigoEan().contains(termo);

                if (bateComNome || bateComEan) {
                    listaExibida.add(produto);
                }
            }
        }

        // Avisa o RecyclerView para redesenhar os itens filtrados na tela
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
        holder.tvEan.setText("EAN: " + produto.getCodigoEan());

        // Destaque se for item crítico
        if (produto.isCritico()) {
            holder.tvCriticoTag.setVisibility(View.VISIBLE);
        } else {
            holder.tvCriticoTag.setVisibility(View.GONE);
        }

        holder.cbReposto.setOnCheckedChangeListener(null);
        holder.cbReposto.setChecked(produto.isReposto());

        holder.cbReposto.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onItemClick(produto, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaExibida.size();
    }

    static class ProdutoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNome, tvEan, tvCriticoTag;
        CheckBox cbReposto;

        public ProdutoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvNomeProduto);
            tvEan = itemView.findViewById(R.id.tvEanProduto);
            tvCriticoTag = itemView.findViewById(R.id.tvTagCritico); // opcional
            cbReposto = itemView.findViewById(R.id.cbReposto);
        }
    }
}