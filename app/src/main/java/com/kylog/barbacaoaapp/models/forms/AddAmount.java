package com.kylog.barbacaoaapp.models.forms;

public class AddAmount {
    private Integer product_id;
    private Double amount;

    public AddAmount(){

    }

    public AddAmount(Integer product_id, Double amount) {
        this.product_id = product_id;
        this.amount = amount;
    }

    public Integer getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Integer product_id) {
        this.product_id = product_id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
