package com.application.coletorplus.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.application.coletorplus.data.model.Produto;
import com.application.coletorplus.data.model.Endereco;
import com.application.coletorplus.data.model.ProdutoEndereco;

import java.util.List;

@Dao
public interface EnderecoDao {

    // 1. Cadastra uma nova rua no banco (cancela se já houver conflito de ID)
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long inserir(Endereco endereco);

    // 2. Associa um produto a este endereço (tabela de junção N:N)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void vincularProdutoEndereco(ProdutoEndereco ref);

    // 3. Lista todas as ruas cadastradas no sistema
    @Query("SELECT * FROM enderecos ORDER BY descricao ASC")
    List<Endereco> listarTodos();

    // 4. Busca se a rua já existe exatamente pelo nome/código (Ex: "Rua 03")
    @Query("SELECT * FROM enderecos WHERE descricao = :nomeRua LIMIT 1")
    Endereco buscarRuaExata(String nomeRua);

    // 5. Busca ruas por aproximação para preencher o filtro de busca em tempo real
    @Query("SELECT * FROM enderecos WHERE descricao LIKE '%' || :termo || '%' ORDER BY descricao ASC")
    List<Endereco> buscarRuasPorTermo(String termo);

    // 6. Remove a vinculação de um produto com determinado endereço
    @Query("DELETE FROM produto_endereco_ref WHERE produtoId = :produtoId AND enderecoId = :enderecoId")
    void desenderecarProduto(long produtoId, int enderecoId);

    @Query("SELECT p.* FROM produtos p " +
            "INNER JOIN produto_endereco_ref pe ON p.id = pe.produtoId " +
            "WHERE pe.enderecoId = :enderecoId")
    List<Produto> buscarProdutosPorEndereco(int enderecoId);

    // 7. Exclui a rua do banco de dados
    @Delete
    void deletarEndereco(Endereco endereco);
}