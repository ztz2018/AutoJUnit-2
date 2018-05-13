package com.ankit.autojunit.sample_project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/autojunit/sample")
public class MainController {

    @Autowired
    OperationsService operationsService;

    @GetMapping("/test")
    public ResponseEntity<Object> getSomeResponse() {
        return ResponseEntity.ok(operationsService.getNewCar());
    }


}
