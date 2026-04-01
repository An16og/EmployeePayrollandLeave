package com.example.demo.controller;

import com.example.demo.Service.SalaryService;
import com.example.demo.dto.SalaryChangeRequestDTO;
import com.example.demo.dto.SalaryChangeResponseDTO;
import com.example.demo.dto.SalaryReviewDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salary")
public class SalaryController {

    private final SalaryService salaryService;

    public SalaryController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }

    // HR: Raise a salary change request for an employee → goes to Finance for approval
    @PostMapping("/requests")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<SalaryChangeResponseDTO> raiseSalaryRequest(Authentication auth,
                                                                       @Valid @RequestBody SalaryChangeRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(salaryService.raiseSalaryRequest(auth, request));
    }

    // HR: View your own submitted salary requests
    @GetMapping("/requests/my")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<List<SalaryChangeResponseDTO>> getMyRequests(Authentication auth) {
        return ResponseEntity.ok(salaryService.getMyRequests(auth));
    }

    // FINANCE / ADMIN: View all salary change requests (full audit trail)
    @GetMapping("/requests")
    @PreAuthorize("hasAnyRole('FINANCE', 'ADMIN')")
    public ResponseEntity<List<SalaryChangeResponseDTO>> getAllRequests() {
        return ResponseEntity.ok(salaryService.getAllRequests());
    }

    // FINANCE / ADMIN: View only pending requests needing review
    @GetMapping("/requests/pending")
    @PreAuthorize("hasAnyRole('FINANCE', 'ADMIN')")
    public ResponseEntity<List<SalaryChangeResponseDTO>> getPendingRequests() {
        return ResponseEntity.ok(salaryService.getPendingRequests());
    }

    // FINANCE / ADMIN: Approve → salary is updated immediately
    @PutMapping("/requests/{id}/approve")
    @PreAuthorize("hasAnyRole('FINANCE', 'ADMIN')")
    public ResponseEntity<SalaryChangeResponseDTO> approveRequest(@PathVariable Long id,
                                                                   Authentication auth,
                                                                   @RequestBody(required = false) SalaryReviewDTO review) {
        return ResponseEntity.ok(salaryService.approveSalaryRequest(id, auth, review != null ? review : new SalaryReviewDTO()));
    }

    // FINANCE / ADMIN: Reject with a note explaining why
    @PutMapping("/requests/{id}/reject")
    @PreAuthorize("hasAnyRole('FINANCE', 'ADMIN')")
    public ResponseEntity<SalaryChangeResponseDTO> rejectRequest(@PathVariable Long id,
                                                                  Authentication auth,
                                                                  @RequestBody(required = false) SalaryReviewDTO review) {
        return ResponseEntity.ok(salaryService.rejectSalaryRequest(id, auth, review != null ? review : new SalaryReviewDTO()));
    }
}
