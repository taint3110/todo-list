package com.example.todoapp;

import com.google.gson.annotations.SerializedName;

public class Employee {
    @SerializedName("id")
    private int id;

    @SerializedName("employee_name")
    private String name;

    @SerializedName("employee_age")
    private String age;

    @SerializedName("employee_salary")
    private String salary;

    @SerializedName("profile_image")
    private String profileImage;

    // Getters
    public String getId() { return String.valueOf(id); }
    public String getName() { return name; }
    public int getAge() { 
        try { return Integer.parseInt(age); } 
        catch (Exception e) { return 0; }
    }
    public int getSalary() { 
        try { return Integer.parseInt(salary); } 
        catch (Exception e) { return 0; }
    }
    public String getProfileImage() { return profileImage; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAge(String age) { this.age = age; }
    public void setSalary(String salary) { this.salary = salary; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
}
