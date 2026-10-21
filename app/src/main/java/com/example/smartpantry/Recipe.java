package com.example.smartpantry;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/** A recipe in the pre-loaded collection. Its ingredients live in RecipeIngredient. */
@Entity(tableName = "recipes")
public class Recipe {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String name = "";

    @NonNull
    private String steps = "";

    public Recipe() { }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @NonNull public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }

    @NonNull public String getSteps() { return steps; }
    public void setSteps(@NonNull String steps) { this.steps = steps; }
}