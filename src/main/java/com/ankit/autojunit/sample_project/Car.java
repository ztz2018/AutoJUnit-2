package com.ankit.autojunit.sample_project;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Car {

    private static Long currentNum = 1L;

    private Long id;
    private String brand;
    private String model;
    private int mileage;
    private List<CarServicing> carServices;

    public Car() {
        this.id = currentNum++;
        carServices = new ArrayList<>();
    }
}
