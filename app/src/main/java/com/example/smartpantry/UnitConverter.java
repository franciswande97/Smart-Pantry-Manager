package com.example.smartpantry;

import java.util.Locale;

/**
 * Converts any supported unit into a base unit so quantities can be compared:
 * mass -> grams, volume -> millilitres, countable items -> pieces.
 * Example: 1 kg and 1000 g are the same amount once both are in grams.
 */
public final class UnitConverter {

    public enum Dimension { MASS, VOLUME, COUNT, UNKNOWN }

    /** A quantity expressed in its base unit. */
    public static final class Measure {
        public final double amount;
        public final Dimension dimension;

        Measure(double amount, Dimension dimension) {
            this.amount = amount;
            this.dimension = dimension;
        }
    }

    private UnitConverter() { }

    public static Measure toBase(double quantity, String unit) {
        String u = (unit == null) ? "" : unit.toLowerCase(Locale.ROOT).trim().replace(".", "");

        switch (u) {
            case "g": case "gram": case "grams":
                return new Measure(quantity, Dimension.MASS);
            case "kg": case "kilogram": case "kilograms":
                return new Measure(quantity * 1000, Dimension.MASS);

            case "ml": case "millilitre": case "milliliter":
                return new Measure(quantity, Dimension.VOLUME);
            case "l": case "litre": case "litres": case "liter": case "liters":
                return new Measure(quantity * 1000, Dimension.VOLUME);
            case "tsp": case "teaspoon": case "teaspoons":
                return new Measure(quantity * 5, Dimension.VOLUME);
            case "tbsp": case "tablespoon": case "tablespoons":
                return new Measure(quantity * 15, Dimension.VOLUME);
            case "cup": case "cups":
                return new Measure(quantity * 250, Dimension.VOLUME);   // metric cup

            case "pcs": case "pc": case "piece": case "pieces": case "whole":
            case "clove": case "cloves": case "slice": case "slices": case "":
                return new Measure(quantity, Dimension.COUNT);

            default:
                return new Measure(quantity, Dimension.UNKNOWN);
        }
    }
}