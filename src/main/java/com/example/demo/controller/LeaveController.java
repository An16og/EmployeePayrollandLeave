package com.example.demo.controller;

import com.example.demo.Service.LeaveApplicationService;
import com.example.demo.dto.LeaveActionRequest;
import com.example.demo.dto.LeaveRequestDTO;
import com.example.demo.dto.LeaveResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    private final LeaveApplicationService leaveService;

    public LeaveController(LeaveApplicationService leaveService) {
        this.leaveService = leaveService;
    }

    // EMPLOYEE: Apply for leave (identity from JWT — never from body)
    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<LeaveResponseDTO> applyLeave(Authentication auth,
                                                       @Valid @RequestBody LeaveRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leaveService.applyLeave(auth, request));
    }

    // EMPLOYEE: View own leave history
    @GetMapping("/my")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<LeaveResponseDTO>> getMyLeaves(Authentication auth) {
        return ResponseEntity.ok(leaveService.getMyLeaves(auth));
    }

    // MANAGER: View only their team's leave requests
    @GetMapping("/team")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<LeaveResponseDTO>> getTeamLeaves(Authentication auth) {
        return ResponseEntity.ok(leaveService.getTeamLeaves(auth));
    }

    // HR / ADMIN: Full oversight of all leave requests
    @GetMapping
    @PreAuthorize("hasAnyRole('HR', 'ADMIN')")
    public ResponseEntity<List<LeaveResponseDTO>> getAllLeaves() {
        return ResponseEntity.ok(leaveService.getAllLeaves());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'ADMIN', 'MANAGER')")
    public ResponseEntity<LeaveResponseDTO> getLeaveById(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.getLeaveById(id));
    }

    // MANAGER / ADMIN: Approve leave (service validates team ownership for MANAGER)
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<LeaveResponseDTO> approveLeave(@PathVariable Long id,
                                                         Authentication auth,
                                                         @RequestBody(required = false) LeaveActionRequest action) {
        return ResponseEntity.ok(leaveService.approveLeave(id, auth, action != null ? action : new LeaveActionRequest()));
    }

    // MANAGER / ADMIN: Reject leave
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<LeaveResponseDTO> rejectLeave(@PathVariable Long id,
                                                        Authentication auth,
                                                        @RequestBody(required = false) LeaveActionRequest action) {
        return ResponseEntity.ok(leaveService.rejectLeave(id, auth, action != null ? action : new LeaveActionRequest()));
    }
}
