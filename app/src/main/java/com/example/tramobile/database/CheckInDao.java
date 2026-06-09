package com.example.tramobile.database;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

import com.example.tramobile.database.CheckIn;


@Dao
public interface CheckInDao {

    @Insert
    void inserir(CheckIn checkIn);

    @Query("SELECT * FROM checkins WHERE userId = :userId ORDER BY timestamp DESC")
    List<CheckIn> listarPorUsuario(String userId);

    @Query("DELETE FROM checkins WHERE id = :id")
    void deletar(int id);
}
