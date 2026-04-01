package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class SalaryChangeResponseDTO {

    private Long id;
    private String targetEmployeeName;
    private String targetEmployeeEmail;
    private Double currentSalary;
    private Double proposedSalary;
    private String reason;
    private String status;
    private String requestedByName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime requestedAt;
    private String reviewedByName;
    private String reviewNote;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTargetEmployeeName() { return targetEmployeeName; }
    public void setTargetEmployeeName(String targetEmployeeName) { this.targetEmployeeName = targetEmployeeName; }
    public String getTargetEmployeeEmail() { return targetEmployeeEmail; }
    public void setTargetEmployeeEmail(String targetEmployeeEmail) { this.targetEmployeeEmail = targetEmployeeEmail; }
    public Double getCurrentSalary() { return currentSalary; }
    public void setCurrentSalary(Double currentSalary) { this.currentSalary = currentSalary; }
    public Double getProposedSalary() { return proposedSalary; }
    public void setProposedSalary(Double proposedSalary) { this.proposedSalary = proposedSalary; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRequestedByName() { return requestedByName; }
    public void setRequestedByName(String requestedByName) { this.requestedByName = requestedByName; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
    public String getReviewedByName() { return reviewedByName; }
    public void setReviewedByName(String reviewedByName) { this.reviewedByName = reviewedByName; }
    public String getReviewNote() { return reviewNote; }
    public void setReviewNote(String reviewNote) { this.reviewNote = reviewNote; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
}
