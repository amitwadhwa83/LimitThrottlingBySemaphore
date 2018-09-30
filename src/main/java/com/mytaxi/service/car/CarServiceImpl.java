package com.mytaxi.service.car;

import java.time.ZonedDateTime;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mytaxi.dataaccessobject.CarRepository;
import com.mytaxi.domainobject.CarDO;
import com.mytaxi.exception.ConstraintsViolationException;
import com.mytaxi.exception.EntityNotFoundException;
import com.mytaxi.exception.GenericException;
import com.mytaxi.exception.InvalidCarRatingException;

/**
 * Service to encapsulate the link between DAO and controller and to have
 * business logic for car specific things.
 * <p/>
 */

@Service
public class CarServiceImpl implements CarService {

    private static final Logger LOG = LoggerFactory.getLogger(CarServiceImpl.class);

    private final CarRepository carRepository;
    private final ValueRange range = ValueRange.of(1, 4);

    public CarServiceImpl(final CarRepository carRepository) {
	this.carRepository = carRepository;
    }

    @Override
    public CarDO createCar(CarDO carDO) throws GenericException {
	if (carRepository.findByLicensePlate(carDO.getLicensePlate()).isPresent()) {
	    throw new ConstraintsViolationException(
		    "A car with this license plate already exists:" + carDO.getLicensePlate());
	}
	CarDO car;
	try {
	    car = carRepository.save(carDO);
	} catch (DataIntegrityViolationException e) {
	    LOG.warn("ConstraintsViolationException while creating a car: {}", carDO, e);
	    throw new ConstraintsViolationException(e.getMessage());
	}
	return car;
    }

    @Override
    public CarDO findCar(Long carId) throws GenericException {
	return findCarChecked(carId);
    }

    @Override
    @Transactional
    public CarDO updateCar(Long carId, Integer rating) throws GenericException {
	if (!range.isValidValue(rating)) {
	    throw new InvalidCarRatingException(rating.toString());
	}
	CarDO car = findCarChecked(carId);
	car.setDateUpdated(ZonedDateTime.now());
	car.setRating(rating);
	return car;
    }

    @Override
    @Transactional
    public CarDO deleteCar(Long carId) throws GenericException {
	CarDO car = findCarChecked(carId);
	carRepository.deleteById(carId);
	return car;
    }

    @Override
    public List<CarDO> findCars() {
	List<CarDO> listCardDO = new ArrayList<CarDO>();
	carRepository.findAll().forEach(listCardDO::add);
	return listCardDO;
    }

    private CarDO findCarChecked(Long carId) throws EntityNotFoundException {
	return carRepository.findById(carId).orElseThrow(() -> new EntityNotFoundException(carId.toString()));
    }
}
