package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Tracks the HR → Finance salary change workflow.
 * HR raises a request, Finance approves or rejects it.
 */
@Entity
@Table(name = "salary_change_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalaryChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // HR employee who raised this request
    @ManyToOne
    @JoinColumn(name = "requested_by_id", nullable = false)
    private Employee requestedBy;

    // Employee whose salary is being changed
    @ManyToOne
    @JoinColumn(name = "target_employee_id", nullable = false)
    private Employee targetEmployee;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Double currentSalary;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Double proposedSalary;

    @Size(max = 500)
    @Column(length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus status = ApprovalStatus.PENDING;

    // FINANCE employee who reviewed the request
    @ManyToOne
    @JoinColumn(name = "reviewed_by_id")
    private Employee reviewedBy;

    @Size(max = 500)
    @Column(length = 500)
    private String reviewNote;

    @Column(nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    private LocalDateTime reviewedAt;

    @PrePersist
    public void prePersist() {
        this.requestedAt = LocalDateTime.now();
    }
}
