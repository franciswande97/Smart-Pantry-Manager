package com.example.smartpantry;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Add/Edit Ingredient screen. Launched with an Intent:
 *  - no extra            -> "add" mode (blank form)
 *  - EXTRA_ITEM_ID given -> "edit" mode (form pre-filled from the database)
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "extra_item_id";

    /** Only units that UnitConverter understands, so every saved item can be matched. */
    private static final List<String> UNITS =
            Arrays.asList("g", "kg", "ml", "l", "tsp", "tbsp", "cup", "pcs", "slices", "cloves");

    private TextInputLayout tilName, tilQuantity, tilUnit, tilExpiry;
    private TextInputEditText etName, etQuantity, etExpiry;
    private MaterialAutoCompleteTextView actvUnit;

    private PantryItem editingItem;   // null in add mode
    private Long expiryMillis;        // null = no expiry date
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_ingredient);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_edit_root), (v, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return windowInsets;
        });

        bindViews();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        actvUnit.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, UNITS));

        etExpiry.setOnClickListener(v -> showDatePicker());
        tilExpiry.setEndIconOnClickListener(v -> {      // the "x" icon clears the optional date
            expiryMillis = null;
            etExpiry.setText("");
        });

        ((MaterialButton) findViewById(R.id.btn_save)).setOnClickListener(v -> saveIngredient());

        // Read the Intent: did the caller pass an item id?
        int itemId = getIntent().getIntExtra(EXTRA_ITEM_ID, -1);
        if (itemId != -1) {
            toolbar.setTitle("Edit ingredient");
            loadItem(itemId);
        }
    }

    private void bindViews() {
        tilName = findViewById(R.id.til_name);
        tilQuantity = findViewById(R.id.til_quantity);
        tilUnit = findViewById(R.id.til_unit);
        tilExpiry = findViewById(R.id.til_expiry);
        etName = findViewById(R.id.et_name);
        etQuantity = findViewById(R.id.et_quantity);
        etExpiry = findViewById(R.id.et_expiry);
        actvUnit = findViewById(R.id.actv_unit);
    }

    /** Edit mode: fetch the item on a background thread, then fill the form on the UI thread. */
    private void loadItem(int id) {
        AppDatabase.databaseExecutor.execute(() -> {
            PantryItem item = AppDatabase.getInstance(getApplicationContext())
                    .pantryItemDao().getById(id);
            runOnUiThread(() -> {
                if (item == null) { finish(); return; }
                editingItem = item;
                etName.setText(item.getName());
                etQuantity.setText(String.valueOf(item.getQuantity()));
                actvUnit.setText(item.getUnit(), false);   // false = don't filter the dropdown
                expiryMillis = item.getExpiryDate();
                if (expiryMillis != null) etExpiry.setText(dateFormat.format(new Date(expiryMillis)));
            });
        });
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Expiry date")
                .setSelection(expiryMillis != null ? expiryMillis : MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        picker.addOnPositiveButtonClickListener(selection -> {
            expiryMillis = selection;
            etExpiry.setText(dateFormat.format(new Date(selection)));
        });
        picker.show(getSupportFragmentManager(), "expiry_picker");
    }

    /** Validates the form; only if every field is valid does it write to the database. */
    private void saveIngredient() {
        String name = etName.getText() == null ? "" : etName.getText().toString().trim();
        String qtyText = etQuantity.getText() == null ? "" : etQuantity.getText().toString().trim();
        String unit = actvUnit.getText() == null ? "" : actvUnit.getText().toString().trim();

        boolean valid = true;

        if (name.isEmpty()) {
            tilName.setError("Please enter an ingredient name");
            valid = false;
        } else if (!name.matches(".*[A-Za-z].*")) {
            tilName.setError("Name must contain letters");
            valid = false;
        } else {
            tilName.setError(null);
        }

        double quantity = 0;
        if (qtyText.isEmpty()) {
            tilQuantity.setError("Enter a quantity");
            valid = false;
        } else {
            try {
                quantity = Double.parseDouble(qtyText.replace(',', '.'));   // some keyboards use a comma
                if (quantity <= 0) {
                    tilQuantity.setError("Quantity must be greater than 0");
                    valid = false;
                } else {
                    tilQuantity.setError(null);
                }
            } catch (NumberFormatException e) {
                tilQuantity.setError("Enter a valid number");
                valid = false;
            }
        }

        if (!UNITS.contains(unit)) {
            tilUnit.setError("Choose a unit");
            valid = false;
        } else {
            tilUnit.setError(null);
        }

        if (!valid) return;   // stay on the form so the user can fix the errors

        final PantryItem item = (editingItem != null) ? editingItem : new PantryItem();
        item.setName(name);
        item.setQuantity(quantity);
        item.setUnit(unit);
        item.setExpiryDate(expiryMillis);
        final boolean isEdit = editingItem != null;

        AppDatabase.databaseExecutor.execute(() -> {
            PantryItemDao dao = AppDatabase.getInstance(getApplicationContext()).pantryItemDao();
            if (isEdit) dao.update(item); else dao.insert(item);

            runOnUiThread(() -> {
                Toast.makeText(this, isEdit ? "Ingredient updated" : "Ingredient added",
                        Toast.LENGTH_SHORT).show();
                finish();   // back to the list, which reloads itself in onResume()
            });
        });
    }
}