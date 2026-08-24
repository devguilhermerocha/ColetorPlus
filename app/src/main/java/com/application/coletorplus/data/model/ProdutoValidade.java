package com.application.coletorplus.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
        tableName = "produto_validade_ref",
        primaryKeys = {"produtoId", "validadeId"},
        foreignKeys = {
                @ForeignKey(entity = Produto.class, parentColumns = "id", childColumns = "produtoId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Validade.class, parentColumns = "id", childColumns = "validadeId", onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("produtoId"), @Index("validadeId")}
)
public class ProdutoValidade {

    private int produtoId;
    private int validadeId;

    public ProdutoValidade(int produtoId, int validadeId) {
        this.produtoId = produtoId;
        this.validadeId = validadeId;
    }

    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }

    public int getValidadeId() { return validadeId; }
    public void setValidadeId(int validadeId) { this.validadeId = validadeId; }
}