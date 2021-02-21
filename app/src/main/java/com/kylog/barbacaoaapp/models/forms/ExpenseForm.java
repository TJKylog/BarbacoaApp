package com.kylog.barbacaoaapp.models.forms;


public class ExpenseForm {
    private String approved_by;
    private String reason;
    private Double amount;

    /**
     * No args constructor for use in serialization
     *
     */
    public ExpenseForm() {
    }

    /**
     *
     * @param reason
     * @param amount
     * @param approvedBy
     */
    public ExpenseForm(String approvedBy, String reason, Double amount) {
        super();
        this.approved_by = approvedBy;
        this.reason = reason;
        this.amount = amount;
    }

    public String getApprovedBy() {
        return approved_by;
    }

    public void setApprovedBy(String approvedBy) {
        this.approved_by = approved_by;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

}