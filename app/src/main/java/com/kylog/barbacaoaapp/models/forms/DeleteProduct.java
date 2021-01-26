package com.kylog.barbacaoaapp.models.forms;

public class DeleteProduct {
    private Integer product_id;

    public DeleteProduct() {

    }

    public DeleteProduct(Integer product_id) {
        this.product_id = product_id;
    }

    public Integer getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Integer product_id) {
        this.product_id = product_id;
    }
}
