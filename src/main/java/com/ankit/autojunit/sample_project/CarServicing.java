package com.ankit.autojunit.sample_project;

import lombok.Data;
import lombok.Getter;

import java.util.Date;

@Getter
public class CarServicing {

    private static Long currentNum = 1L;

    private Date serviceDate;
    private double cost;
    private Long carServicingId;
    private Long customerId;

    public CarServicing(Long customerId, double cost) {
        this.customerId = customerId;
        this.serviceDate = new Date();
        this.carServicingId = currentNum++;
        this.cost = cost;
    }

}
