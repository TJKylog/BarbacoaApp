package com.kylog.barbacaoaapp.models.forms;

public class DoneTicketForm {
    private Double payment;
    private Double change;

    public DoneTicketForm(){ }

    public DoneTicketForm(Double payment, Double change) {
        this.payment = payment;
        this.change = change;
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
