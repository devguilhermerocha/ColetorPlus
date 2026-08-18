package com.application.pistalimpa.data.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "produtos",  indices = {@Index(value = {"codigoEan"}, unique = true)})
public class Produto {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String nome;
    private String codigoEan;
    private boolean isCritico;
    private boolean isReposto;

    public Produto(String nome, String codigoEan, boolean isCritico, boolean isReposto) {
        this.nome = nome;
        this.codigoEan = codigoEan;
        this.isCritico = isCritico;
        this.isReposto = isReposto;
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
        this.nome = nome;
    }

    public String getCodigoEan() {
        return codigoEan;
    }

    public void setCodigoEan(String codigoEan) {
        this.codigoEan = codigoEan;
    }

    public boolean isCritico() {
        return isCritico;
    }

    public void setCritico(boolean critico) {
        isCritico = critico;
    }

    public boolean isReposto() {
        return isReposto;
    }

    public void setReposto(boolean reposto) {
        isReposto = reposto;
    }
}
