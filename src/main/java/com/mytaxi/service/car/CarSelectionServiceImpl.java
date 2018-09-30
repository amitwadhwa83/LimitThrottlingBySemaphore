package com.mytaxi.service.car;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.mytaxi.dataaccessobject.CarRepository;
import com.mytaxi.dataaccessobject.DriverRepository;
import com.mytaxi.domainobject.CarDO;
import com.mytaxi.domainobject.DriverDO;
import com.mytaxi.domainvalue.OnlineStatus;
import com.mytaxi.exception.CarAlreadyInUseException;
import com.mytaxi.exception.EntityNotFoundException;
import com.mytaxi.exception.GenericException;
import com.mytaxi.exception.IncorrectStatusException;

@Service
public class CarSelectionServiceImpl implements CarSelectionService {

    private final CarRepository carRepository;
    private final DriverRepository driverRepository;

    public CarSelectionServiceImpl(final CarRepository carRepository, final DriverRepository driverRepository) {
	this.carRepository = carRepository;
	this.driverRepository = driverRepository;
    }

    @Override
    @Transactional
    public DriverDO selectCar(Long driverId, Long carId) throws GenericException {

	DriverDO driver = findDriverChecked(driverId);

	// Driver offline check
	if (driver.getOnlineStatus() == OnlineStatus.OFFLINE) {
	    throw new IncorrectStatusException(driverId.toString());
	}

	// Car already assigned check
	CarDO car = findCarChecked(carId);
	if (null != car.getSelected() && car.getSelected()) {
	    throw new CarAlreadyInUseException(carId.toString());
	}

	// De-select old entry
	Optional.ofNullable(driver.getCar()).ifPresent(currentCar -> {
	    currentCar.setSelected(false);
	    carRepository.save(currentCar);
	});

	// Select the car
	car.setSelected(true);
	carRepository.save(car);

	driver.setCar(car);
	driverRepository.save(driver);

	return driver;
    }

    @Override
    public DriverDO deselectCar(Long driverId) throws GenericException {
	DriverDO driver = findDriverChecked(driverId);

	Optional.ofNullable(driver.getCar()).ifPresent(c -> {
	    c.setSelected(false);
	    carRepository.save(c);
	});

	driver.setCar(null);
	driverRepository.save(driver);

	return driver;
    }

    private DriverDO findDriverChecked(Long driverId) throws EntityNotFoundException {
	return driverRepository.findById(driverId).orElseThrow(() -> new EntityNotFoundException(driverId.toString()));
    }

    private CarDO findCarChecked(Long carId) throws EntityNotFoundException {
	return carRepository.findById(carId).orElseThrow(() -> new EntityNotFoundException(carId.toString()));
    }
}
