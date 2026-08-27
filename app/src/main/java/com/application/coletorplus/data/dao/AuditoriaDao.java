package com.application.coletorplus.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.application.coletorplus.data.model.Auditoria;

import java.util.List;

@Dao
public interface AuditoriaDao {

    @Insert
    void inserir(Auditoria log);

    @Query("SELECT * FROM auditoria ORDER BY timestamp DESC")
    List<Auditoria> listarTodas();

    @Query("DELETE FROM auditoria")
    void deletarTudo();
}
