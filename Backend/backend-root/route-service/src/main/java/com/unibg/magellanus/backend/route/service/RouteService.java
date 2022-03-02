package com.unibg.magellanus.backend.route.service;

import java.util.NoSuchElementException;

/**
 * Servizio di generazione dei percorsi. Offre metodi per la creazione, modifica
 * e generazione automatica dei percorsi.
 * 
 * @since 0.3
 *
 */
public interface RouteService {
	/**
	 * Restituisce il percorso con l'id specificato
	 * 
	 * @param id l'id del percorso da cercare
	 * @return il percorso cercato
	 * @throws NoSuchElementException se non esiste un percorso con l'id specificato
	 */
	public Route findById(String id) throws NoSuchElementException;

	/**
	 * Restituisce il percorso (solo uno) relativo all'itinerario specificato tramite id
	 * 
	 * @param id l'id dell'itinerario
	 * @return il percorso relativo all'itinerario specificato
	 * @throws NoSuchElementException se non esiste un percorso relativo all'itinerario specificato
	 */
	public Route findByItineraryId(String id) throws NoSuchElementException;

	/**
	 * Crea un nuovo percorso
	 * 
	 * @param route i dati del cammino da creare
	 * @return il percorso appena creato
	 */
	public Route create(Route route);

	/**
	 * Aggiorna il percorso specificato dall'id
	 * 
	 * @param id l'id del cammino da aggiornare
	 * @param route i dati aggiornati del cammino
	 * @param issuer l'uid dell'utente che ha richiesto l'update
	 * @return il percorso aggiornato
	 * @throws IllegalStateException se l'issuer non ha i permessi per aggiornare il cammino
	 */
	public Route update(String id, Route route, String issuer) throws IllegalStateException;

	/**
	 * Elimina il percorso specificato dall'id
	 * 
	 * @param id l'id del cammino da eliminare
	 * @param issuer l'uid dell'utente che ha richiesto la delete
	 * @throws IllegalStateException se l'issure non ha i permessi per eliminare il cammino
	 */
	public void delete(String id, String issuer) throws IllegalStateException;

	/**
	 * Genera automaticamente un percorso ottimo
	 * 
	 * @param route i dati del percorso da generare
	 * @return il percorso ottimo generato
	 */
	public Route autoGenerate(Route route);
}
