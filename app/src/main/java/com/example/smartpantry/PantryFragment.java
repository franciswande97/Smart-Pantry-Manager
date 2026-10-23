package com.example.smartpantry;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import android.content.Intent;

/** The Pantry List screen: shows every ingredient stored in the database. */
public class PantryFragment extends Fragment implements PantryAdapter.OnItemActionListener {

    private PantryAdapter adapter;
    private TextView emptyView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pantry, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.rv_pantry);
        emptyView = root.findViewById(R.id.tv_empty);
        FloatingActionButton fab = root.findViewById(R.id.fab_add);

        adapter = new PantryAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Wired to the Add/Edit screen in Step 6
        fab.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), AddEditIngredientActivity.class)));

        return root;
    }

    /** Reloading in onResume means the list refreshes automatically when we come back from Add/Edit. */
    @Override
    public void onResume() {
        super.onResume();
        loadItems();
    }

    private void loadItems() {
        Context appContext = requireContext().getApplicationContext();
        AppDatabase.databaseExecutor.execute(() -> {
            List<PantryItem> items = AppDatabase.getInstance(appContext).pantryItemDao().getAll();

            Activity activity = getActivity();
            if (activity == null) return;           // screen was closed while the query ran
            activity.runOnUiThread(() -> {
                adapter.setHighlightExpiry(AppPreferences.isExpiryHighlightEnabled(appContext));// UI may only be touched from the main thread
                adapter.setItems(items);
                emptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }

    @Override
    public void onEdit(PantryItem item) {
        Intent intent = new Intent(requireContext(), AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(intent);
    }

    @Override
    public void onDelete(PantryItem item) {
        // Confirm first so an accidental tap doesn't destroy data
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete ingredient")
                .setMessage("Remove " + item.getName() + " from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    Context appContext = requireContext().getApplicationContext();
                    AppDatabase.databaseExecutor.execute(() -> {
                        AppDatabase.getInstance(appContext).pantryItemDao().delete(item);
                        Activity activity = getActivity();
                        if (activity != null) activity.runOnUiThread(this::loadItems);
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}