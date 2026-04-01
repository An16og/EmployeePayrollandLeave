package com.example.demo.Service;

import com.example.demo.Repository.AttendanceRepository;
import com.example.demo.Repository.EmployeeRepository;
import com.example.demo.dto.AttendanceRequestDTO;
import com.example.demo.dto.AttendanceResponseDTO;
import com.example.demo.entity.Attendance;
import com.example.demo.entity.Employee;
import com.example.demo.entity.Role;
import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendenceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    public AttendenceService(AttendanceRepository attendanceRepository,
                             EmployeeRepository employeeRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
    }

    private Employee getCurrentEmployee(Authentication auth) {
        return employeeRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated employee not found"));
    }

    private AttendanceResponseDTO toDTO(Attendance attendance) {
        AttendanceResponseDTO dto = new AttendanceResponseDTO();
        dto.setId(attendance.getId());
        dto.setEmployeeName(attendance.getEmployee().getName());
        dto.setEmployeeEmail(attendance.getEmployee().getEmail());
        dto.setDate(attendance.getDate());
        dto.setStatus(attendance.getStatus().name());
        dto.setMarkedByName(attendance.getMarkedBy().getName());
        dto.setMarkedOn(attendance.getMarkedOn());
        return dto;
    }

    // MANAGER / ADMIN: Mark attendance for a specific employee
    @Transactional
    public AttendanceResponseDTO markAttendance(Authentication auth, AttendanceRequestDTO request) {
        Employee marker = getCurrentEmployee(auth);
        Employee targetEmployee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId()));

        // MANAGER can only mark attendance for their own team members
        if (marker.getRole() == Role.MANAGER) {
            if (targetEmployee.getManager() == null ||
                    !targetEmployee.getManager().getId().equals(marker.getId())) {
                throw new IllegalArgumentException("You can only mark attendance for your direct reports.");
            }
        }

        // Prevent duplicate attendance for same employee + date
        LocalDate today = LocalDate.now();
        attendanceRepository.findByEmployeeIdAndDate(targetEmployee.getId(), today)
                .ifPresent(a -> {
                    throw new IllegalArgumentException(
                            "Attendance already marked for " + targetEmployee.getName() + " today.");
                });

        Attendance attendance = new Attendance();
        attendance.setEmployee(targetEmployee);
        attendance.setStatus(request.getStatus());
        attendance.setMarkedBy(marker);

        return toDTO(attendanceRepository.save(attendance));
    }

    // MANAGER: See today's presence status for everyone on their team
    @Transactional(readOnly = true)
    public List<AttendanceResponseDTO> getTeamTodayAttendance(Authentication auth) {
        Employee manager = getCurrentEmployee(auth);
        return attendanceRepository.findByManagerIdAndDate(manager.getId(), LocalDate.now())
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // MANAGER: Monthly attendance report for their team
    @Transactional(readOnly = true)
    public List<AttendanceResponseDTO> getTeamMonthlyAttendance(Authentication auth, int month, int year) {
        Employee manager = getCurrentEmployee(auth);
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate from = yearMonth.atDay(1);
        LocalDate to = yearMonth.atEndOfMonth();
        return attendanceRepository.findByManagerIdAndDateBetween(manager.getId(), from, to)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // MANAGER / HR / ADMIN: View attendance history for a specific employee
    @Transactional(readOnly = true)
    public List<AttendanceResponseDTO> getEmployeeAttendance(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee", employeeId);
        }
        return attendanceRepository.findByEmployeeId(employeeId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ADMIN: View all attendance records
    @Transactional(readOnly = true)
    public List<AttendanceResponseDTO> getAllAttendance() {
        return attendanceRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }
}
