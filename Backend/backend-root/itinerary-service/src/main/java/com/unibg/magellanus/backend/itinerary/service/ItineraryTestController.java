package com.unibg.magellanus.backend.itinerary.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/itineraries")
public class ItineraryTestController {

	@GetMapping("/message")
	public String test() {
		return "Itinerary Service is up";
	}
}