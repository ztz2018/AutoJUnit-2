package com.ankit.autojunit.sample_project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/mongo")
public class CustomerController {

    @Autowired
    CustomerRepository customerRepository;

    @GetMapping("/getAllCustomers")
    public ResponseEntity getAllCustomers()
    {
        try {
            return ResponseEntity.ok(customerRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/createCustomer")
    public ResponseEntity createCustomer(@Valid @RequestBody Customer customer) {
        try {
            return ResponseEntity.ok(customerRepository.save(customer));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
