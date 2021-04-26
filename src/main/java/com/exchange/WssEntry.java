package com.exchange;

public class WssEntry implements Comparable<WssEntry>{
    private double price;
    private double amount;

    public WssEntry(double price, double amount) {
        this.price = price;
        this.amount = amount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "[ " + price +", " + amount + " ]";
    }

    @Override
    public int compareTo(WssEntry o) {
        return Double.compare(o.getPrice(), getPrice()); //descending
    }
}
