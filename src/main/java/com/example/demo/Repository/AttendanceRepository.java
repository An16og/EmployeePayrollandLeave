package com.example.demo.Repository;

import com.example.demo.entity.Attendance;
import com.example.demo.entity.AttendenceEnums;
import com.example.demo.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance,Long> {
    List<Attendance> findByEmployeeId(Long Id);
    List<Attendance> findByDate(LocalDate date);
    List<Attendance> findByMarkedOn(LocalDateTime dateTime);
    List<Attendance> findByStatus(AttendenceEnums status);
    List<Attendance> findByMarkedBy(Employee marked);
    List<Attendance> findByMarkedOnBetween(LocalDateTime start, LocalDateTime end);
    List<Attendance>findByEmployeeIdAndDate(Long employeeId, LocalDate date);


}
