package com.kylog.barbacaoaapp.models.forms;

public class SendEmailForm {
    private String email;

    public SendEmailForm () { }

    public SendEmailForm(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
