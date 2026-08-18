package com.application.pistalimpa.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.application.pistalimpa.data.model.Produto;

import java.util.List;

@Dao
public interface ProdutoDAO {
    @Insert
    long inserir(Produto produto);

    @Update
    void atualizar(Produto produto);

    @Query("SELECT * FROM produtos WHERE isReposto = 0 ORDER BY isCritico DESC, id DESC")
    List<Produto> getProdutosPendentes();

    @Query("SELECT * FROM produtos WHERE isReposto = 1 ORDER BY id DESC")
    List<Produto> getProdutosHistorico();
}
