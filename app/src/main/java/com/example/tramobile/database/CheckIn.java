package com.example.tramobile.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "checkins")
public class CheckIn {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nome;
    public String descricao;
    public String categoria;
    public double latitude;
    public double longitude;
    public String userId;
    public long timestamp;


}
