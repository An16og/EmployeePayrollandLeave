package com.example.demo.dto;

import com.example.demo.entity.Role;
import jakarta.validation.constraints.*;

public class EmployeeRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Must be a valid email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Must be at least 18 years old")
    @Max(value = 80, message = "Age cannot exceed 80")
    private Integer age;

    private Role role = Role.EMPLOYEE;
    private String designation;

    @NotNull(message = "Salary is required")
    @PositiveOrZero(message = "Salary must be zero or positive")
    private Double salary;

    private Long managerId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public Double getSalary() { return salary; }
    public void setSalary(Double salary) { this.salary = salary; }
    public Long getManagerId() { return managerId; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }
}
