package com.example.demo.controller;

import com.example.demo.Service.Employeeservice;
import com.example.demo.dto.EmployeeResponseDTO;
import com.example.demo.dto.UpdateProfileRequest;
import com.example.demo.entity.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmpController {

    private final Employeeservice employeeService;

    public EmpController(Employeeservice employeeService) {
        this.employeeService = employeeService;
    }

    // All authenticated users can view their own profile
    @GetMapping("/me")
    public ResponseEntity<EmployeeResponseDTO> getMyProfile(Authentication auth) {
        return ResponseEntity.ok(employeeService.getMe(auth));
    }

    // All authenticated users can update their own profile (name, designation only)
    @PutMapping("/me")
    public ResponseEntity<EmployeeResponseDTO> updateMyProfile(Authentication auth,
                                                               @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(employeeService.updateMe(auth, request));
    }

    // HR and ADMIN can view all employees
    @GetMapping
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<List<EmployeeResponseDTO>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    // MANAGER: View only direct reports (their team)
    @GetMapping("/team")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<EmployeeResponseDTO>> getTeamEmployees(Authentication auth) {
        return ResponseEntity.ok(employeeService.getTeamEmployees(auth));
    }

    // HR, ADMIN, and MANAGER can look up specific employee
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN', 'MANAGER')")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    // Only ADMIN can change roles
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponseDTO> changeRole(@PathVariable Long id,
                                                          @RequestParam Role role) {
        return ResponseEntity.ok(employeeService.changeRole(id, role));
    }

    // Only ADMIN can deactivate accounts
    @DeleteMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deactivateEmployee(@PathVariable Long id) {
        employeeService.deactivateEmployee(id);
        return ResponseEntity.ok("Employee account deactivated successfully.");
    }
}
