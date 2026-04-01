package com.example.demo.controller;

import com.example.demo.Service.AttendenceService;
import com.example.demo.dto.AttendanceRequestDTO;
import com.example.demo.dto.AttendanceResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendenceController {

    private final AttendenceService attendenceService;

    public AttendenceController(AttendenceService attendenceService) {
        this.attendenceService = attendenceService;
    }

    // MANAGER / ADMIN: Mark attendance for a team member
    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<AttendanceResponseDTO> markAttendance(Authentication auth,
                                                                @Valid @RequestBody AttendanceRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendenceService.markAttendance(auth, request));
    }

    // MANAGER: Today's presence summary for their team
    @GetMapping("/team/today")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<AttendanceResponseDTO>> getTeamTodayAttendance(Authentication auth) {
        return ResponseEntity.ok(attendenceService.getTeamTodayAttendance(auth));
    }

    // MANAGER: Monthly attendance report for their team
    @GetMapping("/team/monthly")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<List<AttendanceResponseDTO>> getTeamMonthlyAttendance(Authentication auth,
                                                                                 @RequestParam int month,
                                                                                 @RequestParam int year) {
        return ResponseEntity.ok(attendenceService.getTeamMonthlyAttendance(auth, month, year));
    }

    // MANAGER / HR / ADMIN: Specific employee's attendance history
    @GetMapping("/{employeeId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR', 'ADMIN')")
    public ResponseEntity<List<AttendanceResponseDTO>> getEmployeeAttendance(@PathVariable Long employeeId) {
        return ResponseEntity.ok(attendenceService.getEmployeeAttendance(employeeId));
    }

    // ADMIN: All attendance records
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AttendanceResponseDTO>> getAllAttendance() {
        return ResponseEntity.ok(attendenceService.getAllAttendance());
    }
}
