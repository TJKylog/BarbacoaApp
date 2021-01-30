package com.kylog.barbacaoaapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Consume {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("measure")
    @Expose
    private String measure;
    @SerializedName("price")
    @Expose
    private Double price;
    @SerializedName("amount")
    @Expose
    private Double amount;
    @SerializedName("amount_price")
    @Expose
    private Double amountPrice;

    /**
     * No args constructor for use in serialization
     *
     */
    public Consume() {
    }

    /**
     *
     * @param amount
     * @param measure
     * @param price
     * @param name
     * @param id
     * @param amountPrice
     */
    public Consume(Integer id, String name, String measure, Double price, Double amount, Double amountPrice) {
        super();
        this.id = id;
        this.name = name;
        this.measure = measure;
        this.price = price;
        this.amount = amount;
        this.amountPrice = amountPrice;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getAmountPrice() {
        return amountPrice;
    }

    public void setAmountPrice(Double amountPrice) {
        this.amountPrice = amountPrice;
    }

}
