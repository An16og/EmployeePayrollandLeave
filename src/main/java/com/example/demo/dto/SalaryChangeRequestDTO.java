package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class SalaryChangeRequestDTO {

    @NotNull(message = "Target employee ID is required")
    private Long targetEmployeeId;

    @NotNull(message = "Proposed salary is required")
    @PositiveOrZero(message = "Salary must be zero or positive")
    private Double newSalary;

    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    private String reason;

    public Long getTargetEmployeeId() { return targetEmployeeId; }
    public void setTargetEmployeeId(Long targetEmployeeId) { this.targetEmployeeId = targetEmployeeId; }
    public Double getNewSalary() { return newSalary; }
    public void setNewSalary(Double newSalary) { this.newSalary = newSalary; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
