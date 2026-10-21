package com.example.smartpantry;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/** Queries for the recipe collection (seeded on first run, then read-only). */
@Dao
public interface RecipeDao {

    @Insert
    long insertRecipe(Recipe recipe);

    @Insert
    void insertIngredients(List<RecipeIngredient> ingredients);

    @Query("SELECT * FROM recipes ORDER BY name ASC")
    List<Recipe> getAllRecipes();

    @Query("SELECT * FROM recipes WHERE id = :id")
    Recipe getRecipeById(int id);

    @Query("SELECT * FROM recipe_ingredients WHERE recipeId = :recipeId")
    List<RecipeIngredient> getIngredientsForRecipe(int recipeId);

    @Query("SELECT * FROM recipe_ingredients")
    List<RecipeIngredient> getAllIngredients();
}