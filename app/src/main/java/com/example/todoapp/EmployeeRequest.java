package com.example.todoapp;

import com.google.gson.annotations.SerializedName;

public class EmployeeRequest {
    @SerializedName("employee_name")
    private String name;

    @SerializedName("employee_age")
    private int age;

    @SerializedName("employee_salary")
    private int salary;

    @SerializedName("profile_image")
    private String profileImage;

    public EmployeeRequest(String name, int age, int salary, String profileImage) {
        this.name = name;
        this.age = age;
        this.salary = salary;
        this.profileImage = profileImage;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public int getSalary() { return salary; }
    public void setSalary(int salary) { this.salary = salary; }
    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
}
