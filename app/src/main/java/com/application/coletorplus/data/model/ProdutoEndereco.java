package com.application.coletorplus.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
        tableName = "produto_endereco_ref",
        primaryKeys = {"produtoId", "enderecoId"},
        foreignKeys = {
                @ForeignKey(entity = Produto.class, parentColumns = "id", childColumns = "produtoId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Endereco.class, parentColumns = "id", childColumns = "enderecoId", onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("produtoId"), @Index("enderecoId")}
)
public class ProdutoEndereco {

    private long produtoId;
    private int enderecoId;

    public ProdutoEndereco(long produtoId, int enderecoId) {
        this.produtoId = produtoId;
        this.enderecoId = enderecoId;
    }

    public long getProdutoId() { return produtoId; }
    public void setProdutoId(long produtoId) { this.produtoId = produtoId; }

    public int getEnderecoId() { return enderecoId; }
    public void setEnderecoId(int enderecoId) { this.enderecoId = enderecoId; }
}