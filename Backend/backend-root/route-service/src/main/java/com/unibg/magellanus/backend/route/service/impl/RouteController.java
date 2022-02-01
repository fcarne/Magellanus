package com.unibg.magellanus.backend.route.service.impl;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.unibg.magellanus.backend.route.service.Route;
import com.unibg.magellanus.backend.route.service.RouteAPI;
import com.unibg.magellanus.backend.route.service.RouteService;

@RestController
@RequestMapping("/api/routes")
public class RouteController implements RouteAPI {

	RouteService service;

	@Autowired
	public RouteController(RouteService service) {
		this.service = service;
	}

	@Override
	@GetMapping("{id}")
	public Route get(@PathVariable String id) {
		try {
			return service.findById(id);
		} catch (NoSuchElementException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@Override
	@GetMapping("itinerary/{id}")
	public Route getByItinerary(@PathVariable String id) {
		try {
			return service.findByItineraryId(id);
		} catch (NoSuchElementException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	
	@Override
	@PostMapping
	public Route create(@RequestBody Route route) {
		route.setOwner(getAuthenticatedUid());
		return service.create(route);
	}

	@Override
	@PutMapping("me/{id}")
	public ResponseEntity<Void> updateMine(@PathVariable String id, @RequestBody Route route) {
		try {
			service.update(id, route, getAuthenticatedUid());
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
	@PostMapping("auto")
	public Route autoGenerate(@RequestBody Route route) {
		route.setOwner(getAuthenticatedUid());
		return service.autoGenerate(route);
	}

	private String getAuthenticatedUid() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

}