package com.unibg.magellanus.backend.itinerary.service;

import java.util.Objects;

public class POI {
	private double lat;
	private double lon;
	private boolean inRoute;
	
	public POI() {
	}

	public POI(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
		this.setInRoute(false);
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public boolean isInRoute() {
		return inRoute;
	}

	public void setInRoute(boolean inRoute) {
		this.inRoute = inRoute;
	}

	@Override
	public int hashCode() {
		return Objects.hash(lat, lon);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		POI other = (POI) obj;
		return Double.doubleToLongBits(lat) == Double.doubleToLongBits(other.lat)
				&& Double.doubleToLongBits(lon) == Double.doubleToLongBits(other.lon);
	}

}
