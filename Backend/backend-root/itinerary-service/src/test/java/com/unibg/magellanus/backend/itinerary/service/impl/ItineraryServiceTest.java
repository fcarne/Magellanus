package com.unibg.magellanus.backend.itinerary.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.unibg.magellanus.backend.itinerary.service.Itinerary;
import com.unibg.magellanus.backend.itinerary.service.ItineraryRepository;

@ExtendWith(MockitoExtension.class)
class ItineraryServiceTest {

	Itinerary testItinerary;

	@Mock
	ItineraryRepository repository;

	@InjectMocks
	ItineraryServiceImpl service;

	Itinerary buildItinerary() {
		Itinerary itinerary = new Itinerary();
		itinerary.setId("test_id");
		itinerary.setName("test_name");
		itinerary.setOwner("test_owner");
		return itinerary;
	}

	@BeforeEach
	void setupItinerary() {
		testItinerary = buildItinerary();
	}

	@Test
	void findById_exists_returnsItinerary() {
		when(repository.findById(anyString())).thenReturn(Optional.of(testItinerary));

		Itinerary i = service.findById(testItinerary.getId());
		assertNotNull(i);
		assertEquals(testItinerary.getId(), i.getId());
	}

	@Test
	void findById_notExists_throwsNoSuchElement() {
		when(repository.findById(anyString())).thenReturn(Optional.empty());
		assertThrows(NoSuchElementException.class, () -> service.findById("NOT_EXISTS"));
	}

	@Test
	void create_ok_returnsItinerary() {
		when(repository.save(testItinerary)).thenReturn(buildItinerary());

		Itinerary i = service.create(testItinerary);
		assertEquals(testItinerary, i);
	}

	@Test
	void update_ownerIsIssuer_returnsItinerary() {
		when(repository.save(testItinerary)).thenReturn(testItinerary);
		testItinerary.setName("new_name");
		Itinerary i = service.update(testItinerary.getId(), testItinerary, testItinerary.getOwner());
		assertEquals(testItinerary.getId(), i.getId());
		assertEquals(testItinerary.getName(), i.getName());
	}

	@Test
	void update_ownerIsNotIssuer_throwsIllegalState() {
		assertThrows(IllegalStateException.class, () -> service.update(testItinerary.getId(), testItinerary, "not_owner"));
	}

	@Test
	void delete_ownerIsIssuer_returnsVoid() {
		when(repository.findById(testItinerary.getId())).thenReturn(Optional.of(testItinerary));
		doNothing().when(repository).deleteById(anyString());
		service.delete(testItinerary.getId(), testItinerary.getOwner());

		verify(repository, times(1)).deleteById(anyString());
	}

	@Test
	void delete_ownerIsNotIssuer_throwsIllegalState() {
		when(repository.findById(testItinerary.getId())).thenReturn(Optional.of(testItinerary));
		assertThrows(IllegalStateException.class, () -> service.delete(testItinerary.getId(), "not_owner"));
	}

}
