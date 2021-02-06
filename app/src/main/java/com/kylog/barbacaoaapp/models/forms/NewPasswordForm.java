package com.kylog.barbacaoaapp.models.forms;

public class NewPasswordForm {
    private String email;
    private String code;
    private String password;
    private String repeat_password;

    public NewPasswordForm() {
    }

    public NewPasswordForm(String email, String code, String password, String repeat_password) {
        this.email = email;
        this.code = code;
        this.password = password;
        this.repeat_password = repeat_password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeat_password() {
        return repeat_password;
    }

    public void setRepeat_password(String repeat_password) {
        this.repeat_password = repeat_password;
    }
}
