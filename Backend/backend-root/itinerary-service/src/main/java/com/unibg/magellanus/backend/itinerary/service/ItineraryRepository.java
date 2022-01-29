package com.unibg.magellanus.backend.itinerary.service;

import org.springframework.data.repository.CrudRepository;

public interface ItineraryRepository extends CrudRepository<Itinerary, String> {

	public Iterable<Itinerary> findByOwner(String owner);

	public Iterable<Itinerary> findByOwnerAndCompletionDateNotNull(String owner);

	public Iterable<Itinerary> findByOwnerAndCompletionDateIsNull(String owner);

}
