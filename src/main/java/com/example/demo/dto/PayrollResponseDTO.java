package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class PayrollResponseDTO {

    private Long id;
    private String employeeName;
    private String employeeEmail;
    private int month;
    private int year;
    private Double baseSalary;
    private Double bonus;
    private Double deductions;
    private Double tax;
    private Double finalSalary;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime generatedAt;
    private String generatedByName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getEmployeeEmail() { return employeeEmail; }
    public void setEmployeeEmail(String employeeEmail) { this.employeeEmail = employeeEmail; }
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public Double getBaseSalary() { return baseSalary; }
    public void setBaseSalary(Double baseSalary) { this.baseSalary = baseSalary; }
    public Double getBonus() { return bonus; }
    public void setBonus(Double bonus) { this.bonus = bonus; }
    public Double getDeductions() { return deductions; }
    public void setDeductions(Double deductions) { this.deductions = deductions; }
    public Double getTax() { return tax; }
    public void setTax(Double tax) { this.tax = tax; }
    public Double getFinalSalary() { return finalSalary; }
    public void setFinalSalary(Double finalSalary) { this.finalSalary = finalSalary; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    public String getGeneratedByName() { return generatedByName; }
    public void setGeneratedByName(String generatedByName) { this.generatedByName = generatedByName; }
}
