package com.unibg.magellanus.backend.itinerary.service;

import java.util.NoSuchElementException;

/**
 * Servizio di gestione degli itinerari. Offre metodi per la creazione, modifica, eliminazione e ricerca degli itinerari.
 * 
 * @since 0.2
 *
 */
public interface ItineraryService {
	/**
	 * Cerca un itinerario dato l'id.
	 * 
	 * @param id l'id dell'itinerario da cercare
	 * @return l'itinerario cercato
	 * @throws NoSuchElementException se non esiste un itinerario con l'id cercato
	 */
	public Itinerary findById(String id) throws NoSuchElementException;

	/**
	 * Crea un nuovo itinerario
	 * @param itinerary i dati del nuovo itinerario
	 * @return l'itinerario appena creato
	 */
	public Itinerary create(Itinerary itinerary);

	/**
	 * Aggiorna un itinerario dato l'id
	 * @param id l'id dell'itinerario da aggiornare
	 * @param itinerary i dati aggiornati dell'itinerario
	 * @param issuer l'uid dell'utente che ha richiesto l'update
	 * @return l'itinerario aggiornato
	 * @throws IllegalStateException se l'issuer non ha i permessi per aggiornare l'itinerario
	 */
	public Itinerary update(String id, Itinerary itinerary, String issuer) throws IllegalStateException;

	/**
	 * Elimina un itinerario dato l'id
	 * @param id l'id dell'itinerario da eliminare
	 * @param issuer l'uid dell'utente che ha richiesto la delete
	 * @throws IllegalStateException se l'issuer non ha i permessi per eliminare l'itinerario
	 */
	public void delete(String id, String issuer) throws IllegalStateException;

	/**
	 * Cerca tutti gli itinerari di un utente, in base al valore di completed filtra quelli che sono stati o meno completati.
	 * 
	 * @param owner l'id dell'utente di cui si stanno cercando gli itinerari 
	 * @param completed se true cerca solo gli itinerari completati, se false solo gli itinerari completati, se null non filtra
	 * @return la lista degli itinerari (filtrati secondo completed)
	 */
	public Iterable<Itinerary> findAll(String owner, Boolean completed);
}
