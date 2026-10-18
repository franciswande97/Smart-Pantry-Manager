package com.example.smartpantry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The strict-matching rule (brief section 2.3): a recipe is "suggested" only if
 * EVERY ingredient it needs is in the pantry in at least the required quantity.
 */
public final class RecipeMatcher {

    private static final double EPSILON = 1e-9;   // guards against floating-point rounding

    /** The outcome for one recipe: which of its ingredients are missing. */
    public static final class MatchResult {
        private final Recipe recipe;
        private final List<String> missingIngredients;

        MatchResult(Recipe recipe, List<String> missingIngredients) {
            this.recipe = recipe;
            this.missingIngredients = missingIngredients;
        }

        public Recipe getRecipe() { return recipe; }
        public List<String> getMissingIngredients() { return missingIngredients; }

        /** Strict match = nothing missing at all. */
        public boolean isStrictMatch() { return missingIngredients.isEmpty(); }
    }

    private RecipeMatcher() { }

    /** Evaluates every recipe against the pantry. */
    public static List<MatchResult> evaluate(List<PantryItem> pantry,
                                             List<Recipe> recipes,
                                             List<RecipeIngredient> allIngredients) {
        Map<String, Double> available = buildAvailableMap(pantry);
        Map<Integer, List<RecipeIngredient>> byRecipe = groupByRecipe(allIngredients);

        List<MatchResult> results = new ArrayList<>();
        for (Recipe recipe : recipes) {
            List<RecipeIngredient> needed = byRecipe.get(recipe.getId());
            if (needed == null || needed.isEmpty()) continue;   // a recipe with no ingredients is meaningless

            List<String> missing = new ArrayList<>();
            for (RecipeIngredient ri : needed) {
                if (!isSatisfied(ri, available)) {
                    missing.add(ri.getName());
                }
            }
            results.add(new MatchResult(recipe, missing));
        }
        return results;
    }

    /** Only the recipes the user can make right now (the "Suggested Recipes" list). */
    public static List<Recipe> strictMatches(List<PantryItem> pantry,
                                             List<Recipe> recipes,
                                             List<RecipeIngredient> allIngredients) {
        List<Recipe> suggested = new ArrayList<>();
        for (MatchResult r : evaluate(pantry, recipes, allIngredients)) {
            if (r.isStrictMatch()) suggested.add(r.getRecipe());
        }
        return suggested;
    }

    // ---- helpers ----

    /** Key = cleaned name + dimension, e.g. "tomato|COUNT" or "flour|MASS". */
    private static String key(String name, UnitConverter.Dimension dimension) {
        return IngredientNormalizer.normalizeName(name) + "|" + dimension.name();
    }

    /** Totals the pantry in base units; two entries of the same item are added together. */
    private static Map<String, Double> buildAvailableMap(List<PantryItem> pantry) {
        Map<String, Double> map = new HashMap<>();
        for (PantryItem item : pantry) {
            UnitConverter.Measure m = UnitConverter.toBase(item.getQuantity(), item.getUnit());
            String k = key(item.getName(), m.dimension);
            double current = map.containsKey(k) ? map.get(k) : 0;
            map.put(k, current + m.amount);
        }
        return map;
    }

    private static Map<Integer, List<RecipeIngredient>> groupByRecipe(List<RecipeIngredient> all) {
        Map<Integer, List<RecipeIngredient>> map = new HashMap<>();
        for (RecipeIngredient ri : all) {
            if (!map.containsKey(ri.getRecipeId())) {
                map.put(ri.getRecipeId(), new ArrayList<RecipeIngredient>());
            }
            map.get(ri.getRecipeId()).add(ri);
        }
        return map;
    }

    /** True if the pantry holds at least the required amount, in a comparable unit type. */
    private static boolean isSatisfied(RecipeIngredient ri, Map<String, Double> available) {
        UnitConverter.Measure required = UnitConverter.toBase(ri.getQuantity(), ri.getUnit());
        if (required.dimension == UnitConverter.Dimension.UNKNOWN) return false;

        Double have = available.get(key(ri.getName(), required.dimension));
        return have != null && have + EPSILON >= required.amount;
    }
}