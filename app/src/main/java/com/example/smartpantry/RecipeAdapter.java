package com.example.smartpantry;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/** Shows the suggested recipes as cards, each with a short preview of its ingredients. */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    /** One display row: the recipe plus its pre-built ingredient preview text. */
    public static class Row {
        final Recipe recipe;
        final String ingredientSummary;

        public Row(Recipe recipe, String ingredientSummary) {
            this.recipe = recipe;
            this.ingredientSummary = ingredientSummary;
        }
    }

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private final List<Row> rows = new ArrayList<>();
    private final OnRecipeClickListener listener;

    public RecipeAdapter(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    public void setRows(List<Row> newRows) {
        rows.clear();
        rows.addAll(newRows);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Row row = rows.get(position);
        holder.name.setText(row.recipe.getName());
        holder.ingredients.setText(row.ingredientSummary);
        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(row.recipe));
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        final TextView name, ingredients;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_recipe_name);
            ingredients = itemView.findViewById(R.id.tv_recipe_ingredients);
        }
    }
}