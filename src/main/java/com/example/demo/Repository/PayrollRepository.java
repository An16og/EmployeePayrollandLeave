package com.example.demo.Repository;

import com.example.demo.entity.Employee;
import com.example.demo.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    List<Payroll> findByEmployee(Employee employee);

    Optional<Payroll> findByEmployeeAndMonthAndYear(Employee employee, int month, int year);

    boolean existsByEmployeeAndMonthAndYear(Employee employee, int month, int year);

    // Audit view — most recent first
    List<Payroll> findAllByOrderByGeneratedAtDesc();
}
