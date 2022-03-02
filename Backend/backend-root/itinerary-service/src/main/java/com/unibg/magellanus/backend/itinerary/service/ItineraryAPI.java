package com.unibg.magellanus.backend.itinerary.service;

import org.springframework.http.ResponseEntity;

/**
 * L'API Rest esposta dal microservizio.
 * 
 * @since 0.2
 *
 */
public interface ItineraryAPI {
	/**
	 * Restiuisce l'itinerario cercato, dato l'id
	 * 
	 * @param id l'id dell'itienerario cercato
	 * @return l'itinerario cercato
	 */
	public Itinerary get(String id);

	/**
	 * Crea un nuovo itinerario
	 * 
	 * @param itinerary i dati dell'itinerario da creare
	 * @return l'itinerario appena creato
	 */
	public Itinerary create(Itinerary itinerary);

	/**
	 * Aggiorna l'itinerario specificato tramite id, che dev'essere stato creato da
	 * chi ha inviato la richiesta
	 * 
	 * @param id        l'id dell'itinerario da aggiornare
	 * @param itinerary i dati aggiornati dell'itinerario
	 * @return 204 - No content
	 */
	public ResponseEntity<Void> updateMine(String id, Itinerary itinerary);

	/**
	 * 
	 * Elimina l'itinerario specificato tramite id, che dev'essere stato creato da
	 * chi ha inviato la richiesta
	 * 
	 * @param id l'id dell'itinerario da eliminare
	 * @return 204 - No content
	 */
	public ResponseEntity<Void> deleteMine(String id);

	/**
	 * Cerca gli itinerari di proprietà dell'utente che ha inviato la richiesta,
	 * filtrando secondo il valore di completed
	 * 
	 * @param completed, se true restituisce solo gli itinerari completati, se false
	 *                   restituisce i non completati, se null non filtra
	 * @return 204 - No content
	 */
	public Iterable<Itinerary> findMine(Boolean completed);

}