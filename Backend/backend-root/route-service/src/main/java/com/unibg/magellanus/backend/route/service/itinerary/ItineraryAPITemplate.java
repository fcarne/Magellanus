package com.unibg.magellanus.backend.route.service.itinerary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/* implementato ma non utilizzato - invia una richiesta all'itinerary-service 
 * per controllare l'esistenza di un dato itinerario. 
 * Aumenta la consistenza del servizio offerto */
@Service
public class ItineraryAPITemplate implements ItineraryAPI {

	RestTemplate restTemplate;

	@Autowired
	public ItineraryAPITemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Override
	public boolean exists(String id) {
		ResponseEntity<Void> header = restTemplate.getForEntity("http://ITINERARY-SERVICE/api/itineraries/get/" + id,
				Void.class);
		return header.getStatusCode().equals(HttpStatus.OK);
	}

}
