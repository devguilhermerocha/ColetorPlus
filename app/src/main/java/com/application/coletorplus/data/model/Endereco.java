package com.application.coletorplus.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "enderecos",
        indices = {@Index(value = {"descricao"}, unique = true)}
)
public class Endereco {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String descricao; // Ex: "Rua 03 - Gondola B" ou QR Code da Rua

    public Endereco(@NonNull String descricao) {
        this.descricao = descricao;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @NonNull public String getDescricao() { return descricao; }
    public void setDescricao(@NonNull String descricao) { this.descricao = descricao; }
}