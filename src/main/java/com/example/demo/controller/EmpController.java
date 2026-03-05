package com.example.demo.controller;


import com.example.demo.Service.Empservice;
import com.example.demo.entity.Employee;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/do")

public class EmpController {
    private final Empservice service;

    public EmpController(Empservice service){
        this.service =service;
    }
@GetMapping
public List<Employee> getAllEmp(){ return service.allEmployee();}

@GetMapping("/{id}")
public Optional<Employee> getEmpByID(@PathVariable String id){return service.getEmpByID(id);}

@PostMapping
public Employee newEmp(@RequestBody Employee e){return service.createEmployee(e);}

@PutMapping

public Optional<Employee> updateEmp(@RequestParam String id,@RequestBody Employee e){
        return service.updateEmployee(id,e);
}

@DeleteMapping("/{id}")
public Employee delete(@PathVariable String id){
       return service.deleteEmployee(id);
}

}
