package com.unibg.magellanus.backend.route.service;

import java.util.NoSuchElementException;

public interface RouteService {
	public Route findById(String id) throws NoSuchElementException;
	
	public Route findByItineraryId(String id) throws NoSuchElementException;

	public Route create(Route route);

	public Route update(String id, Route route, String issuer) throws IllegalStateException;

	public void delete(String id, String issuer) throws IllegalStateException;

	public Route autoGenerate(Route route) throws NoSuchElementException;
}
