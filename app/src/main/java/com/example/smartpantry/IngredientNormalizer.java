package com.example.smartpantry;

import java.util.Locale;

/**
 * Cleans ingredient names so that "Tomatoes", " tomato " and "TOMATO"
 * all become the same string. The matcher compares these cleaned names.
 */
public final class IngredientNormalizer {

    private IngredientNormalizer() { }   // utility class: no instances

    public static String normalizeName(String raw) {
        if (raw == null) return "";

        // lower-case, remove punctuation/digits, collapse repeated spaces
        String s = raw.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
        if (s.isEmpty()) return s;

        // Only the last word is singularised: "red onions" -> "red onion"
        int lastSpace = s.lastIndexOf(' ');
        String head = (lastSpace == -1) ? "" : s.substring(0, lastSpace + 1);
        String last = s.substring(lastSpace + 1);
        return head + singularize(last);
    }

    /** Simple rule-based singular form. Not full NLP, but covers common food words. */
    static String singularize(String w) {
        if (w.length() <= 3) return w;                                   // "pea", "ham"
        if (w.endsWith("ies")) return w.substring(0, w.length() - 3) + "y";   // berries -> berry
        if (w.endsWith("oes")) return w.substring(0, w.length() - 2);         // tomatoes -> tomato
        if (w.endsWith("ches") || w.endsWith("shes")
                || w.endsWith("sses") || w.endsWith("xes")) {
            return w.substring(0, w.length() - 2);                            // peaches -> peach
        }
        if (w.endsWith("ss") || w.endsWith("us") || w.endsWith("is")) return w; // couscous, hummus
        if (w.endsWith("s")) return w.substring(0, w.length() - 1);           // eggs -> egg
        return w;
    }
}