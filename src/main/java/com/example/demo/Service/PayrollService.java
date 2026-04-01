package com.example.demo.Service;

import com.example.demo.Repository.EmployeeRepository;
import com.example.demo.Repository.PayrollRepository;
import com.example.demo.dto.PayrollRequestDTO;
import com.example.demo.dto.PayrollResponseDTO;
import com.example.demo.entity.Employee;
import com.example.demo.entity.Payroll;
import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;

    public PayrollService(PayrollRepository payrollRepository, EmployeeRepository employeeRepository) {
        this.payrollRepository = payrollRepository;
        this.employeeRepository = employeeRepository;
    }

    private Employee getCurrentEmployee(Authentication auth) {
        return employeeRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated employee not found"));
    }

    private PayrollResponseDTO toDTO(Payroll payroll) {
        PayrollResponseDTO dto = new PayrollResponseDTO();
        dto.setId(payroll.getId());
        dto.setEmployeeName(payroll.getEmployee().getName());
        dto.setEmployeeEmail(payroll.getEmployee().getEmail());
        dto.setMonth(payroll.getMonth());
        dto.setYear(payroll.getYear());
        dto.setBaseSalary(payroll.getBaseSalary());
        dto.setBonus(payroll.getBonus());
        dto.setDeductions(payroll.getDeductions());
        dto.setTax(payroll.getTax());
        dto.setFinalSalary(payroll.getFinalSalary());
        dto.setGeneratedAt(payroll.getGeneratedAt());
        if (payroll.getGeneratedBy() != null) {
            dto.setGeneratedByName(payroll.getGeneratedBy().getName());
        }
        return dto;
    }

    // FINANCE / ADMIN: Generate payslip for an employee for a given month/year
    @Transactional
    public PayrollResponseDTO generatePayslip(Authentication auth, PayrollRequestDTO request) {
        Employee finance = getCurrentEmployee(auth);
        Employee targetEmployee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId()));

        // Prevent generating duplicate payslip for the same month/year
        if (payrollRepository.existsByEmployeeAndMonthAndYear(
                targetEmployee, request.getMonth(), request.getYear())) {
            throw new IllegalArgumentException(
                    "Payslip for " + targetEmployee.getName() +
                    " for " + request.getMonth() + "/" + request.getYear() + " already exists.");
        }

        double baseSalary = targetEmployee.getSalary();
        double bonus = request.getBonus() != null ? request.getBonus() : 0.0;
        double deductions = request.getDeductions() != null ? request.getDeductions() : 0.0;

        // Simple flat 10% tax on base salary
        double tax = baseSalary * 0.10;
        double finalSalary = baseSalary + bonus - deductions - tax;

        Payroll payroll = new Payroll();
        payroll.setEmployee(targetEmployee);
        payroll.setMonth(request.getMonth());
        payroll.setYear(request.getYear());
        payroll.setBaseSalary(baseSalary);
        payroll.setBonus(bonus);
        payroll.setDeductions(deductions);
        payroll.setTax(tax);
        payroll.setFinalSalary(finalSalary);
        payroll.setGeneratedBy(finance);

        return toDTO(payrollRepository.save(payroll));
    }

    // EMPLOYEE: View their own payslips
    @Transactional(readOnly = true)
    public List<PayrollResponseDTO> getMyPayslips(Authentication auth) {
        Employee employee = getCurrentEmployee(auth);
        return payrollRepository.findByEmployee(employee)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // FINANCE / ADMIN: View a specific employee's payslips
    @Transactional(readOnly = true)
    public List<PayrollResponseDTO> getEmployeePayslips(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", employeeId));
        return payrollRepository.findByEmployee(employee)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // FINANCE / ADMIN: Full payroll audit — all payslips, newest first
    @Transactional(readOnly = true)
    public List<PayrollResponseDTO> getAllPayslips() {
        return payrollRepository.findAllByOrderByGeneratedAtDesc()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }
}
