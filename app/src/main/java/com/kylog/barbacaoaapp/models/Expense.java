package com.kylog.barbacaoaapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Expense {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("approved_by")
    @Expose
    private String approvedBy;
    @SerializedName("reason")
    @Expose
    private String reason;
    @SerializedName("amount")
    @Expose
    private Double amount;
    @SerializedName("created_by")
    @Expose
    private Integer createdBy;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("created_by_name")
    @Expose
    private String createdByName;

    /**
     * No args constructor for use in serialization
     *
     */
    public Expense() {
    }

    /**
     *
     * @param reason
     * @param createdAt
     * @param createdByName
     * @param amount
     * @param createdBy
     * @param approvedBy
     * @param id
     */
    public Expense(Integer id, String approvedBy, String reason, Double amount, Integer createdBy, String createdAt, String createdByName) {
        super();
        this.id = id;
        this.approvedBy = approvedBy;
        this.reason = reason;
        this.amount = amount;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.createdByName = createdByName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
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

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String toString(){
        String string = "Autorizado por: "+ approvedBy+"\n"+
                        "Fecha: "+ createdAt +"\n"
                        +"Concepto: "+ reason +"\n"+
                        "Cantidad: "+amount.toString()+"\n\n\n\n\n\n\n\n\n"+
                        "================================\n"+
                        "\t\tFirma\n";
        return string;
    }

}