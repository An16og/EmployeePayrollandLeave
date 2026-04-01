package com.example.demo.Service;

import com.example.demo.Repository.EmployeeRepository;
import com.example.demo.Repository.SalaryChangeRequestRepository;
import com.example.demo.dto.SalaryChangeRequestDTO;
import com.example.demo.dto.SalaryChangeResponseDTO;
import com.example.demo.dto.SalaryReviewDTO;
import com.example.demo.entity.ApprovalStatus;
import com.example.demo.entity.Employee;
import com.example.demo.entity.SalaryChangeRequest;
import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SalaryService {

    private final SalaryChangeRequestRepository salaryRequestRepository;
    private final EmployeeRepository employeeRepository;

    public SalaryService(SalaryChangeRequestRepository salaryRequestRepository,
                         EmployeeRepository employeeRepository) {
        this.salaryRequestRepository = salaryRequestRepository;
        this.employeeRepository = employeeRepository;
    }

    private Employee getCurrentEmployee(Authentication auth) {
        return employeeRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated employee not found"));
    }

    private SalaryChangeResponseDTO toDTO(SalaryChangeRequest req) {
        SalaryChangeResponseDTO dto = new SalaryChangeResponseDTO();
        dto.setId(req.getId());
        dto.setTargetEmployeeName(req.getTargetEmployee().getName());
        dto.setTargetEmployeeEmail(req.getTargetEmployee().getEmail());
        dto.setCurrentSalary(req.getCurrentSalary());
        dto.setProposedSalary(req.getProposedSalary());
        dto.setReason(req.getReason());
        dto.setStatus(req.getStatus().name());
        dto.setRequestedByName(req.getRequestedBy().getName());
        dto.setRequestedAt(req.getRequestedAt());
        if (req.getReviewedBy() != null) {
            dto.setReviewedByName(req.getReviewedBy().getName());
        }
        dto.setReviewNote(req.getReviewNote());
        dto.setReviewedAt(req.getReviewedAt());
        return dto;
    }

    // HR: Raise a salary change request for an employee
    @Transactional
    public SalaryChangeResponseDTO raiseSalaryRequest(Authentication auth, SalaryChangeRequestDTO request) {
        Employee hr = getCurrentEmployee(auth);
        Employee targetEmployee = employeeRepository.findById(request.getTargetEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getTargetEmployeeId()));

        SalaryChangeRequest salaryRequest = new SalaryChangeRequest();
        salaryRequest.setRequestedBy(hr);
        salaryRequest.setTargetEmployee(targetEmployee);
        salaryRequest.setCurrentSalary(targetEmployee.getSalary());
        salaryRequest.setProposedSalary(request.getNewSalary());
        salaryRequest.setReason(request.getReason());
        salaryRequest.setStatus(ApprovalStatus.PENDING);

        return toDTO(salaryRequestRepository.save(salaryRequest));
    }

    // FINANCE / ADMIN: View all salary change requests
    @Transactional(readOnly = true)
    public List<SalaryChangeResponseDTO> getAllRequests() {
        return salaryRequestRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // FINANCE / ADMIN: View only pending requests
    @Transactional(readOnly = true)
    public List<SalaryChangeResponseDTO> getPendingRequests() {
        return salaryRequestRepository.findByStatus(ApprovalStatus.PENDING)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // HR: View the salary requests they have submitted
    @Transactional(readOnly = true)
    public List<SalaryChangeResponseDTO> getMyRequests(Authentication auth) {
        Employee hr = getCurrentEmployee(auth);
        return salaryRequestRepository.findByRequestedBy(hr)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // FINANCE / ADMIN: Approve a salary change request
    @Transactional
    public SalaryChangeResponseDTO approveSalaryRequest(Long id, Authentication auth, SalaryReviewDTO review) {
        SalaryChangeRequest salaryRequest = salaryRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary change request", id));

        if (salaryRequest.getStatus() != ApprovalStatus.PENDING) {
            throw new IllegalArgumentException("Only PENDING salary requests can be reviewed.");
        }

        Employee reviewer = getCurrentEmployee(auth);
        Employee targetEmployee = salaryRequest.getTargetEmployee();

        // Apply the salary change
        targetEmployee.setSalary(salaryRequest.getProposedSalary());
        employeeRepository.save(targetEmployee);

        salaryRequest.setStatus(ApprovalStatus.APPROVED);
        salaryRequest.setReviewedBy(reviewer);
        salaryRequest.setReviewNote(review.getReviewNote());
        salaryRequest.setReviewedAt(LocalDateTime.now());

        return toDTO(salaryRequestRepository.save(salaryRequest));
    }

    // FINANCE / ADMIN: Reject a salary change request
    @Transactional
    public SalaryChangeResponseDTO rejectSalaryRequest(Long id, Authentication auth, SalaryReviewDTO review) {
        SalaryChangeRequest salaryRequest = salaryRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary change request", id));

        if (salaryRequest.getStatus() != ApprovalStatus.PENDING) {
            throw new IllegalArgumentException("Only PENDING salary requests can be reviewed.");
        }

        Employee reviewer = getCurrentEmployee(auth);
        salaryRequest.setStatus(ApprovalStatus.REJECTED);
        salaryRequest.setReviewedBy(reviewer);
        salaryRequest.setReviewNote(review.getReviewNote());
        salaryRequest.setReviewedAt(LocalDateTime.now());

        return toDTO(salaryRequestRepository.save(salaryRequest));
    }
}
