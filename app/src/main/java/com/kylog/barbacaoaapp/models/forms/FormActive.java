package com.kylog.barbacaoaapp.models.forms;

public class FormActive {
    private Integer user_id;
    private Integer mesa_id;

    public FormActive(Integer user_id, Integer mesa_id) {
        this.user_id = user_id;
        this.mesa_id = mesa_id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getMesa_id() {
        return mesa_id;
    }

    public void setMesa_id(Integer mesa_id) {
        this.mesa_id = mesa_id;
    }
}
