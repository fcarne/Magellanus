package com.unibg.magellanus.backend.route.service;

import org.springframework.http.ResponseEntity;

/**
 * L'API Rest esposta dal microservizio.
 * 
 * @since 0.3
 *
 */
public interface RouteAPI {
	/**
	 * Restituisce il percorso con l'id specificato.
	 * 
	 * @param id l'id del percorso da cercare
	 * @return il percorso relativo all'id specificato
	 */
	public Route get(String id);

	/**
	 * Restituisce il percorso relativo all'itinerario specificato attraverso l'id.
	 * 
	 * @param id l'id dell'itinerario di cui cercare il percorso
	 * @return il percorso relativo all'itinerario specificato
	 */
	public Route getByItinerary(String id);

	/**
	 * Crea un nuovo percorso.
	 * 
	 * @param route i dati del nuovo percorso da creare
	 * @return il percorso appena creato
	 */
	public Route create(Route route);

	/**
	 * Aggiorna il percorso specificato attraverso l'id, solo se è di proprietà
	 * dell'utente che ha inviato la richiesta.
	 * 
	 * @param id    il percorso da aggiornare
	 * @param route i dati aggiornati del percorso
	 * @return 204 - No content
	 */
	public ResponseEntity<Void> updateMine(String id, Route route);

	/**
	 * Elimina il percorso specificato attraverso l'id, solo se è di proprietà
	 * dell'utente che ha inviato la richiesta.
	 * 
	 * @param id il percorso da eliminare
	 * @return 204 - No content
	 */
	public ResponseEntity<Void> deleteMine(String id);

	/**
	 * Genera automaticamente un percorso, sulla base dei dati passati.
	 * 
	 * @param route i dati del percorso da generare
	 * @return il percorso generato automaticamente
	 */
	public Route autoGenerate(Route route);

}