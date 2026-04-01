package com.example.demo.controller;

import com.example.demo.Service.PayrollService;
import com.example.demo.dto.PayrollRequestDTO;
import com.example.demo.dto.PayrollResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll")
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    // FINANCE / ADMIN: Generate a payslip for a specific employee + month/year
    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('FINANCE', 'ADMIN')")
    public ResponseEntity<PayrollResponseDTO> generatePayslip(Authentication auth,
                                                              @Valid @RequestBody PayrollRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(payrollService.generatePayslip(auth, request));
    }

    // EMPLOYEE: View their own payslips only
    @GetMapping("/my")
    public ResponseEntity<List<PayrollResponseDTO>> getMyPayslips(Authentication auth) {
        return ResponseEntity.ok(payrollService.getMyPayslips(auth));
    }

    // FINANCE / ADMIN: View all payslips for a specific employee
    @GetMapping("/{employeeId}")
    @PreAuthorize("hasAnyRole('FINANCE', 'ADMIN')")
    public ResponseEntity<List<PayrollResponseDTO>> getEmployeePayslips(@PathVariable Long employeeId) {
        return ResponseEntity.ok(payrollService.getEmployeePayslips(employeeId));
    }

    // FINANCE / ADMIN: Full payroll audit view
    @GetMapping
    @PreAuthorize("hasAnyRole('FINANCE', 'ADMIN')")
    public ResponseEntity<List<PayrollResponseDTO>> getAllPayslips() {
        return ResponseEntity.ok(payrollService.getAllPayslips());
    }
}
