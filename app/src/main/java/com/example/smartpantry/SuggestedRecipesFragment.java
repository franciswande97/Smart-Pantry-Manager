package com.example.smartpantry;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** The Suggested Recipes screen: runs the strict-matching rule against the current pantry. */
public class SuggestedRecipesFragment extends Fragment implements RecipeAdapter.OnRecipeClickListener {

    private RecipeAdapter adapter;
    private TextView emptyView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_suggested_recipes, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.rv_recipes);
        emptyView = root.findViewById(R.id.tv_recipes_empty);

        adapter = new RecipeAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        return root;
    }

    /** Re-runs the matching every time the screen is shown, so it always reflects the latest pantry. */
    @Override
    public void onResume() {
        super.onResume();
        loadSuggestions();
    }

    private void loadSuggestions() {
        Context appContext = requireContext().getApplicationContext();
        AppDatabase.databaseExecutor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(appContext);
            List<PantryItem> pantry = db.pantryItemDao().getAll();
            List<Recipe> recipes = db.recipeDao().getAllRecipes();
            List<RecipeIngredient> allIngredients = db.recipeDao().getAllIngredients();

            // The core business logic: only recipes where EVERY ingredient is available
            List<Recipe> suggested = RecipeMatcher.strictMatches(pantry, recipes, allIngredients);

            Map<Integer, List<String>> namesByRecipe = new HashMap<>();
            for (RecipeIngredient ri : allIngredients) {
                if (!namesByRecipe.containsKey(ri.getRecipeId())) {
                    namesByRecipe.put(ri.getRecipeId(), new ArrayList<String>());
                }
                namesByRecipe.get(ri.getRecipeId()).add(ri.getName());
            }

            List<RecipeAdapter.Row> rows = new ArrayList<>();
            for (Recipe recipe : suggested) {
                List<String> names = namesByRecipe.get(recipe.getId());
                String summary = (names == null) ? "" : TextUtils.join(", ", names);
                rows.add(new RecipeAdapter.Row(recipe, summary));
            }

            Activity activity = getActivity();
            if (activity == null) return;
            activity.runOnUiThread(() -> {
                adapter.setRows(rows);
                emptyView.setVisibility(rows.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}