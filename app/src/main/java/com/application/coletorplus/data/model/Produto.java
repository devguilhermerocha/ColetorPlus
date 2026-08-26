package com.application.coletorplus.data.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "produtos",  indices = {@Index(value = {"codigoEan"}, unique = true)})
public class Produto {
    @PrimaryKey(autoGenerate = true)

    private long id;
    private String nome;
    private String codigoEan;
    private int quantidadeTotal;

    public Produto(String nome, String codigoEan, int quantidadeTotal) {
        this.nome = nome != null ? nome.toUpperCase() : null;
        this.codigoEan = codigoEan != null ? codigoEan.toUpperCase() : null;
        this.quantidadeTotal = quantidadeTotal;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome != null ? nome.toUpperCase() : null;
    }

    public String getCodigoEan() {
        return codigoEan;
    }

    public void setCodigoEan(String codigoEan) {
        this.codigoEan = codigoEan != null ? codigoEan.toUpperCase() : null;
    }

    public int getQuantidadeTotal() {
        return quantidadeTotal;
    }

    public void setQuantidadeTotal(int quantidadeTotal) {
        this.quantidadeTotal = quantidadeTotal;
    }
}
