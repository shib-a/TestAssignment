package com.project.testassignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/udr")
@CrossOrigin
public class UdrController {
    private CdrGeneratorService cdrGeneratorService;
    private CustomerRepository customerRepository;
    @Autowired
    public UdrController(CdrGeneratorService cdrGeneratorService, CustomerRepository customerRepository){
        this.cdrGeneratorService = cdrGeneratorService;
        this.customerRepository = customerRepository;
    }
    @PostMapping("/generate")
    public void generateCdr(){
        cdrGeneratorService.generate();
    }
    @PostMapping("/user")
    public void addUser(){
        var c = new Customer();
        c.setNumber("12345");
        customerRepository.save(c);
    }
}
