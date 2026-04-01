package com.example.demo.Service;

import com.example.demo.Repository.EmployeeRepository;
import com.example.demo.Repository.LeaveRepository;
import com.example.demo.dto.LeaveActionRequest;
import com.example.demo.dto.LeaveRequestDTO;
import com.example.demo.dto.LeaveResponseDTO;
import com.example.demo.entity.*;
import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveApplicationService {

    private final LeaveRepository leaveRepository;
    private final EmployeeRepository employeeRepository;

    public LeaveApplicationService(LeaveRepository leaveRepository,
                                   EmployeeRepository employeeRepository) {
        this.leaveRepository = leaveRepository;
        this.employeeRepository = employeeRepository;
    }

    private Employee getCurrentEmployee(Authentication auth) {
        return employeeRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated employee not found"));
    }

    private LeaveResponseDTO toDTO(LeaveRequest leave) {
        LeaveResponseDTO dto = new LeaveResponseDTO();
        dto.setLeaveId(leave.getLeaveId());
        dto.setEmployeeName(leave.getEmployee().getName());
        dto.setEmployeeEmail(leave.getEmployee().getEmail());
        dto.setLeaveType(leave.getLeaveType().name());
        dto.setStartDate(leave.getStartDate());
        dto.setEndDate(leave.getEndDate());
        dto.setReason(leave.getReason());
        dto.setStatus(leave.getStatus().name());
        dto.setNumberOfDays(leave.getNumberOfDays());
        dto.setAppliedAt(leave.getAppliedAt());
        if (leave.getApprovedBy() != null) {
            dto.setApprovedByName(leave.getApprovedBy().getName());
        }
        if (leave.getEmployee().getManager() != null) {
            dto.setManagerName(leave.getEmployee().getManager().getName());
        }
        return dto;
    }

    // EMPLOYEE: apply for leave
    @Transactional
    public LeaveResponseDTO applyLeave(Authentication auth, LeaveRequestDTO request) {
        Employee employee = getCurrentEmployee(auth);

        // Business rule: start date must be before end date
        if (!request.getStartDate().isBefore(request.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date.");
        }

        // Business rule: check leave balance before allowing application
        int days = (int) ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
        validateLeaveBalance(employee, request.getLeaveType(), days);

        LeaveRequest leave = new LeaveRequest();
        leave.setEmployee(employee);
        leave.setLeaveType(request.getLeaveType());
        leave.setStartDate(request.getStartDate());
        leave.setEndDate(request.getEndDate());
        leave.setReason(request.getReason());
        leave.setStatus(ApprovalStatus.PENDING);

        return toDTO(leaveRepository.save(leave));
    }

    private void validateLeaveBalance(Employee employee, LeaveType leaveType, int days) {
        switch (leaveType) {
            case PAID -> {
                if (employee.getPaidLeaveBalance() < days) {
                    throw new IllegalArgumentException(
                            "Insufficient paid leave balance. Available: " + employee.getPaidLeaveBalance() + " days.");
                }
            }
            case SICK -> {
                if (employee.getSickLeaveBalance() < days) {
                    throw new IllegalArgumentException(
                            "Insufficient sick leave balance. Available: " + employee.getSickLeaveBalance() + " days.");
                }
            }
            case UNPAID -> {
                // Unpaid leave is always allowed
            }
        }
    }

    // EMPLOYEE: view their own leave history
    @Transactional(readOnly = true)
    public List<LeaveResponseDTO> getMyLeaves(Authentication auth) {
        Employee employee = getCurrentEmployee(auth);
        return leaveRepository.findByEmployee(employee)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // MANAGER: view leave requests for their team only
    @Transactional(readOnly = true)
    public List<LeaveResponseDTO> getTeamLeaves(Authentication auth) {
        Employee manager = getCurrentEmployee(auth);
        return leaveRepository.findByEmployeeManagerId(manager.getId())
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // HR / ADMIN: view all leave requests
    @Transactional(readOnly = true)
    public List<LeaveResponseDTO> getAllLeaves() {
        return leaveRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public LeaveResponseDTO getLeaveById(Long id) {
        return toDTO(leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request", id)));
    }

    // MANAGER / ADMIN: approve a leave request
    @Transactional
    public LeaveResponseDTO approveLeave(Long id, Authentication auth, LeaveActionRequest action) {
        LeaveRequest leave = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request", id));

        if (leave.getStatus() != ApprovalStatus.PENDING) {
            throw new IllegalArgumentException("Only PENDING leave requests can be approved.");
        }

        Employee approver = getCurrentEmployee(auth);

        // MANAGER can only approve their own team's leaves
        if (approver.getRole() == Role.MANAGER) {
            Employee leaveEmployee = leave.getEmployee();
            if (leaveEmployee.getManager() == null ||
                    !leaveEmployee.getManager().getId().equals(approver.getId())) {
                throw new IllegalArgumentException("You can only approve leave for your direct reports.");
            }
        }

        // Deduct leave balance only on approval
        Employee leaveEmployee = leave.getEmployee();
        int days = leave.getNumberOfDays();
        deductLeaveBalance(leaveEmployee, leave.getLeaveType(), days);
        employeeRepository.save(leaveEmployee);

        leave.setStatus(ApprovalStatus.APPROVED);
        leave.setApprovedBy(approver);

        return toDTO(leaveRepository.save(leave));
    }

    private void deductLeaveBalance(Employee employee, LeaveType leaveType, int days) {
        switch (leaveType) {
            case PAID -> employee.setPaidLeaveBalance(employee.getPaidLeaveBalance() - days);
            case SICK -> employee.setSickLeaveBalance(employee.getSickLeaveBalance() - days);
            case UNPAID -> employee.setUnpaidLeaveBalance(employee.getUnpaidLeaveBalance() + days);
        }
    }

    // MANAGER / ADMIN: reject a leave request
    @Transactional
    public LeaveResponseDTO rejectLeave(Long id, Authentication auth, LeaveActionRequest action) {
        LeaveRequest leave = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request", id));

        if (leave.getStatus() != ApprovalStatus.PENDING) {
            throw new IllegalArgumentException("Only PENDING leave requests can be rejected.");
        }

        Employee approver = getCurrentEmployee(auth);

        if (approver.getRole() == Role.MANAGER) {
            Employee leaveEmployee = leave.getEmployee();
            if (leaveEmployee.getManager() == null ||
                    !leaveEmployee.getManager().getId().equals(approver.getId())) {
                throw new IllegalArgumentException("You can only reject leave for your direct reports.");
            }
        }

        leave.setStatus(ApprovalStatus.REJECTED);
        leave.setApprovedBy(approver);

        return toDTO(leaveRepository.save(leave));
    }
}
