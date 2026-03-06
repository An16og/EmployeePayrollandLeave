package com.example.demo.Service;


import com.example.demo.entity.Employee;
import com.example.demo.Repository.EmployeeRepository;
import jakarta.persistence.Id;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class Employeeservice {
    private final EmployeeRepository repo;
    public Employeeservice(EmployeeRepository repo){
        this.repo=repo;
    }
   public Employee createEmployee(Employee employee){
        if(repo.existsByEmail(employee.getEmail())){
            throw new RuntimeException("Email already exists.");
        }
        //Password encryption logic here

       employee.setCreatedAt(LocalDateTime.now());
        return repo.save(employee);

   }
   public Optional<Employee> getEmployeeById(Long id){
       return repo.findById(id);

   }
   public List<Employee> getAllEmployees(){
        return repo.findAll();
   }
   public boolean updateEmployee(Long id, Employee employee){
        Optional<Employee> e= repo.findById(id);
        if(e.isPresent()){
            Employee emp= e.get();
            emp.setName(employee.getName());
            emp.setPassword(employee.getPassword());//Might change this later after studying jwt
            emp.setAge(employee.getAge());
            emp.setDesignation(employee.getDesignation());
            emp.setRole(employee.getRole());
            emp.setManagerId(employee.getManagerId());
            emp.setSalary(employee.getSalary());
            emp.setEmail(employee.getEmail());
            repo.save(emp);
            return true;
        }
        else return false;
   }
   // deleteEmployee


}
