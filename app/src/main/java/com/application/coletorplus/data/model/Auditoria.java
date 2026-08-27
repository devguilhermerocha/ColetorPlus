package com.application.coletorplus.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "auditoria")
public class Auditoria {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String usuarioNome;

    @NonNull
    private String tipoAcao; // "ENTRADA", "SAÍDA", "AVARIA", "ZERAMENTO"

    @NonNull
    private String descricao;

    private long timestamp;

    public Auditoria(@NonNull String usuarioNome, @NonNull String tipoAcao, @NonNull String descricao, long timestamp) {
        this.usuarioNome = usuarioNome;
        this.tipoAcao = tipoAcao;
        this.descricao = descricao;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @NonNull public String getUsuarioNome() { return usuarioNome; }
    public void setUsuarioNome(@NonNull String usuarioNome) { this.usuarioNome = usuarioNome; }

    @NonNull public String getTipoAcao() { return tipoAcao; }
    public void setTipoAcao(@NonNull String tipoAcao) { this.tipoAcao = tipoAcao; }

    @NonNull public String getDescricao() { return descricao; }
    public void setDescricao(@NonNull String descricao) { this.descricao = descricao; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
