package com.example.demo.Repository;

import com.example.demo.entity.Role;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String Email);
    List<Employee> findByManagerId(Long managerId);
    List<Employee> findByRole(Role role);
    boolean existsByEmail(String email);
}