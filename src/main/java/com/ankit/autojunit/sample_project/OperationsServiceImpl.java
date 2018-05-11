package com.ankit.autojunit.sample_project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OperationsServiceImpl implements OperationsService
{
    @Autowired
    DocumentationService documentationService;

    @Override
    public Car getNewCar() {
        Car car = new Car();
        documentationService.registerCar(car);
        documentationService.prepareTaxDocs(car);
        return car;
    }

    @Override
    public void getTheCarServiced(Car car) {
        car.getCarServices().add(new CarServicing(187131L, 2999));
    }
}
