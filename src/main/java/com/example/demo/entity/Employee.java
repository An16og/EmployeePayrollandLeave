package com.example.demo.entity;

import com.example.demo.entity.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false, unique=true)
    private String email;

    @Column(nullable=false)
    private String password;

    @Positive
    @Column(nullable=false)
    private int age;

    @Enumerated(EnumType.STRING)
    private Role role = Role.EMPLOYEE;

    private String designation;

    @Positive
    private int salary;

    private int paidLeaveBalance = 14;
    private int sickLeaveBalance = 14;
@ManyToOne
@JoinColumn(name="manager_id")
private Employee managerId;

@Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;
}