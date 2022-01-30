package com.unibg.magellanus.backend.itinerary.service;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ItineraryRepository extends CrudRepository<Itinerary, String> {

	@Query(value="{ 'owner' : ?0 }", fields="{ 'poiSet' : 0}")
	public Iterable<Itinerary> findByOwner(String owner);
	
	@Query(value="{ 'owner' : ?0, 'completionDate' : {'$ne' : null} }", fields="{ 'poiSet' : 0}")
	public Iterable<Itinerary> findByOwnerAndCompletionDateNotNull(String owner);

	@Query(value="{ 'owner' : ?0, 'completionDate' : null }", fields="{ 'poiSet' : 0}")
	public Iterable<Itinerary> findByOwnerAndCompletionDateIsNull(String owner);

}
