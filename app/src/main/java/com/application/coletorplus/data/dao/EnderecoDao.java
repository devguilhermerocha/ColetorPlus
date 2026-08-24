package com.application.coletorplus.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.application.coletorplus.data.model.Endereco;
import com.application.coletorplus.data.model.ProdutoEndereco;

import java.util.List;

@Dao
public interface EnderecoDao {

    @Insert
    long inserir(Endereco endereco);

    @Insert
    void vincularProdutoEndereco(ProdutoEndereco ref);

    @Query("SELECT * FROM enderecos")
    List<Endereco> listarTodos();
}