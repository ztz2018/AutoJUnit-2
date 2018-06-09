package com.ankit.autojunit.sample_project;

import com.ankit.autojunit.processor.model.ParsedUnit;
import org.springframework.stereotype.Component;

@Component
public class DocumentationServiceImpl implements DocumentationService {

    @Override
    public ParsedUnit registerCar(Car car) {
        return null;
    }

    @Override
    public void prepareTaxDocs(Car car) {

    }

    @Override
    public boolean requestCustomerCare(Car car) {
        return true;
    }
}
