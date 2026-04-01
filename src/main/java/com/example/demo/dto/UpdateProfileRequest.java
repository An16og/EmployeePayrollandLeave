package com.example.demo.dto;

public class UpdateProfileRequest {

    // Employees can only update their own name and designation
    private String name;
    private String designation;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
}
