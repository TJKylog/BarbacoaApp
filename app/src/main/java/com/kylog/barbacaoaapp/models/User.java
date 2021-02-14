package com.kylog.barbacaoaapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("first_lastname")
    @Expose
    private String firstLastname;
    @SerializedName("second_lastname")
    @Expose
    private String secondLastname;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("role_id")
    @Expose
    private Integer roleId;

    /**
     * No args constructor for use in serialization
     *
     */
    public User() {
    }

    /**
     *
     * @param firstLastname
     * @param role
     * @param roleId
     * @param secondLastname
     * @param name
     * @param id
     * @param email
     */
    public User(Integer id, String name, String email, String firstLastname, String secondLastname, String role, Integer roleId) {
        super();
        this.id = id;
        this.name = name;
        this.email = email;
        this.firstLastname = firstLastname;
        this.secondLastname = secondLastname;
        this.role = role;
        this.roleId = roleId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User withId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User withName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User withEmail(String email) {
        this.email = email;
        return this;
    }

    public String getFirstLastname() {
        return firstLastname;
    }

    public void setFirstLastname(String firstLastname) {
        this.firstLastname = firstLastname;
    }

    public User withFirstLastname(String firstLastname) {
        this.firstLastname = firstLastname;
        return this;
    }

    public String getSecondLastname() {
        return secondLastname;
    }

    public void setSecondLastname(String secondLastname) {
        this.secondLastname = secondLastname;
    }

    public User withSecondLastname(String secondLastname) {
        this.secondLastname = secondLastname;
        return this;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public User withRole(String role) {
        this.role = role;
        return this;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public User withRoleId(Integer roleId) {
        this.roleId = roleId;
        return this;
    }
}