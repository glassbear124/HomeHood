package com.homefood.model;

import java.io.Serializable;

/**
 * Created by Vishh.Makasana on 10/18/2016.
 */

public class Chef implements Serializable
{
    private String created_date;

    private String dinner_menu;

    private String phone;

    private String services;

    private String veg_nonveg;

    private String hours;

    private String id;

    private String special_menu;

    private String email;

    private String address;

    private String name;

    private String food_culture;

    private String longitude;

    private String lunch_menu;

    private String profile_pic;

    private String latitude;

    private String discovery;

    private String deadline;

    private String specialty;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    private String distance;

    public String getCreated_date ()
    {
        return created_date;
    }

    public void setCreated_date (String created_date)
    {
        this.created_date = created_date;
    }

    public String getDinner_menu ()
    {
        return dinner_menu;
    }

    public void setDinner_menu (String dinner_menu)
    {
        this.dinner_menu = dinner_menu;
    }

    public String getPhone ()
    {
        return phone;
    }

    public void setPhone (String phone)
    {
        this.phone = phone;
    }

    public String getServices ()
    {
        return services;
    }

    public void setServices (String services)
    {
        this.services = services;
    }

    public String getVeg_nonveg ()
    {
        return veg_nonveg;
    }

    public void setVeg_nonveg (String veg_nonveg)
    {
        this.veg_nonveg = veg_nonveg;
    }

    public String getHours ()
    {
        return hours;
    }

    public void setHours (String hours)
    {
        this.hours = hours;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getSpecial_menu ()
    {
        return special_menu;
    }

    public void setSpecial_menu (String special_menu)
    {
        this.special_menu = special_menu;
    }

    public String getEmail ()
    {
        return email;
    }

    public void setEmail (String email)
    {
        this.email = email;
    }

    public String getAddress ()
    {
        return address;
    }

    public void setAddress (String address)
    {
        this.address = address;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getFood_culture ()
    {
        return food_culture;
    }

    public void setFood_culture (String food_culture)
    {
        this.food_culture = food_culture;
    }

    public String getLongitude ()
    {
        return longitude;
    }

    public void setLongitude (String longitude)
    {
        this.longitude = longitude;
    }

    public String getLunch_menu ()
    {
        return lunch_menu;
    }

    public void setLunch_menu (String lunch_menu)
    {
        this.lunch_menu = lunch_menu;
    }

    public String getProfile_pic ()
    {
        return profile_pic;
    }

    public void setProfile_pic (String profile_pic)
    {
        this.profile_pic = profile_pic;
    }

    public String getLatitude ()
    {
        return latitude;
    }

    public void setLatitude (String latitude)
    {
        this.latitude = latitude;
    }

    public String getDiscovery ()
    {
        return discovery;
    }

    public void setDiscovery (String discovery)
    {
        this.discovery = discovery;
    }

    public String getDeadline ()
    {
        return deadline;
    }

    public void setDeadline (String deadline)
    {
        this.deadline = deadline;
    }

    public String getSpecialty ()
    {
        return specialty;
    }

    public void setSpecialty (String specialty)
    {
        this.specialty = specialty;
    }

    @Override
    public String toString()
    {
        return "Chef [created_date = "+created_date+", dinner_menu = "+dinner_menu+", phone = "+phone+", services = "+services+", veg_nonveg = "+veg_nonveg+", hours = "+hours+", id = "+id+", special_menu = "+special_menu+", email = "+email+", address = "+address+", name = "+name+", food_culture = "+food_culture+", longitude = "+longitude+", lunch_menu = "+lunch_menu+", profile_pic = "+profile_pic+", latitude = "+latitude+", discovery = "+discovery+", deadline = "+deadline+", specialty = "+specialty+"]";
    }
}
