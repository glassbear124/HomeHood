package com.homefood.model;

/**
 * Created by Vishh.Makasana on 10/18/2016.
 */

public class User {

    private String created_date;

    private String id;

    private String email;

    private String name;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private String phone;

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User [created_date = " + created_date + ", id = " + id + ", phone = " + phone + ", email = " + email + ", name = " + name + "]";
    }
}
