package com.kylog.barbacaoaapp.models.forms;

public class UserForm {
    private String name;
    private String email;
    private String firstLastname;
    private String secondLastname;
    private String password;
    private String role;

    public UserForm(String name, String email, String firstLastname, String secondLastname, String password, String role) {
        this.name = name;
        this.email = email;
        this.firstLastname = firstLastname;
        this.secondLastname = secondLastname;
        this.password = password;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstLastname() {
        return firstLastname;
    }

    public void setFirstLastname(String firstLastname) {
        this.firstLastname = firstLastname;
    }

    public String getSecondLastname() {
        return secondLastname;
    }

    public void setSecondLastname(String secondLastname) {
        this.secondLastname = secondLastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
