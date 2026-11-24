package com.example.demo.Repository;

import com.example.demo.entity.Employee;
import com.example.demo.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Employee manager;
    private Employee dev;

    @BeforeEach
    void setUp() {
        manager = Employee.builder()
                .name("Jane Manager")
                .email("jane.manager@example.com")
                .password("Manager@123")
                .age(35)
                .role(Role.MANAGER)
                .designation("Engineering Manager")
                .salary(120000.0)
                .build();
        entityManager.persistAndFlush(manager);

        dev = Employee.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("StrongPass@123")
                .age(28)
                .role(Role.EMPLOYEE)
                .designation("Software Engineer")
                .salary(75000.0)
                .manager(manager)
                .build();
        entityManager.persistAndFlush(dev);
    }

    @Test
    void findByEmail() {
        Optional<Employee> found = employeeRepository.findByEmail("john.doe@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John Doe");
    }

    @Test
    void findByManagerId() {
        List<Employee> reports = employeeRepository.findByManagerId(manager.getId());
        assertThat(reports).hasSize(1);
        assertThat(reports.get(0).getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void findByRole() {
        List<Employee> managers = employeeRepository.findByRole(Role.MANAGER);
        assertThat(managers).hasSize(1);
        assertThat(managers.get(0).getName()).isEqualTo("Jane Manager");

        List<Employee> employees = employeeRepository.findByRole(Role.EMPLOYEE);
        assertThat(employees).hasSize(1);
        assertThat(employees.get(0).getName()).isEqualTo("John Doe");
    }

    @Test
    void existsByEmail() {
        boolean exists = employeeRepository.existsByEmail("john.doe@example.com");
        assertThat(exists).isTrue();

        boolean notExists = employeeRepository.existsByEmail("nonexistent@example.com");
        assertThat(notExists).isFalse();
    }
}
