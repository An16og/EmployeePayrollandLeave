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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id",nullable = false)
    private Employee employee;
    /* TODO(v2): Review database indexing strategy.
        Current implementation relies on the composite unique index (employee_id, date).
        Future improvements:
        - Evaluate adding a dedicated index on `date` if reporting queries
        (findByDate, monthly attendance reports) become a bottleneck.
        - Consider an index on `status` only if status-based filtering is frequent.
        - Use EXPLAIN/EXPLAIN ANALYZE to verify that indexes are actually used
        before adding new ones.
        Note: Do not add indexes preemptively. Optimize based on real query patterns.*/

    @Column(nullable = false,updatable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalDateTime markedOn;
    /* TODO(v2): Revisit @PrePersist date initialization.
        Currently, Attendance.date and markedOn are always set to the current date/time.
        This enforces the business rule that attendance can only be marked for "today".
        If future requirements allow backdated attendance or data imports,
        preserve manually assigned values instead of overwriting them.*/
      @PrePersist
    public void prePersist() {
        this.markedOn = LocalDateTime.now();
        this.date = LocalDate.now();   
    }



    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendenceTypes status = AttendenceTypes.ABSENT;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marked_by",nullable = false)
    private Employee markedBy;


}
