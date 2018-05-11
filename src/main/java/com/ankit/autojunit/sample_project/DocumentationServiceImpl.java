package com.ankit.autojunit.sample_project;

import org.springframework.stereotype.Component;

@Component
public class DocumentationServiceImpl implements DocumentationService {

    @Override
    public void registerCar(Car car) {

    }

    @Override
    public void prepareTaxDocs(Car car) {

    }

    @Override
    public boolean requestCustomerCare(Car car) {
        return true;
    }
}
