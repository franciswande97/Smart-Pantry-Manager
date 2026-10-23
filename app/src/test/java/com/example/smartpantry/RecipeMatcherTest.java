package com.example.smartpantry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Unit tests for the strict-matching rule (brief section 2.3). Runs on the PC, no phone needed. */
public class RecipeMatcherTest {

    private PantryItem pantry(String name, double qty, String unit) {
        PantryItem p = new PantryItem();
        p.setName(name);
        p.setQuantity(qty);
        p.setUnit(unit);
        return p;
    }

    private Recipe recipe(int id, String name) {
        Recipe r = new Recipe();
        r.setId(id);
        r.setName(name);
        return r;
    }

    private RecipeIngredient ing(int recipeId, String name, double qty, String unit) {
        RecipeIngredient ri = new RecipeIngredient();
        ri.setRecipeId(recipeId);
        ri.setName(name);
        ri.setQuantity(qty);
        ri.setUnit(unit);
        return ri;
    }

    private List<Recipe> strict(List<PantryItem> pantry, List<RecipeIngredient> ingredients) {
        return RecipeMatcher.strictMatches(pantry, Arrays.asList(recipe(1, "Test Recipe")), ingredients);
    }

    @Test
    public void allIngredientsPresent_recipeIsSuggested() {
        List<PantryItem> p = Arrays.asList(pantry("Bread", 2, "slices"), pantry("Cheese", 60, "g"));
        List<RecipeIngredient> i = Arrays.asList(ing(1, "Bread", 2, "slices"), ing(1, "Cheese", 60, "g"));
        assertEquals(1, strict(p, i).size());
    }

    @Test
    public void oneIngredientMissing_recipeIsNotSuggested() {
        List<PantryItem> p = Arrays.asList(pantry("Bread", 2, "slices"));
        List<RecipeIngredient> i = Arrays.asList(ing(1, "Bread", 2, "slices"), ing(1, "Cheese", 60, "g"));
        assertTrue(strict(p, i).isEmpty());
    }

    @Test
    public void quantityTooSmall_recipeIsNotSuggested() {
        List<PantryItem> p = Arrays.asList(pantry("Cheese", 40, "g"));
        List<RecipeIngredient> i = Arrays.asList(ing(1, "Cheese", 60, "g"));
        assertTrue(strict(p, i).isEmpty());
    }

    @Test
    public void pluralNamesAndDifferentUnits_stillMatch() {
        // "Eggs" must satisfy "Egg", and 1 kg must satisfy 200 g
        List<PantryItem> p = Arrays.asList(pantry("Eggs", 3, "pcs"), pantry("Flour", 1, "kg"));
        List<RecipeIngredient> i = Arrays.asList(ing(1, "Egg", 2, "pcs"), ing(1, "Flour", 200, "g"));
        assertEquals(1, strict(p, i).size());
    }

    @Test
    public void duplicatePantryEntries_areAddedTogether() {
        List<PantryItem> p = Arrays.asList(pantry("Sugar", 100, "g"), pantry("Sugar", 150, "g"));
        List<RecipeIngredient> i = new ArrayList<>(Arrays.asList(ing(1, "Sugar", 250, "g")));
        assertEquals(1, strict(p, i).size());
    }

    @Test
    public void gramsInPantry_satisfySpoonMeasureInRecipe() {
        // 2 tbsp sugar = 30 ml, which is about 25.5 g, so 30 g is enough
        List<PantryItem> p = Arrays.asList(pantry("Sugar", 30, "g"));
        List<RecipeIngredient> i = Arrays.asList(ing(1, "Sugar", 2, "tbsp"));
        assertEquals(1, strict(p, i).size());
    }

    @Test
    public void unknownIngredient_isNeverComparedAcrossUnitTypes() {
        // No density is known for this item, so grams cannot satisfy tablespoons
        List<PantryItem> p = Arrays.asList(pantry("Mystery Spice", 100, "g"));
        List<RecipeIngredient> i = Arrays.asList(ing(1, "Mystery Spice", 2, "tbsp"));
        assertTrue(strict(p, i).isEmpty());
    }
}