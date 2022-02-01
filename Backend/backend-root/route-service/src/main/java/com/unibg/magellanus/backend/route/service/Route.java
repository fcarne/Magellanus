package com.unibg.magellanus.backend.route.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("routes")
public class Route {

	@Id
	private String id;
	private String owner;
	private String itineraryId;
	private List<RoutedPOI> route;

	public Route() {
		this.route = new LinkedList<>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}


	public String getItineraryId() {
		return itineraryId;
	}

	public void setItineraryId(String itineraryId) {
		this.itineraryId = itineraryId;
	}

	public List<RoutedPOI> getRoute() {
		return route;
	}

	public void setRoute(List<RoutedPOI> route) {
		this.route = route;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Route other = (Route) obj;
		return Objects.equals(id, other.id);
	}

}
