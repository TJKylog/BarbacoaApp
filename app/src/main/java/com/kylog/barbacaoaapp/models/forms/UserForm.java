package com.kylog.barbacaoaapp.models.forms;

public class UserForm {
    private String name;
    private String email;
    private String first_lastname;
    private String second_lastname;
    private String password;
    private String role;

    public UserForm(String name, String email, String first_lastname, String second_lastname, String password, String role) {
        this.name = name;
        this.email = email;
        this.first_lastname = first_lastname;
        this.second_lastname = second_lastname;
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

    public String getFirst_lastname() {
        return first_lastname;
    }

    public void setFirst_lastname(String first_lastname) {
        this.first_lastname = first_lastname;
    }

    public String getSecond_lastname() {
        return second_lastname;
    }

    public void setSecond_lastname(String second_lastname) {
        this.second_lastname = second_lastname;
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
