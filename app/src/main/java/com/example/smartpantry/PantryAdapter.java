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

    private final List<PantryItem> items = new ArrayList<>();
    private final OnItemActionListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public PantryAdapter(OnItemActionListener listener) {
        this.listener = listener;
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
        View itemView = holder.itemView;

        holder.name.setText(item.getName());
        holder.quantity.setText(itemView.getContext().getString(R.string.quantity_unit_format, formatQuantity(item.getQuantity()), item.getUnit()));

        if (item.getExpiryDate() != null) {
            holder.expiry.setVisibility(View.VISIBLE);
            holder.expiry.setText(itemView.getContext().getString(R.string.expires_format, dateFormat.format(new Date(item.getExpiryDate()))));
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
    public static class PantryViewHolder extends RecyclerView.ViewHolder {
        final TextView name, quantity, expiry;
        final ImageButton delete;

        public PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_name);
            quantity = itemView.findViewById(R.id.tv_quantity);
            expiry = itemView.findViewById(R.id.tv_expiry);
            delete = itemView.findViewById(R.id.btn_delete);
        }
    }
}