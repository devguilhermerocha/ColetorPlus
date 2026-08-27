package com.application.coletorplus.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.application.coletorplus.data.model.ProdutoValidade;
import com.application.coletorplus.data.model.Validade;

import java.util.List;

@Dao
public interface ValidadeDao {

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    long inserirValidade(Validade validade);

    @Query("SELECT * FROM validades WHERE produtoId = :produtoId ORDER BY dataVencimento ASC")
    List<Validade> buscarPorProduto(long produtoId);

    @Query("SELECT SUM(quantidade) FROM validades WHERE produtoId = :produtoId")
    int getSomaQuantidades(long produtoId);

    @Delete
    void deletar(Validade validade);
}