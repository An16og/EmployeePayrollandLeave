package com.example.demo.Repository;

import com.example.demo.entity.ApprovalStatus;
import com.example.demo.entity.Employee;
import com.example.demo.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LeaveRepository extends JpaRepository<LeaveRequest, Long> {

    // Employee views their own leaves
    List<LeaveRequest> findByEmployee(Employee employee);

    // HR/ADMIN oversight — filter by status
    List<LeaveRequest> findByStatus(ApprovalStatus status);

    // Manager views all leave requests from their direct reports
    @Query("SELECT l FROM LeaveRequest l WHERE l.employee.manager.id = :managerId")
    List<LeaveRequest> findByEmployeeManagerId(@Param("managerId") Long managerId);

    // Employee's leaves filtered by status
    List<LeaveRequest> findByEmployeeAndStatus(Employee employee, ApprovalStatus status);
}
