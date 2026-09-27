package com.shikharsingh.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    public interface OnItemActionListener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private final List<PantryItem> items;
    private final OnItemActionListener listener;

    public PantryAdapter(List<PantryItem> items, OnItemActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.textName.setText(item.getName());
        holder.textQuantity.setText(item.getQuantity() + " " + item.getUnit());

        String expiry = item.getExpiryDate();
        boolean expired = ExpiryUtils.isExpired(expiry);
        boolean expiringSoon = ExpiryUtils.isExpiringSoon(expiry, 3);

        if (expiry == null || expiry.trim().isEmpty()) {
            holder.textExpiry.setText("No expiry set");
            holder.warningIcon.setVisibility(View.GONE);
            holder.itemView.setAlpha(1.0f);
        } else if (expired) {
            holder.textExpiry.setText("Expired: " + expiry);
            holder.warningIcon.setImageResource(R.drawable.expired);
            holder.warningIcon.setVisibility(View.VISIBLE);
            // Visual grey-out substitutes for auto-deleting expired items, user still controls removal
            holder.itemView.setAlpha(0.5f); // grey out expired items
        } else if (expiringSoon) {
            holder.textExpiry.setText("Expires soon: " + expiry);
            holder.warningIcon.setImageResource(R.drawable.expiring_soon);
            holder.warningIcon.setVisibility(View.VISIBLE);
            holder.itemView.setAlpha(1.0f);
        } else {
            holder.textExpiry.setText("Expires: " + expiry);
            holder.warningIcon.setVisibility(View.GONE);
            holder.itemView.setAlpha(1.0f);
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(item));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textQuantity, textExpiry;
        ImageView warningIcon;
        View btnEdit, btnDelete;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textItemName);
            textQuantity = itemView.findViewById(R.id.textItemQuantity);
            textExpiry = itemView.findViewById(R.id.textItemExpiry);
            warningIcon = itemView.findViewById(R.id.iconWarning);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}