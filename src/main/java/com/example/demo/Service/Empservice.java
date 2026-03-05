package com.example.demo.Service;


import com.example.demo.entity.Employee;
import com.example.demo.Repository.EmployeeRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class Empservice {
    private final EmployeeRepository repo;
    public static final PasswordEncoder encoder = new BCryptPasswordEncoder();
    public Empservice(EmployeeRepository repo){
        this.repo=repo;
    }
public Employee createEmployee(Employee e){
        return repo.save(e);

}
public @NonNull List<Employee> allEmployee(){
        return repo.findAll();
}
public Optional<Employee> getEmpByID(String id){
    return repo.findById(id);
    }
public Optional<Employee> updateEmployee(String id, Employee updated){
        Optional<Employee> e= repo.findById(id);
        if(e.isPresent()){
            Employee em = e.get();
            em.setId(updated.getId());
            em.setDesignation(updated.getDesignation());
            em.setName(updated.getName());
            em.setName(updated.getName());
            em.setPassword(updated.getPassword());
            em.setLeaves(updated.getLeaves());
            return Optional.of(repo.save(em));
        }
        return Optional.empty();
}
public Employee deleteEmployee(String id){
        Optional<Employee>e= repo.findById(id);
        if(e.isPresent()){
            repo.deleteById(id);
            return e.get();
        }
        return null;
}

}
