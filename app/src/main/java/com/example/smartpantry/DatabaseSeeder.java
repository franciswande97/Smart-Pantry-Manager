package com.example.smartpantry;

import java.util.ArrayList;
import java.util.List;

/**
 * Pre-loads the recipe collection the first time the database is created.
 * Ingredient format: "Name|quantity|unit". Units are limited to ones that
 * UnitConverter understands (g, kg, ml, l, tsp, tbsp, cup, pcs, slices, cloves).
 */
public final class DatabaseSeeder {

    private DatabaseSeeder() { }

    public static void seed(RecipeDao dao) {
        add(dao, "Cheese Omelette",
                "1. Beat the eggs with the salt.\n2. Melt the butter in a pan.\n3. Pour in the eggs and cook until almost set.\n4. Add the cheese, fold and serve.",
                "Egg|3|pcs", "Cheese|50|g", "Butter|10|g", "Salt|1|tsp");

        add(dao, "Scrambled Eggs on Toast",
                "1. Toast the bread.\n2. Melt the butter and add the beaten eggs and salt.\n3. Stir gently until just set.\n4. Serve on the toast.",
                "Egg|3|pcs", "Bread|2|slices", "Butter|10|g", "Salt|1|tsp");

        add(dao, "Tomato Pasta",
                "1. Boil the pasta in salted water.\n2. Fry the onion and garlic in the oil.\n3. Add the chopped tomatoes and simmer 10 minutes.\n4. Toss with the drained pasta.",
                "Pasta|200|g", "Tomato|4|pcs", "Onion|1|pcs", "Garlic|2|cloves", "Olive Oil|2|tbsp", "Salt|1|tsp");

        add(dao, "Garlic Butter Rice",
                "1. Cook the rice until tender.\n2. Melt the butter and fry the garlic for one minute.\n3. Stir in the rice and salt.",
                "Rice|200|g", "Butter|20|g", "Garlic|2|cloves", "Salt|1|tsp");

        add(dao, "Egg Fried Rice",
                "1. Fry the chopped onion in the oil.\n2. Push aside, scramble the eggs.\n3. Add cooked rice and soy sauce.\n4. Stir-fry for three minutes.",
                "Rice|300|g", "Egg|2|pcs", "Onion|1|pcs", "Oil|2|tbsp", "Soy Sauce|2|tbsp");

        add(dao, "Pancakes",
                "1. Whisk the flour, sugar, milk and eggs into a batter.\n2. Melt a little butter in a pan.\n3. Cook spoonfuls until bubbles form, then flip.",
                "Flour|200|g", "Milk|300|ml", "Egg|2|pcs", "Sugar|2|tbsp", "Butter|20|g");

        add(dao, "Mashed Potatoes",
                "1. Peel and boil the potatoes until soft.\n2. Drain and mash with the butter and milk.\n3. Season with salt.",
                "Potato|4|pcs", "Butter|30|g", "Milk|100|ml", "Salt|1|tsp");

        add(dao, "Cheese Toastie",
                "1. Butter the outside of the bread.\n2. Place the cheese between the slices.\n3. Fry on both sides until golden.",
                "Bread|2|slices", "Cheese|60|g", "Butter|10|g");

        add(dao, "Chicken Rice Bowl",
                "1. Season and pan-fry the chicken in the oil.\n2. Cook the rice.\n3. Fry the sliced onion.\n4. Serve everything together.",
                "Chicken Breast|300|g", "Rice|200|g", "Onion|1|pcs", "Oil|1|tbsp", "Salt|1|tsp");

        add(dao, "Tuna Pasta",
                "1. Boil the pasta.\n2. Warm the tuna with the oil and garlic.\n3. Toss with the pasta and season.",
                "Pasta|200|g", "Tuna|170|g", "Olive Oil|1|tbsp", "Garlic|1|cloves");

        add(dao, "Vegetable Soup",
                "1. Chop the carrots, potatoes and onion.\n2. Simmer in the stock for 25 minutes.\n3. Season with salt and serve.",
                "Carrot|2|pcs", "Potato|2|pcs", "Onion|1|pcs", "Vegetable Stock|500|ml", "Salt|1|tsp");

        add(dao, "French Toast",
                "1. Whisk the eggs, milk and sugar.\n2. Dip the bread slices.\n3. Fry in butter until golden on both sides.",
                "Bread|4|slices", "Egg|2|pcs", "Milk|100|ml", "Sugar|1|tbsp", "Butter|10|g");

        add(dao, "Beans on Toast",
                "1. Toast the bread and butter it.\n2. Heat the beans.\n3. Spoon the beans over the toast.",
                "Baked Beans|200|g", "Bread|2|slices", "Butter|10|g");

        add(dao, "Avocado Toast",
                "1. Toast the bread.\n2. Mash the avocado with lemon juice and salt.\n3. Spread on the toast.",
                "Avocado|1|pcs", "Bread|2|slices", "Lemon Juice|1|tbsp", "Salt|0.5|tsp");

        add(dao, "Banana Oat Porridge",
                "1. Simmer the oats in the milk for five minutes.\n2. Slice in the banana.\n3. Drizzle with honey.",
                "Oats|80|g", "Milk|250|ml", "Banana|1|pcs", "Honey|1|tbsp");

        add(dao, "Greek Salad",
                "1. Chop the cucumber, tomatoes and onion.\n2. Crumble over the feta.\n3. Dress with the olive oil.",
                "Cucumber|1|pcs", "Tomato|3|pcs", "Onion|1|pcs", "Feta Cheese|100|g", "Olive Oil|2|tbsp");

        add(dao, "Soy Egg Noodles",
                "1. Boil the noodles.\n2. Fry the onion, then scramble in the eggs.\n3. Add the noodles and soy sauce and toss.",
                "Noodles|200|g", "Egg|2|pcs", "Soy Sauce|2|tbsp", "Oil|1|tbsp", "Onion|1|pcs");

        add(dao, "Mac and Cheese",
                "1. Boil the macaroni.\n2. Melt the butter, stir in the flour, then add the milk.\n3. Melt in the cheese.\n4. Mix with the macaroni.",
                "Macaroni|200|g", "Cheese|150|g", "Milk|250|ml", "Butter|30|g", "Flour|20|g");

        add(dao, "Chicken Stir-Fry",
                "1. Slice the chicken, peppers and onion.\n2. Fry the chicken in the oil until cooked.\n3. Add the vegetables and soy sauce.\n4. Stir-fry for five minutes.",
                "Chicken Breast|300|g", "Bell Pepper|2|pcs", "Onion|1|pcs", "Soy Sauce|2|tbsp", "Oil|1|tbsp");

        add(dao, "Peanut Butter Banana Sandwich",
                "1. Spread the peanut butter on both slices.\n2. Layer the sliced banana between them.",
                "Bread|2|slices", "Peanut Butter|2|tbsp", "Banana|1|pcs");
    }

    /** Inserts one recipe, then all of its ingredient rows linked by the new recipe id. */
    private static void add(RecipeDao dao, String name, String steps, String... specs) {
        Recipe recipe = new Recipe();
        recipe.setName(name);
        recipe.setSteps(steps);
        int recipeId = (int) dao.insertRecipe(recipe);   // Room returns the generated id

        List<RecipeIngredient> ingredients = new ArrayList<>();
        for (String spec : specs) {
            String[] parts = spec.split("\\|");
            RecipeIngredient ri = new RecipeIngredient();
            ri.setRecipeId(recipeId);
            ri.setName(parts[0]);
            ri.setQuantity(Double.parseDouble(parts[1]));
            ri.setUnit(parts[2]);
            ingredients.add(ri);
        }
        dao.insertIngredients(ingredients);
    }
}