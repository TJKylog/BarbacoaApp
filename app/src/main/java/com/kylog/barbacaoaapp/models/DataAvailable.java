package com.kylog.barbacaoaapp.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataAvailable {

    @SerializedName("mesas")
    @Expose
    private List<Mesa> mesas = new ArrayList<Mesa>();
    @SerializedName("waiters")
    @Expose
    private List<Waiter> waiters = new ArrayList<Waiter>();

    /**
     * No args constructor for use in serialization
     *
     */
    public DataAvailable() {
    }

    /**
     *
     * @param mesas
     * @param waiters
     */
    public DataAvailable(List<Mesa> mesas, List<Waiter> waiters) {
        super();
        this.mesas = mesas;
        this.waiters = waiters;
    }

    public List<Mesa> getMesas() {
        return mesas;
    }

    public void setMesas(List<Mesa> mesas) {
        this.mesas = mesas;
    }

    public List<Waiter> getWaiters() {
        return waiters;
    }

    public void setWaiters(List<Waiter> waiters) {
        this.waiters = waiters;
    }

}
