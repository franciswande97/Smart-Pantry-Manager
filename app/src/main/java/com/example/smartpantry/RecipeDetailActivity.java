package com.example.smartpantry;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

/** Shows the full ingredient list and method for the recipe whose id arrives in the Intent. */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recipe_detail_root), (v, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return windowInsets;
        });

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        int recipeId = getIntent().getIntExtra(EXTRA_RECIPE_ID, -1);
        if (recipeId == -1) {      // opened without an id: nothing to show
            finish();
            return;
        }
        loadRecipe(recipeId);
    }

    private void loadRecipe(int recipeId) {
        AppDatabase.databaseExecutor.execute(() -> {
            RecipeDao dao = AppDatabase.getInstance(getApplicationContext()).recipeDao();
            Recipe recipe = dao.getRecipeById(recipeId);
            List<RecipeIngredient> ingredients = dao.getIngredientsForRecipe(recipeId);

            runOnUiThread(() -> {
                if (recipe == null) { finish(); return; }

                ((TextView) findViewById(R.id.tv_detail_name)).setText(recipe.getName());

                StringBuilder sb = new StringBuilder();
                for (RecipeIngredient ri : ingredients) {
                    sb.append("• ")
                            .append(formatQuantity(ri.getQuantity())).append(" ")
                            .append(ri.getUnit()).append(" ")
                            .append(ri.getName()).append("\n");
                }
                ((TextView) findViewById(R.id.tv_detail_ingredients)).setText(sb.toString().trim());
                ((TextView) findViewById(R.id.tv_detail_steps)).setText(recipe.getSteps());
            });
        });
    }

    /** Shows 3 instead of 3.0, but keeps 0.5 as 0.5. */
    private String formatQuantity(double q) {
        return (q == Math.rint(q)) ? String.valueOf((long) q) : String.valueOf(q);
    }
}