package com.unibg.magellanus.backend.route.service;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface RouteRepository extends CrudRepository<Route, String> {
	List<Route> findByItineraryId(String itineraryId);
}
