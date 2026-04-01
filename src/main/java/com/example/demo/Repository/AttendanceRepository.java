package com.example.demo.Repository;

import com.example.demo.entity.Attendance;
import com.example.demo.entity.AttendenceTypes;
import com.example.demo.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByEmployeeId(Long id);

    List<Attendance> findByDate(LocalDate date);

    List<Attendance> findByStatus(AttendenceTypes status);

    List<Attendance> findByMarkedBy(Employee markedBy);

    // Check if attendance already marked for this employee today (prevent duplicates)
    Optional<Attendance> findByEmployeeIdAndDate(Long employeeId, LocalDate date);

    // Manager: get all attendance records for their team
    @Query("SELECT a FROM Attendance a WHERE a.employee.manager.id = :managerId")
    List<Attendance> findByManagerId(@Param("managerId") Long managerId);

    // Manager: get team attendance for a specific date
    @Query("SELECT a FROM Attendance a WHERE a.employee.manager.id = :managerId AND a.date = :date")
    List<Attendance> findByManagerIdAndDate(@Param("managerId") Long managerId,
                                            @Param("date") LocalDate date);

    // Manager: get team attendance for a date range (monthly reports)
    @Query("SELECT a FROM Attendance a WHERE a.employee.manager.id = :managerId " +
           "AND a.date BETWEEN :from AND :to ORDER BY a.date ASC")
    List<Attendance> findByManagerIdAndDateBetween(@Param("managerId") Long managerId,
                                                   @Param("from") LocalDate from,
                                                   @Param("to") LocalDate to);
}
