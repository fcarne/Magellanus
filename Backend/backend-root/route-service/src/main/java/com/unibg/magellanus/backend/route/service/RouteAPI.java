package com.unibg.magellanus.backend.route.service;

import org.springframework.http.ResponseEntity;

public interface RouteAPI {
	public Route get(String id);

	public Route getByItinerary(String id);
	
	public Route create(Route route);

	public ResponseEntity<Void> updateMine(String id, Route route);

	public ResponseEntity<Void> deleteMine(String id);

	public Route autoGenerate(Route route);

}