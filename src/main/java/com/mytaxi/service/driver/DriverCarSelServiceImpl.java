package com.mytaxi.service.driver;

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
import com.mytaxi.exception.IncorrectStatusException;

/**
 * Service used to drive the car assignment/removal to/from driver(s)
 */

@Service
public class DriverCarSelServiceImpl implements DriverCarSelService {

    private final CarRepository carRepository;
    private final DriverRepository driverRepository;

    public DriverCarSelServiceImpl(final CarRepository carRepository, final DriverRepository driverRepository) {
	this.carRepository = carRepository;
	this.driverRepository = driverRepository;
    }

    /**
     * Car assignment/selection for driver
     *
     * @param Long
     *            driverId,Long carId
     * 
     * @return DriverDO driver details updated with assigned car
     * @throws EntityNotFoundException
     *             if no car/driver with the given input is found,
     *             IncorrectStatusException if driver state is not correct for car
     *             assignment, CarAlreadyInUseException if car is already in use or
     *             already assigned to driver
     */
    @Override
    @Transactional
    public DriverDO assignDriverCar(Long driverId, Long carId)
	    throws EntityNotFoundException, IncorrectStatusException, CarAlreadyInUseException {

	DriverDO driver = findDriverChecked(driverId);

	// Driver offline check
	if (driver.getOnlineStatus() == OnlineStatus.OFFLINE) {
	    throw new IncorrectStatusException(driverId.toString());
	}

	// Car already assigned check
	CarDO car = carRepository.findCarChecked(carId);
	if (null != car.getSelected() && car.getSelected()) {
	    throw new CarAlreadyInUseException(carId.toString());
	}

	// De-select old car if any
	Optional.ofNullable(driver.getCar()).ifPresent(currentCar -> {
	    currentCar.setSelected(false);
	    carRepository.save(currentCar);
	});

	// Select the car
	car.setSelected(true);
	carRepository.save(car);

	// Assign
	driver.setCar(car);
	driverRepository.save(driver);

	return driver;
    }

    /**
     * Car removal/de-selection for driver
     *
     * @param Long
     *            driverId
     * 
     * @return DriverDO driver details update with car removal
     * @throws EntityNotFoundException
     *             if no car/driver with the given input is found
     */
    @Override
    public DriverDO removeDriverCar(Long driverId) throws EntityNotFoundException {
	DriverDO driver = findDriverChecked(driverId);

	// De-select car if any assigned
	Optional.ofNullable(driver.getCar()).ifPresent(c -> {
	    c.setSelected(false);
	    carRepository.save(c);
	});

	driver.setCar(null);
	driverRepository.save(driver);

	return driver;
    }

    private DriverDO findDriverChecked(Long driverId) throws EntityNotFoundException {
	return driverRepository.findDriverChecked(driverId);
    }
}