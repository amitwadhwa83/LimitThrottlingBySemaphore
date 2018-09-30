package com.mytaxi.dataaccessobject;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mytaxi.domainobject.CarDO;

@Repository
public interface CarRepository extends CrudRepository<CarDO, Long> {

    Optional<CarDO> findByLicensePlate(String licensePlate);
}
