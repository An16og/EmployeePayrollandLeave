package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "payroll",
        uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "month", "year"})
)
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    @Min(2000)
    private int year;
    @Min(1)
    @Max(12)
    @Column(nullable = false)
    private int month;

    private int workingDays;

    private int presentDays;

    private int paidLeaves;

    private int unpaidLeaves;
    @NotNull
    private Double baseSalary;
    @NotNull
    private Double deductions;
    @NotNull
    private Double bonus;
    @NotNull
    private Double tax;
    @NotNull
    private Double finalSalary;

    @Column(nullable = false, updatable = false)
    private LocalDateTime generatedAt;

    @ManyToOne
    @JoinColumn(name = "generated_by_id")
    private Employee generatedBy;

    @PrePersist
    public void prePersist() {
        this.generatedAt = LocalDateTime.now();
    }
}