package com.kylog.barbacaoaapp.models;

public class BasicPackage {

    private Double amount;
    private Double price;
    private String name;

    public BasicPackage(Double amount, Double price, String name) {
        this.amount = amount;
        this.price = price;
        this.name = name;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
