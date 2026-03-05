package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "payroll",
        uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id","month","year"})
)
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int month;

    private int workingDays;

    private int presentDays;

    private int paidLeaves;

    private int unpaidLeaves;

    private int baseSalary;

    private int deductions;
    private int bonus;
    private int tax;
    private int finalSalary;

    private LocalDateTime generatedAt;
}