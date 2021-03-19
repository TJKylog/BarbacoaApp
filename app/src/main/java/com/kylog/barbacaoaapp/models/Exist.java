package com.kylog.barbacaoaapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Exist {

    @SerializedName("exist")
    @Expose
    private Boolean exist;

    /**
     * No args constructor for use in serialization
     *
     */
    public Exist() {
    }

    /**
     *
     * @param exist
     */
    public Exist(Boolean exist) {
        super();
        this.exist = exist;
    }

    public Boolean getExist() {
        return exist;
    }

    public void setExist(Boolean exist) {
        this.exist = exist;
    }

    public Exist withExist(Boolean exist) {
        this.exist = exist;
        return this;
    }

}
