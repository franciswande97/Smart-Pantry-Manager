package com.example.smartpantry;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/** CRUD operations for pantry items. Room generates the implementation at build time. */
@Dao
public interface PantryItemDao {

    @Insert
    long insert(PantryItem item);

    @Query("SELECT * FROM pantry_items ORDER BY name COLLATE NOCASE ASC")
    List<PantryItem> getAll();

    @Query("SELECT * FROM pantry_items WHERE id = :id")
    PantryItem getById(int id);

    @Update
    void update(PantryItem item);

    @Delete
    void delete(PantryItem item);
}