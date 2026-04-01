package com.example.demo.Repository;

import com.example.demo.entity.ApprovalStatus;
import com.example.demo.entity.Employee;
import com.example.demo.entity.SalaryChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalaryChangeRequestRepository extends JpaRepository<SalaryChangeRequest, Long> {

    List<SalaryChangeRequest> findByStatus(ApprovalStatus status);

    List<SalaryChangeRequest> findByTargetEmployee(Employee employee);

    // HR views their own submitted requests
    List<SalaryChangeRequest> findByRequestedBy(Employee requestedBy);
}
