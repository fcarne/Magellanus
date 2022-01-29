package com.unibg.magellanus.backend.itinerary.service.impl;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.unibg.magellanus.backend.itinerary.service.Itinerary;
import com.unibg.magellanus.backend.itinerary.service.ItineraryAPI;
import com.unibg.magellanus.backend.itinerary.service.ItineraryService;

@RestController
@RequestMapping("/api/itineraries")
public class ItineraryController implements ItineraryAPI {

	ItineraryService service;

	@Autowired
	public ItineraryController(ItineraryService service) {
		this.service = service;
	}

	@Override
	@GetMapping("{id}")
	public Itinerary get(@PathVariable String id) {
		try {
			return service.findById(id);
		} catch (NoSuchElementException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@Override
	@PostMapping
	public Itinerary create(@RequestBody Itinerary itinerary) {
		itinerary.setOwner(getAuthenticatedUid());
		return service.create(itinerary);
	}

	@Override
	@PutMapping("me/{id}")
	public ResponseEntity<Void> updateMine(@PathVariable String id, @RequestBody Itinerary itinerary) {
		try {
			service.update(id, itinerary, getAuthenticatedUid());
			return ResponseEntity.noContent().build();
		} catch (IllegalStateException e) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
		}
	}

	@Override
	@DeleteMapping("me/{id}")
	public ResponseEntity<Void> deleteMine(@PathVariable String id) {
		try {
			service.delete(id, getAuthenticatedUid());
			return ResponseEntity.noContent().build();
		} catch (IllegalStateException e) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
		}
	}

	@Override
	@GetMapping("me")
	public Iterable<Itinerary> findMine(@RequestParam(required = false) Boolean completed) {
		return service.findAll(getAuthenticatedUid(), completed);
	}

	private String getAuthenticatedUid() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
}