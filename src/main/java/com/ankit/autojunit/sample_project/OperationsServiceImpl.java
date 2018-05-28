package com.ankit.autojunit.sample_project;

import com.ankit.autojunit.processor.parser.MainParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OperationsServiceImpl implements OperationsService
{
    @Autowired
    DocumentationService documentationService;

    @Autowired
    CustomerRepository customerRepository;


    @Autowired
    MainParser testKeLiyeLiyaHaiBas;


    Car globalCar;
    static Car staticCar;

    @Override
    public Car getNewCar() {
        Car car = new Car();
        int ab = 10;
        for(int i=0; i<5; i++) {
            if (ab > 0) {
                privateMethodCallingExternalService(car);
                documentationService.prepareTaxDocs(car);
            }
        }
        return car;
    }

    @Override
    public void getTheCarServiced(Car car) {
        car.getCarServices().add(new CarServicing(187131L, 2999));
    }

    private void privateMethodCallingExternalService(Car car)
    {
        documentationService.registerCar(car);
    }

}
