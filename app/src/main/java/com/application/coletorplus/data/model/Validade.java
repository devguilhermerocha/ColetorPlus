package com.application.coletorplus.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "validades",
        foreignKeys = @ForeignKey(
                entity = Produto.class,
                parentColumns = "id",
                childColumns = "produtoId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("produtoId")}
)
public class Validade {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private long produtoId; // Referência direta ao ID do produto
    private long dataVencimento; // Timestamp em milissegundos
    private int quantidade; // Quantidade deste lote específico

    public Validade(long produtoId, long dataVencimento, int quantidade) {
        this.produtoId = produtoId;
        this.dataVencimento = dataVencimento;
        this.quantidade = quantidade;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public long getProdutoId() { return produtoId; }
    public void setProdutoId(long produtoId) { this.produtoId = produtoId; }

    public long getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(long dataVencimento) { this.dataVencimento = dataVencimento; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}