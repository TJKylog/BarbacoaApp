package com.kylog.barbacaoaapp.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Note {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("waiter")
    @Expose
    private Waiter waiter;
    @SerializedName("consumes")
    @Expose
    private List<Consume> consumes = new ArrayList<Consume>();
    @SerializedName("total")
    @Expose
    private Double total;

    /**
     * No args constructor for use in serialization
     *
     */
    public Note() {
    }

    /**
     *
     * @param total
     * @param name
     * @param id
     * @param waiter
     * @param consumes
     */
    public Note(Integer id, String name, Waiter waiter, List<Consume> consumes, Double total) {
        super();
        this.id = id;
        this.name = name;
        this.waiter = waiter;
        this.consumes = consumes;
        this.total = total;
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

    public Waiter getWaiter() {
        return waiter;
    }

    public void setWaiter(Waiter waiter) {
        this.waiter = waiter;
    }

    public List<Consume> getConsumes() {
        return consumes;
    }

    public void setConsumes(List<Consume> consumes) {
        this.consumes = consumes;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String toString() {
        String note_string;
        note_string = "Mesa: "+name+"\n";
        note_string = note_string+"Mesero: "+waiter+"\n================================\n";
        note_string = note_string+"CANT PLATILLO        P/U  IMP"+"\n================================\n";

        for (Consume consume: this.consumes) {
            String con_name;
            if(consume.getName().length() > 13)
                con_name = consume.getName().substring(0, 14);
            else {
                con_name = consume.getName();
            }
            note_string = note_string+consume.getAmount().toString()+"  "+con_name+"  "+ consume.getPrice().toString()+"  "+consume.getAmountPrice().toString();
        }
        note_string = note_string+"\n================================\n"+"Total: "+this.total+"\n================================\n";

        return note_string;
    }
}
