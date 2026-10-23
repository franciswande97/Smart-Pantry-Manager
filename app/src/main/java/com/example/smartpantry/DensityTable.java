package com.example.smartpantry;

import java.util.HashMap;
import java.util.Map;

/**
 * Approximate density (grams per millilitre) of common pantry ingredients.
 * It lets the matcher compare a volume (tbsp, cup, ml) with a mass (g, kg) for the SAME
 * ingredient, e.g. "2 tbsp sugar" in a recipe against "100 g sugar" in the pantry.
 * Ingredients not listed here are never compared across unit types.
 */
public final class DensityTable {

    private static final Map<String, Double> GRAMS_PER_ML = new HashMap<>();

    static {
        put("sugar", 0.85);
        put("salt", 1.2);
        put("flour", 0.5);
        put("butter", 0.95);
        put("oil", 0.92);
        put("olive oil", 0.92);
        put("soy sauce", 1.15);
        put("honey", 1.4);
        put("lemon juice", 1.0);
        put("peanut butter", 1.1);
        put("rice", 0.8);
        put("oats", 0.4);
        put("milk", 1.03);
        put("water", 1.0);
        put("vegetable stock", 1.0);
    }

    private DensityTable() { }

    /** Keys go through the same normaliser as everything else, so "Oats" and "oat" are one entry. */
    private static void put(String name, double gramsPerMl) {
        GRAMS_PER_ML.put(IngredientNormalizer.normalizeName(name), gramsPerMl);
    }

    /** Returns grams per millilitre, or null when the ingredient's density is unknown. */
    public static Double gramsPerMl(String ingredientName) {
        return GRAMS_PER_ML.get(IngredientNormalizer.normalizeName(ingredientName));
    }
}