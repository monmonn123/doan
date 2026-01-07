package com.example.doan_mau;

public class ReportReason {
    public String value;
    public String label;

    public ReportReason() {}

    public ReportReason(String value, String label) {
        this.value = value;
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}

