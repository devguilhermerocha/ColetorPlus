package com.application.coletorplus.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.application.coletorplus.data.model.Usuario;

@Dao
public interface UsuarioDao {

    @Insert
    long inserir(Usuario usuario);

    @Query("SELECT * FROM usuarios WHERE matricula = :matricula AND senha = :senha LIMIT 1")
    Usuario autenticar(String matricula, String senha);
}