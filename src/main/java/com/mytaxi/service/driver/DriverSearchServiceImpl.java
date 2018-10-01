package com.mytaxi.service.driver;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.mytaxi.dataaccessobject.DriverRepository;
import com.mytaxi.domainobject.CarDO;
import com.mytaxi.domainobject.DriverDO;
import com.mytaxi.domainvalue.OnlineStatus;

@Service
public class DriverSearchServiceImpl implements DriverSearchService {

    private final DriverRepository driverRepository;

    public DriverSearchServiceImpl(final DriverRepository driverRepository) {
	this.driverRepository = driverRepository;
    }

    @Override
    public List<DriverDO> findDrivers(String username, OnlineStatus onlineStatus, String licensePlate, Integer rating) {

	return driverRepository
		.findAll(DriverSpecifications.withDynamicQuery(username, onlineStatus, licensePlate, rating));
    }
}

class DriverSpecifications {

    /**
     * Specification to construct dynamic query based on JPA Criteria API.
     * 
     * @param username
     * @param onlineStatus
     * @param licensePlate
     * @param rating
     * @return Specification<DriverDO> to be used in search
     */
    public static Specification<DriverDO> withDynamicQuery(final String username, final OnlineStatus onlineStatus,
	    final String licensePlate, final Integer rating) {

	return new Specification<DriverDO>() {
	    @Override
	    public Predicate toPredicate(Root<DriverDO> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

		List<Predicate> predicates = new ArrayList<Predicate>();

		if (Optional.ofNullable(username).isPresent()) {
		    predicates.add(cb.like(root.get("username"), username));
		}
		if (Optional.ofNullable(onlineStatus).isPresent()) {
		    predicates.add(cb.equal(root.get("onlineStatus"), onlineStatus));
		}

		if (Optional.ofNullable(licensePlate).isPresent() || Optional.ofNullable(rating).isPresent()) {
		    Join<DriverDO, CarDO> join = root.join("car");

		    if (Optional.ofNullable(licensePlate).isPresent()) {
			predicates.add(cb.like(join.get("licensePlate"), licensePlate));
		    }
		    if (Optional.ofNullable(rating).isPresent()) {
			predicates.add(cb.equal(join.get("rating"), rating));
		    }
		}
		return cb.and(predicates.toArray(new Predicate[predicates.size()]));
	    }
	};
    }
}