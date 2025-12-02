package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DailyBudgetAdapter extends RecyclerView.Adapter<DailyBudgetAdapter.ViewHolder> {

    private List<DailyBudgetItem> items;

    public DailyBudgetAdapter(List<DailyBudgetItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_daily_budget, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DailyBudgetItem item = items.get(position);

        holder.tvTanggal.setText(item.getTanggalLabel());
        holder.tvBudgetReal.setText(
                "Budget : " + formatCurrency(item.getBudget()) + "\n" +
                        "Real   : " + formatCurrency(item.getReal())
        );

        // Atur status icon
        if (item.isOverBudget()) {
            holder.iconCross.setVisibility(View.VISIBLE);
            holder.iconCheck.setVisibility(View.GONE);
        } else {
            holder.iconCross.setVisibility(View.GONE);
            holder.iconCheck.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTanggal, tvBudgetReal;
        View iconCross, iconCheck;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTanggal = itemView.findViewById(R.id.tv_hari_tanggal);
            tvBudgetReal = itemView.findViewById(R.id.tv_budget_real);
            iconCross = itemView.findViewById(R.id.btn_cross);
            iconCheck = itemView.findViewById(R.id.btn_check);
        }
    }

    private String formatCurrency(float amount) {
        return "Rp. " + String.format("%,.0f", amount).replace(",", ".");
    }
}