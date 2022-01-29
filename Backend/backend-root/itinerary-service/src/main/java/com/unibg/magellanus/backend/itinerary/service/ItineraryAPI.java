package com.unibg.magellanus.backend.itinerary.service;

import org.springframework.http.ResponseEntity;

public interface ItineraryAPI {
	public Itinerary get(String id);

	public Itinerary create(Itinerary itinerary);

	public ResponseEntity<Void> updateMine(String id, Itinerary itinerary);

	public ResponseEntity<Void> deleteMine(String id);

	public Iterable<Itinerary> findMine(Boolean completed);

}