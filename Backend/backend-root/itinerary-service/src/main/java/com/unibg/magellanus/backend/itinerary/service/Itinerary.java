package com.unibg.magellanus.backend.itinerary.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("itineraries")
public class Itinerary {

	@Id
	private String id;
	private String owner;

	private String name;
	private LocalDate completionDate;
	private Set<POI> poiSet;

	public Itinerary() {
		this.poiSet = new HashSet<>();
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDate getCompletionDate() {
		return completionDate;
	}

	public void setCompletionDate(LocalDate completionDate) {
		this.completionDate = completionDate;
	}

	public Set<POI> getPoiSet() {
		return poiSet;
	}

	public void setPoiSet(Set<POI> poiSet) {
		this.poiSet = poiSet;
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
		Itinerary other = (Itinerary) obj;
		return Objects.equals(id, other.id);
	}

}
