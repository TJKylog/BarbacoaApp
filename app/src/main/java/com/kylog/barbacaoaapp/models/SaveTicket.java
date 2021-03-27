package com.kylog.barbacaoaapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SaveTicket {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("invoice")
    @Expose
    private Integer invoice;

    /**
     * No args constructor for use in serialization
     *
     */
    public SaveTicket() {
    }

    /**
     *
     * @param invoice
     * @param message
     */
    public SaveTicket(String message, Integer invoice) {
        super();
        this.message = message;
        this.invoice = invoice;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SaveTicket withMessage(String message) {
        this.message = message;
        return this;
    }

    public Integer getInvoice() {
        return invoice;
    }

    public void setInvoice(Integer invoice) {
        this.invoice = invoice;
    }

    public SaveTicket withInvoice(Integer invoice) {
        this.invoice = invoice;
        return this;
    }

}