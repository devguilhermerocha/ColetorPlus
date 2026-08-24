package com.application.coletorplus.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "usuarios",
        indices = {@Index(value = {"matricula"}, unique = true)}
)
public class Usuario {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String nome;

    @NonNull
    private String matricula; // Identificador único de LoginActivity

    @NonNull
    private String senha;

    @NonNull
    private String perfil; // "MASTER" ou "OPERADOR"

    public Usuario(@NonNull String nome, @NonNull String matricula, @NonNull String senha, @NonNull String perfil) {
        this.nome = nome;
        this.matricula = matricula;
        this.senha = senha;
        this.perfil = perfil;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @NonNull public String getNome() { return nome; }
    public void setNome(@NonNull String nome) { this.nome = nome; }

    @NonNull public String getMatricula() { return matricula; }
    public void setMatricula(@NonNull String matricula) { this.matricula = matricula; }

    @NonNull public String getSenha() { return senha; }
    public void setSenha(@NonNull String senha) { this.senha = senha; }

    @NonNull public String getPerfil() { return perfil; }
    public void setPerfil(@NonNull String perfil) { this.perfil = perfil; }
}