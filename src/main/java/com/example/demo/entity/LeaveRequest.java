package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "leave_requests")
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long leaveId;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveType leaveType;
    @Column(nullable = false)
    private LocalDate startDate;
    @Column(nullable = false)
    private LocalDate endDate;
    @Size(max = 500)
    @Column(length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus status = ApprovalStatus.PENDING;
    @Column(nullable = false,updatable = false)
    private LocalDateTime appliedAt;
    @PrePersist
    public void prePersist() {
        this.appliedAt = LocalDateTime.now();
        this.numberOfDays = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    @PreUpdate
    public void preUpdate() {
        this.numberOfDays = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    private int numberOfDays;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private Employee approvedBy;
}