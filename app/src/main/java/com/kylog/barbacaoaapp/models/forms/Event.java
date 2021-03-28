package com.kylog.barbacaoaapp.models.forms;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Event {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("is_completed")
    @Expose
    private boolean is_completed;
    @SerializedName("event_info")
    @Expose
    private EventInfo event_info;

    /**
     * No args constructor for use in serialization
     *
     */
    public Event() {
    }

    public Event(EventInfo event_info) {
        this.event_info = event_info;
    }

    public Event(Integer id,boolean is_completed, EventInfo event_info) {
        super();
        this.id = id;
        this.is_completed = is_completed;
        this.event_info = event_info;
    }

    public Event(boolean is_completed, EventInfo event_info) {
        this.is_completed = is_completed;
        this.event_info = event_info;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isIs_completed() {
        return is_completed;
    }

    public void setIs_completed(boolean is_completed) {
        this.is_completed = is_completed;
    }

    public EventInfo getEvent_info() {
        return event_info;
    }

    public void setEvent_info(EventInfo event_info) {
        this.event_info = event_info;
    }

    public String toString(){
        String event_string = this.event_info.toString();
        return event_string;
    }
}
