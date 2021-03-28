package com.kylog.barbacaoaapp.models.forms;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kylog.barbacaoaapp.models.BasicPackage;
import com.kylog.barbacaoaapp.models.Others;

import java.util.List;

public class EventInfo {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("hour")
    @Expose
    private String hour;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("total")
    @Expose
    private Double total;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("extras_list")
    @Expose
    private List<Others> extras_list;
    @SerializedName("basic_package")
    @Expose
    private List<BasicPackage> basic_package;
    @SerializedName("customer_name")
    @Expose
    private String customer_name;
    @SerializedName("advance_payment")
    @Expose
    private Double advance_payment;

    public EventInfo(List<Others> extras_list, List<BasicPackage> basic_package, Double total, Double advance_payment, String customer_name, String address, String phone, String date, String hour) {
        this.extras_list = extras_list;
        this.basic_package = basic_package;
        this.total = total;
        this.advance_payment = advance_payment;
        this.customer_name = customer_name;
        this.address = address;
        this.phone = phone;
        this.date = date;
        this.hour = hour;
    }

    public List<Others> getExtras_list() {
        return extras_list;
    }

    public void setExtras_list(List<Others> extras_list) {
        this.extras_list = extras_list;
    }

    public List<BasicPackage> getBasic_package() {
        return basic_package;
    }

    public void setBasic_package(List<BasicPackage> basic_package) {
        this.basic_package = basic_package;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getAdvance_payment() {
        return advance_payment;
    }

    public void setAdvance_payment(Double advance_payment) {
        this.advance_payment = advance_payment;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String toString() {
        String event_info = "Fecha: "+this.date+" "+this.hour+"\n";

        event_info = event_info+"Nombre: "+this.customer_name+"\n";

        event_info = event_info+"Tel: "+this.phone+"\n";
        event_info = event_info+"Dereccion: "+this.address+"\n\n\n\n";
        event_info = event_info+"CANT NOMBRE        P/U  IMP"+"\n================================\n";

        for (BasicPackage basicPackage: this.basic_package ) {
            event_info = event_info+basicPackage.getAmount().toString()+" "+basicPackage.getName()+" $"+basicPackage.getPrice().toString()+"\n";
        }
        for (Others other:extras_list) {
            event_info = event_info+other.getAmount().toString()+" "+other.getName()+" $"+other.getPrice().toString()+"\n";
        }

        event_info = event_info+"\n================================\n"+"Total: "+this.total.toString()+"\nAnticipo: "+advance_payment.toString()+"\n================================\n";

        return  event_info;
    }

}
