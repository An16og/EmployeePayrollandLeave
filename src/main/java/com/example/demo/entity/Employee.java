package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

import com.example.demo.validation.StrongPassword;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Setter(AccessLevel.NONE)
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

    // TODO: Make sure the password stored in database is hashed
    @NotBlank
    @StrongPassword
    @Column(nullable = false)
    private String password;

    @Min(value = 18, message = "Must be at least 18 years old")
    @Max(value = 80)
    @Column(nullable = false)
    private Integer age;

    @Builder.Default
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role = Role.EMPLOYEE;

    private String designation;

    @NotNull
    @Column(nullable = false)
    @PositiveOrZero
    private Double salary;

    @Builder.Default
    @Min(0)
    private int paidLeaveBalance = 14;

    @Builder.Default
    @Min(0)
    private int sickLeaveBalance = 14;

    @Builder.Default
    @Min(0)
    private int unpaidLeaveBalance = 0;

    // false = account deactivated by ADMIN
    @Builder.Default
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;

    @Setter(AccessLevel.NONE)
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Setter(AccessLevel.NONE)
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
