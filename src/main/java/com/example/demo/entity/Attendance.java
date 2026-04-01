package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(    uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "date"})
        ,name = "attendance")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id",nullable = false)
    private Employee employee;


    @Column(nullable = false,updatable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalDateTime markedOn;
      @PrePersist
    public void prePersist() {
        this.markedOn = LocalDateTime.now();
        this.date = LocalDate.now();   
    }



    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendenceTypes status = AttendenceTypes.ABSENT;
    @ManyToOne
    @JoinColumn(name = "marked_by",nullable = false)
    private Employee markedBy;


}
