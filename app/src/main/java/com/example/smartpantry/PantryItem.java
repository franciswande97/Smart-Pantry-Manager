package com.example.smartpantry;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * One ingredient the user currently has at home.
 * Each instance maps to one row in the "pantry_items" table.
 */
@Entity(tableName = "pantry_items")
public class PantryItem {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String name = "";

    private double quantity;

    @NonNull
    private String unit = "";

    private Long expiryDate;   // epoch millis; null = no expiry date (optional)

    public PantryItem() { }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @NonNull public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    @NonNull public String getUnit() { return unit; }
    public void setUnit(@NonNull String unit) { this.unit = unit; }

    public Long getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Long expiryDate) { this.expiryDate = expiryDate; }
}