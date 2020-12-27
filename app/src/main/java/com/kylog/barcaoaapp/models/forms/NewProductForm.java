package com.kylog.barcaoaapp.models.forms;

public class NewProductForm {
    private String name;
    private Double price;
    private String measure;
    private String type;

    public NewProductForm() {

    }

    public NewProductForm(String name, Double price, String measure, String type) {
        super();
        this.name = name;
        this.price = price;
        this.measure = measure;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
