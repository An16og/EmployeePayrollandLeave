package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import com.example.demo.validation.StrongPassword;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @StrongPassword
    @Column(nullable = false)
    private String password;

    @Min(value = 18, message = "Must be at least 18 years old")
    @Max(value = 80)
    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role = Role.EMPLOYEE;

    private String designation;

    @NotNull
    @Column(nullable = false)
    @PositiveOrZero
    private Double salary;

    private int paidLeaveBalance = 14;
    private int sickLeaveBalance = 14;
    private int unpaidLeaveBalance = 0;

    // false = account deactivated by ADMIN
    private boolean active = true;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Employee manager;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
