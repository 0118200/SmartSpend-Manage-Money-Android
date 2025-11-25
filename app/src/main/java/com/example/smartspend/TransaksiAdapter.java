package com.example.smartspend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartspend.model.Transaksi;

import java.util.List;

public class TransaksiAdapter extends RecyclerView.Adapter<TransaksiAdapter.ViewHolder> {

    private List<Transaksi> daftar;

    public TransaksiAdapter(List<Transaksi> list) {
        this.daftar = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaksi t = daftar.get(position);
        holder.teks1.setText(t.judul);
        holder.teks2.setText("Rp" + t.jumlah + " - " + t.isi);
    }

    @Override
    public int getItemCount() {
        return daftar.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView teks1, teks2;

        ViewHolder(View itemView) {
            super(itemView);
            teks1 = itemView.findViewById(android.R.id.text1);
            teks2 = itemView.findViewById(android.R.id.text2);
        }
    }
}