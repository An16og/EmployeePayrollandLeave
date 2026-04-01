package com.example.demo.Service;

import com.example.demo.Repository.EmployeeRepository;
import com.example.demo.dto.EmployeeResponseDTO;
import com.example.demo.dto.UpdateProfileRequest;
import com.example.demo.entity.Employee;
import com.example.demo.entity.Role;
import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class Employeeservice {

    private final EmployeeRepository employeeRepository;

    public Employeeservice(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // --- Helper used by all services ---
    public Employee getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated employee not found in database"));
    }

    // --- Mapping helper ---
    public EmployeeResponseDTO toDTO(Employee emp) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(emp.getId());
        dto.setName(emp.getName());
        dto.setEmail(emp.getEmail());
        dto.setRole(emp.getRole().name());
        dto.setDesignation(emp.getDesignation());
        dto.setSalary(emp.getSalary());
        dto.setPaidLeaveBalance(emp.getPaidLeaveBalance());
        dto.setSickLeaveBalance(emp.getSickLeaveBalance());
        dto.setUnpaidLeaveBalance(emp.getUnpaidLeaveBalance());
        dto.setActive(emp.isActive());
        dto.setCreatedAt(emp.getCreatedAt());
        if (emp.getManager() != null) {
            dto.setManagerName(emp.getManager().getName());
        }
        return dto;
    }

    // --- Endpoints logic ---

    @Transactional(readOnly = true)
    public EmployeeResponseDTO getMe(Authentication auth) {
        Employee emp = getEmployeeByEmail(auth.getName());
        return toDTO(emp);
    }

    @Transactional
    public EmployeeResponseDTO updateMe(Authentication auth, UpdateProfileRequest request) {
        Employee emp = getEmployeeByEmail(auth.getName());
        if (request.getName() != null && !request.getName().isBlank()) {
            emp.setName(request.getName());
        }
        if (request.getDesignation() != null && !request.getDesignation().isBlank()) {
            emp.setDesignation(request.getDesignation());
        }
        return toDTO(employeeRepository.save(emp));
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmployeeResponseDTO getEmployeeById(Long id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
        return toDTO(emp);
    }

    @Transactional
    public EmployeeResponseDTO changeRole(Long id, Role role) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
        emp.setRole(role);
        return toDTO(employeeRepository.save(emp));
    }

    @Transactional
    public void deactivateEmployee(Long id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
        emp.setActive(false);
        employeeRepository.save(emp);
    }

    // MANAGER: View only their direct reports
    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> getTeamEmployees(Authentication auth) {
        Employee manager = getEmployeeByEmail(auth.getName());
        return employeeRepository.findByManagerId(manager.getId())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
