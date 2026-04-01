package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AttendanceResponseDTO {

    private Long id;
    private String employeeName;
    private String employeeEmail;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private String status;
    private String markedByName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime markedOn;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getEmployeeEmail() { return employeeEmail; }
    public void setEmployeeEmail(String employeeEmail) { this.employeeEmail = employeeEmail; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMarkedByName() { return markedByName; }
    public void setMarkedByName(String markedByName) { this.markedByName = markedByName; }
    public LocalDateTime getMarkedOn() { return markedOn; }
    public void setMarkedOn(LocalDateTime markedOn) { this.markedOn = markedOn; }
}
