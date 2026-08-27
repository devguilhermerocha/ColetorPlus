package com.application.coletorplus.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.application.coletorplus.data.model.Endereco;
import com.application.coletorplus.data.model.Produto;
import com.application.coletorplus.data.model.ProdutoEndereco;

import java.util.List;

@Dao
public interface EnderecoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long inserir(Endereco endereco);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void vincularProdutoEndereco(ProdutoEndereco ref);

    @Query("SELECT * FROM enderecos ORDER BY descricao ASC")
    List<Endereco> listarTodos();

    @Query("SELECT * FROM enderecos WHERE UPPER(descricao) = UPPER(:nomeRua) LIMIT 1")
    Endereco buscarRuaExata(String nomeRua);

    // Ajustado para consultar a tabela produto_endereco_ref
    @Query("DELETE FROM produto_endereco_ref WHERE produtoId = :produtoId AND enderecoId = :enderecoId")
    int desenderecarProduto(long produtoId, int enderecoId);

    @Query("SELECT p.* FROM produtos p " +
            "INNER JOIN produto_endereco_ref pe ON p.id = pe.produtoId " +
            "WHERE pe.enderecoId = :enderecoId")
    List<Produto> buscarProdutosPorEndereco(int enderecoId);

    @Query("SELECT e.* FROM enderecos e " +
            "INNER JOIN produto_endereco_ref pe ON e.id = pe.enderecoId " +
            "WHERE pe.produtoId = :produtoId")
    List<Endereco> buscarEnderecosPorProduto(long produtoId);

    @Query("DELETE FROM produto_endereco_ref WHERE enderecoId = :enderecoId")
    void desenderecarTodosProdutos(int enderecoId);
}