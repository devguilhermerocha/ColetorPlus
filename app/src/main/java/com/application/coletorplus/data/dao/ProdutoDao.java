package com.application.coletorplus.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.application.coletorplus.data.model.Produto;

import java.util.List;

@Dao
public interface ProdutoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long inserir(Produto produto);

    @Update
    void atualizar(Produto produto);
    
    @Query("SELECT * FROM produtos WHERE codigoEan = :codigoEan LIMIT 1")
    Produto buscarPorEan(String codigoEan);

    @Query("SELECT * FROM produtos ORDER BY nome ASC")
    List<Produto> listarTodos();

    @Query("SELECT * FROM produtos WHERE nome LIKE '%' || :termo || '%' OR codigoEan LIKE '%' || :termo || '%' ORDER BY nome ASC")
    List<Produto> buscarPorTermo(String termo);

    @androidx.room.Delete
    void deletar(Produto produto);

    @Query("SELECT COUNT(*) FROM produtos WHERE quantidadeTotal <= 0")
    int countProdutosEsgotados();

    @Query("SELECT * FROM produtos WHERE quantidadeTotal <= 0 ORDER BY nome ASC")
    List<Produto> getProdutosSemEstoque();
}