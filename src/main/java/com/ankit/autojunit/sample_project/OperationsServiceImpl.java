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
            } else if (ab >20) {
                ab = 100;
            } else if (ab > 30) {
                String yoyo = "agar tum saath ho";
            } else {
                String abs = "you might have understood by now...";
            }

        }
        return car;
    }

    @Override
    public void getTheCarServiced(Car car) {
        car.getCarServices().add(new CarServicing(187131L, 2999));
    }

    private Car privateMethodCallingExternalService(Car car)
    {
        documentationService.registerCar(car);
        documentationService.requestCustomerCare(car);
        return car;
    }

}
