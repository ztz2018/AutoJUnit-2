package com.ankit.autojunit.sample_project;

import com.ankit.autojunit.processor.model.ParsedUnit;

public interface DocumentationService {

    ParsedUnit registerCar(Car car);
    void prepareTaxDocs(Car car);
    boolean requestCustomerCare(Car car);

}
