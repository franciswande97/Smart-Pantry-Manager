package com.example.smartpantry;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Connects the list of PantryItem objects to the RecyclerView rows.
 * RecyclerView recycles row views as you scroll, and this adapter fills
 * each recycled row with the data for the item at that position.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    /** The fragment implements this so the adapter doesn't need to know about databases or dialogs. */
    public interface OnItemActionListener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private static final long DAY_MS = 24L * 60 * 60 * 1000;
    private static final long SOON_WINDOW_MS = 3 * DAY_MS;      // "expiring soon" = within 3 days
    private static final int COLOR_EXPIRED = 0xFFD32F2F;        // red
    private static final int COLOR_SOON = 0xFFF57C00;           // orange

    private final List<PantryItem> items = new ArrayList<>();
    private final OnItemActionListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private boolean highlightExpiry = true;                      // controlled by the Settings screen

    public PantryAdapter(OnItemActionListener listener) {
        this.listener = listener;
    }

    public void setHighlightExpiry(boolean enabled) {
        this.highlightExpiry = enabled;
    }

    /** Replaces the displayed data and redraws the list. */
    public void setItems(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);

        holder.name.setText(item.getName());
        holder.quantity.setText(formatQuantity(item.getQuantity()) + " " + item.getUnit());

        // Rows are recycled, so always reset the colour before deciding what this row needs
        holder.expiry.setTextColor(holder.defaultExpiryColor);

        if (item.getExpiryDate() != null) {
            long expiry = item.getExpiryDate();
            long now = System.currentTimeMillis();
            String dateText = dateFormat.format(new Date(expiry));
            holder.expiry.setVisibility(View.VISIBLE);

            if (highlightExpiry && now > expiry + DAY_MS) {
                holder.expiry.setText("Expired: " + dateText);
                holder.expiry.setTextColor(COLOR_EXPIRED);
            } else if (highlightExpiry && expiry - now <= SOON_WINDOW_MS) {
                holder.expiry.setText("Expiring soon: " + dateText);
                holder.expiry.setTextColor(COLOR_SOON);
            } else {
                holder.expiry.setText("Expires: " + dateText);
            }
        } else {
            holder.expiry.setVisibility(View.GONE);   // expiry is optional
        }

        holder.itemView.setOnClickListener(v -> listener.onEdit(item));
        holder.delete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /** "500.0" looks odd, so whole numbers are shown without the decimal part. */
    private String formatQuantity(double q) {
        return (q == Math.rint(q)) ? String.valueOf((long) q) : String.valueOf(q);
    }

    /** Holds references to the views of one row so findViewById isn't repeated on every scroll. */
    static class PantryViewHolder extends RecyclerView.ViewHolder {
        final TextView name, quantity, expiry;
        final ImageButton delete;
        final int defaultExpiryColor;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_name);
            quantity = itemView.findViewById(R.id.tv_quantity);
            expiry = itemView.findViewById(R.id.tv_expiry);
            delete = itemView.findViewById(R.id.btn_delete);
            defaultExpiryColor = expiry.getCurrentTextColor();   // theme colour, works in light and dark mode
        }
    }
}