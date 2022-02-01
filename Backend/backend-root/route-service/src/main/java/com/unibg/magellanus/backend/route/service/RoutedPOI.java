package com.unibg.magellanus.backend.route.service;

import java.util.Objects;

public class RoutedPOI {
	private Coordinates coordinates;
	private String name;
	private double distance;

	public RoutedPOI() {
	}

	public RoutedPOI(double lat, double lon) {
		this.coordinates = new Coordinates(lat, lon);
	}
	
	
	public Coordinates getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	@Override
	public int hashCode() {
		return Objects.hash(coordinates);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RoutedPOI other = (RoutedPOI) obj;
		return Objects.equals(coordinates, other.coordinates);
	}

	

}
