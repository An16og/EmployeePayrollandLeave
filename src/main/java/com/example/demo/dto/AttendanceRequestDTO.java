package com.example.demo.dto;

import com.example.demo.entity.AttendenceTypes;
import jakarta.validation.constraints.NotNull;

public class AttendanceRequestDTO {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Attendance status is required")
    private AttendenceTypes status;

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public AttendenceTypes getStatus() { return status; }
    public void setStatus(AttendenceTypes status) { this.status = status; }
}
