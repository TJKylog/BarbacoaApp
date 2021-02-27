package com.kylog.barbacaoaapp.models.forms;

public class DoneTicketForm {
    private Double payment;
    private Double change;
    private String payment_method;

    public DoneTicketForm(){ }

    public DoneTicketForm(String payment_method,Double payment, Double change) {
        this.payment = payment;
        this.change = change;
        this.payment_method = payment_method;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public Double getPayment() {
        return payment;
    }

    public void setPayment(Double payment) {
        this.payment = payment;
    }

    public Double getChange() {
        return change;
    }

    public void setChange(Double change) {
        this.change = change;
    }
}
