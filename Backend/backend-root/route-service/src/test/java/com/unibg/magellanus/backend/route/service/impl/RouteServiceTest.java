package com.unibg.magellanus.backend.route.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.unibg.magellanus.backend.route.service.Coordinates;
import com.unibg.magellanus.backend.route.service.Route;
import com.unibg.magellanus.backend.route.service.RouteRepository;
import com.unibg.magellanus.backend.route.service.RoutedPOI;
import com.unibg.magellanus.backend.route.service.alg.TSPAlgorithm;
import com.unibg.magellanus.backend.route.service.distance.MatrixAPI;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {

	Route testRoute;

	@Mock
	RouteRepository repository;

	@Mock
	MatrixAPI matrixAPI;
	
	@Spy
	List<TSPAlgorithm> algorithms = new ArrayList<TSPAlgorithm>();	
	
	@InjectMocks
	RouteServiceImpl service;

	@BeforeEach
	public void init() {
	    MockitoAnnotations.openMocks(this);
	}
	
	Route buildRoute() {
		Route route = new Route();
		route.setId("test_id");
		route.setOwner("test_owner");
		route.setItineraryId("test_itinerary_id");
		
		RoutedPOI poi = new RoutedPOI();
		poi.setCoordinates(new Coordinates(0,0));
		
		route.getRoute().add(poi);
		return route;
	}

	@BeforeEach
	void setupRoute() {
		testRoute = buildRoute();
	}

	@Test
	void findById_exists_returnsRoute() {
		when(repository.findById(anyString())).thenReturn(Optional.of(testRoute));

		Route r = service.findById(testRoute.getId());
		assertNotNull(r);
		assertEquals(testRoute.getId(), r.getId());
	}

	@Test
	void findById_notExists_throwsNoSuchElement() {
		when(repository.findById(anyString())).thenReturn(Optional.empty());
		assertThrows(NoSuchElementException.class, () -> service.findById("NOT_EXISTS"));
	}

	@Test
	void findByItineraryId_exists_returnsRoute() {
		when(repository.findByItineraryId(anyString())).thenReturn(List.of(testRoute));

		Route r = service.findByItineraryId(testRoute.getItineraryId());
		assertNotNull(r);
		assertEquals(testRoute.getId(), r.getId());
	}

	@Test
	void findByItineraryId_notExists_throwsNoSuchElement() {
		when(repository.findByItineraryId(anyString())).thenReturn(new LinkedList<>());
		assertThrows(NoSuchElementException.class, () -> service.findByItineraryId("NOT_EXISTS"));
	}
	
	@Test
	void create_itineraryExists_returnsRoute() {
		when(repository.save(testRoute)).thenReturn(buildRoute());
		
		Route r = service.create(testRoute);
		assertEquals(testRoute, r);
	}

	@Test
	void update_ownerIsIssuer_returnsItinerary() {
		when(repository.save(testRoute)).thenReturn(testRoute);
		testRoute.setOwner("new_owner");
		Route r = service.update(testRoute.getId(), testRoute, testRoute.getOwner());
		assertEquals(testRoute.getId(), r.getId());
		assertEquals(testRoute.getOwner(), r.getOwner());
	}

	@Test
	void update_ownerIsNotIssuer_throwsIllegalState() {
		assertThrows(IllegalStateException.class, () -> service.update(testRoute.getId(), testRoute, "not_owner"));
	}

	@Test
	void delete_ownerIsIssuer_returnsVoid() {
		when(repository.findById(testRoute.getId())).thenReturn(Optional.of(testRoute));
		doNothing().when(repository).deleteById(anyString());
		service.delete(testRoute.getId(), testRoute.getOwner());

		verify(repository, times(1)).deleteById(anyString());
	}

	@Test
	void delete_ownerIsNotIssuer_throwsIllegalState() {
		when(repository.findById(testRoute.getId())).thenReturn(Optional.of(testRoute));
		assertThrows(IllegalStateException.class, () -> service.delete(testRoute.getId(), "not_owner"));
	}
	
}
