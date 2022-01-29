package com.unibg.magellanus.backend.itinerary.service;

import java.util.NoSuchElementException;

public interface ItineraryService {
	public Itinerary findById(String id) throws NoSuchElementException;
	public Itinerary create(Itinerary itinerary);
	public Itinerary update(Itinerary itinerary, String issuer) throws IllegalStateException;
	public void delete(String id, String issuer) throws IllegalStateException;
	public Iterable<Itinerary> findAll(String owner, Boolean completed);
}
