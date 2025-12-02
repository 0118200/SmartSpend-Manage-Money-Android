package com.example.myapplication;

public class DailyBudgetItem {
    private String tanggalLabel; // "Hari ke 1 : 1 Oktober 2025"
    private float budget;
    private float real;

    public DailyBudgetItem(String tanggalLabel, float budget, float real) {
        this.tanggalLabel = tanggalLabel;
        this.budget = budget;
        this.real = real;
    }

    public String getTanggalLabel() { return tanggalLabel; }
    public float getBudget() { return budget; }
    public float getReal() { return real; }
    public boolean isOverBudget() { return real > budget; }
}