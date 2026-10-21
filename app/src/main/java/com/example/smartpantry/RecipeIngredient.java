package com.example.smartpantry;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * One ingredient line of one recipe. The foreign key links it to its Recipe;
 * CASCADE deletes the ingredient rows automatically if the recipe is deleted.
 */
@Entity(
        tableName = "recipe_ingredients",
        foreignKeys = @ForeignKey(
                entity = Recipe.class,
                parentColumns = "id",
                childColumns = "recipeId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("recipeId")}
)
public class RecipeIngredient {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int recipeId;

    @NonNull
    private String name = "";

    private double quantity;

    @NonNull
    private String unit = "";

    public RecipeIngredient() { }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRecipeId() { return recipeId; }
    public void setRecipeId(int recipeId) { this.recipeId = recipeId; }

    @NonNull public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    @NonNull public String getUnit() { return unit; }
    public void setUnit(@NonNull String unit) { this.unit = unit; }
}