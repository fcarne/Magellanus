package com.unibg.magellanus.backend.user.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

/**
 * L'API Rest esposta dal microservizio.
 * 
 * @since 0.1
 *
 */
public interface UserAccountAPI {
	
	/**
	 * Recupera i dati di un utente, dato l'id.
	 * 
	 * @param uid l'id dell'utente
	 * @return l'utente cercato
	 */
	public User getUser(String uid);

	/**
	 * Crea un nuovo utente.
	 * 
	 * @param user il nuovo utente da creare
	 * @return 204 se la creazione è andata a buon fine
	 */
	public ResponseEntity<Void> signUp(User user);

	/**
	 * Elimina l'utente che ha inviato la richiesta
	 * 
	 * @return 204 - No content
	 */
	public ResponseEntity<Void> deleteMe();

	/**
	 * Aggiorna le preferenze dell'utente che ha inviato la richiesta
	 * 
	 * @param preferences le preferenze aggiornate dell'utente
	 * @return 204 se l'operazione è andata a buon fine
	 */
	public ResponseEntity<Void> updateMyPreferences(Map<String, Object> preferences);

	/**
	 * Restituisce le preferenze dell'utente che ha inviato la richiesta
	 * 
	 * @return una mappa contente le preferenze dell'utente
	 */
	public Map<String, Object> getMyPreferences();

}
