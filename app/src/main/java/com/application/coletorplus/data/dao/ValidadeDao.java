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

    @Query("SELECT COUNT(*) FROM validades WHERE dataVencimento <= :limitDate")
    int countValidadesVencendo(long limitDate);

    @Query("SELECT v.*, p.nome as produtoNome, p.codigoEan as produtoEan FROM validades v " +
            "INNER JOIN produtos p ON v.produtoId = p.id " +
            "WHERE v.dataVencimento <= :limitDate ORDER BY v.dataVencimento ASC")
    List<ValidadeComProduto> getValidadesVencendoComProduto(long limitDate);

    class ValidadeComProduto {
        @androidx.room.Embedded
        public Validade validade;
        public String produtoNome;
        public String produtoEan;
    }
}