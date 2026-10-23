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

import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The Suggested Recipes screen.
 *  - "Ready to cook": the strict list (every ingredient present). Recipes that use
 *    ingredients expiring soon are shown first, to cut food waste.
 *  - "Almost there": a separate list of recipes missing exactly one ingredient.
 */
public class SuggestedRecipesFragment extends Fragment implements RecipeAdapter.OnRecipeClickListener {

    private static final long DAY_MS = 24L * 60 * 60 * 1000;
    private static final long SOON_WINDOW_MS = 3 * DAY_MS;

    private RecipeAdapter adapter;
    private TextView emptyView;
    private boolean showAlmost = false;     // false = strict list, true = "almost there" list

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_suggested_recipes, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.rv_recipes);
        emptyView = root.findViewById(R.id.tv_recipes_empty);
        MaterialButtonToggleGroup toggle = root.findViewById(R.id.toggle_mode);

        adapter = new RecipeAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        toggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                showAlmost = (checkedId == R.id.btn_almost);
                loadSuggestions();
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSuggestions();
    }

    private void loadSuggestions() {
        Context appContext = requireContext().getApplicationContext();
        final boolean almostMode = showAlmost;      // fix the mode for this background run
        final boolean prioritiseExpiring = AppPreferences.isExpiryHighlightEnabled(appContext);

        AppDatabase.databaseExecutor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(appContext);
            List<PantryItem> pantry = db.pantryItemDao().getAll();
            List<Recipe> recipes = db.recipeDao().getAllRecipes();
            List<RecipeIngredient> allIngredients = db.recipeDao().getAllIngredients();

            // One evaluation of every recipe; both lists are filtered from this single result
            List<RecipeMatcher.MatchResult> results =
                    RecipeMatcher.evaluate(pantry, recipes, allIngredients);
            Map<Integer, List<String>> namesByRecipe = groupNames(allIngredients);
            Set<String> expiringSoon = prioritiseExpiring
                    ? expiringSoonNames(pantry) : new HashSet<String>();

            List<RecipeAdapter.Row> rows = almostMode
                    ? buildAlmostRows(results, namesByRecipe)
                    : buildReadyRows(results, namesByRecipe, expiringSoon);

            Activity activity = getActivity();
            if (activity == null) return;
            activity.runOnUiThread(() -> {
                adapter.setRows(rows);
                emptyView.setText(almostMode
                        ? "No recipes are one ingredient away right now."
                        : "No recipes match your pantry yet - add more ingredients");
                emptyView.setVisibility(rows.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }

    /** Strict list: nothing missing. Recipes using soon-to-expire items come first. */
    private List<RecipeAdapter.Row> buildReadyRows(List<RecipeMatcher.MatchResult> results,
                                                   Map<Integer, List<String>> namesByRecipe,
                                                   Set<String> expiringSoon) {
        List<RecipeMatcher.MatchResult> ready = new ArrayList<>();
        for (RecipeMatcher.MatchResult r : results) {
            if (r.isStrictMatch()) ready.add(r);
        }

        // For each recipe, which of its ingredients are about to expire?
        final Map<Integer, List<String>> usesExpiring = new HashMap<>();
        for (RecipeMatcher.MatchResult r : ready) {
            List<String> hits = new ArrayList<>();
            List<String> names = namesByRecipe.get(r.getRecipe().getId());
            if (names != null) {
                for (String n : names) {
                    if (expiringSoon.contains(IngredientNormalizer.normalizeName(n))) hits.add(n);
                }
            }
            usesExpiring.put(r.getRecipe().getId(), hits);
        }

        // Most expiring ingredients first, then alphabetical
        Collections.sort(ready, (a, b) -> {
            int diff = usesExpiring.get(b.getRecipe().getId()).size()
                    - usesExpiring.get(a.getRecipe().getId()).size();
            return diff != 0 ? diff : a.getRecipe().getName().compareTo(b.getRecipe().getName());
        });

        List<RecipeAdapter.Row> rows = new ArrayList<>();
        for (RecipeMatcher.MatchResult r : ready) {
            int id = r.getRecipe().getId();
            List<String> hits = usesExpiring.get(id);
            String summary = "";
            if (!hits.isEmpty()) {
                summary += "\u23F0 Uses expiring: " + TextUtils.join(", ", hits) + "\n";
            }
            summary += joinNames(namesByRecipe.get(id));
            rows.add(new RecipeAdapter.Row(r.getRecipe(), summary));
        }
        return rows;
    }

    /** Separate list: recipes missing exactly one ingredient. Never mixed into the strict list. */
    private List<RecipeAdapter.Row> buildAlmostRows(List<RecipeMatcher.MatchResult> results,
                                                    Map<Integer, List<String>> namesByRecipe) {
        List<RecipeMatcher.MatchResult> almost = new ArrayList<>();
        for (RecipeMatcher.MatchResult r : results) {
            if (r.getMissingIngredients().size() == 1) almost.add(r);
        }
        Collections.sort(almost, (a, b) ->
                a.getRecipe().getName().compareTo(b.getRecipe().getName()));

        List<RecipeAdapter.Row> rows = new ArrayList<>();
        for (RecipeMatcher.MatchResult r : almost) {
            String summary = "Missing: " + r.getMissingIngredients().get(0) + "\n"
                    + joinNames(namesByRecipe.get(r.getRecipe().getId()));
            rows.add(new RecipeAdapter.Row(r.getRecipe(), summary));
        }
        return rows;
    }

    // ---- helpers ----

    private static Map<Integer, List<String>> groupNames(List<RecipeIngredient> all) {
        Map<Integer, List<String>> map = new HashMap<>();
        for (RecipeIngredient ri : all) {
            if (!map.containsKey(ri.getRecipeId())) {
                map.put(ri.getRecipeId(), new ArrayList<String>());
            }
            map.get(ri.getRecipeId()).add(ri.getName());
        }
        return map;
    }

    /** Cleaned names of pantry items that expire within the next 3 days (or only just expired). */
    private static Set<String> expiringSoonNames(List<PantryItem> pantry) {
        long now = System.currentTimeMillis();
        Set<String> names = new HashSet<>();
        for (PantryItem p : pantry) {
            Long expiry = p.getExpiryDate();
            if (expiry != null && expiry >= now - DAY_MS && expiry <= now + SOON_WINDOW_MS) {
                names.add(IngredientNormalizer.normalizeName(p.getName()));
            }
        }
        return names;
    }

    private static String joinNames(List<String> names) {
        return names == null ? "" : TextUtils.join(", ", names);
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}