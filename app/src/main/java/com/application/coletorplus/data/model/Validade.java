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

    private int produtoId; // Referência direta ao ID do produto
    private long dataVencimento; // Timestamp em milissegundos

    public Validade(int produtoId, long dataVencimento) {
        this.produtoId = produtoId;
        this.dataVencimento = dataVencimento;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }

    public long getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(long dataVencimento) { this.dataVencimento = dataVencimento; }
}