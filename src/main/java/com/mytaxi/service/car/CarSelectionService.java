package com.mytaxi.service.car;

import com.mytaxi.domainobject.DriverDO;
import com.mytaxi.exception.GenericException;

public interface CarSelectionService {

    DriverDO selectCar(Long driverId, Long carId) throws GenericException;

    DriverDO deselectCar(Long driverId) throws GenericException;
}
