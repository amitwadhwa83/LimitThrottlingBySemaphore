package com.mytaxi.service.car;

import java.util.List;

import com.mytaxi.domainobject.CarDO;
import com.mytaxi.exception.GenericException;

public interface CarService {

    CarDO createCar(CarDO carDO) throws GenericException;

    CarDO findCar(Long carId) throws GenericException;

    CarDO updateCar(Long carId, Integer rating) throws GenericException;

    CarDO deleteCar(Long carId) throws GenericException;

    List<CarDO> findCars();
}
