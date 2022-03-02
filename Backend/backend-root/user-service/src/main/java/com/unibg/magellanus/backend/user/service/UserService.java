package com.unibg.magellanus.backend.user.service;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Servizio di gestione degli utenti. Offre metodi per la creazione di un utente e di eliminazione e gestione delle preferenze
 * 
 * @since 0.1
 *
 */
public interface UserService {
	/**
	 * Recupera i dati di un utente, dato l'id.
	 * 
	 * @param uid l'id dell'utente
	 * @return l'utente cercato
	 * @throws NoSuchElementException se non esiste un utente con l'id cercato
	 */
	public User get(String uid) throws NoSuchElementException;

	/**
	 * Crea un nuovo utente.
	 * 
	 * @param user il nuovo utente da creare
	 * @return l'utente appena creato
	 * @throws IllegalArgumentException se l'id specificato in user è già presente nel database
	 */
	public User signUp(User user) throws IllegalArgumentException;

	/**
	 * Elimina dal database l'utente con l'id specificato
	 * 
	 * @param uid l'id dell'utente da eliminare
	 */
	public void delete(String uid);

	/**
	 * Aggiorna le preferenze dell'utente specificato
	 * 
	 * @param uid l'id dell'utente a cui aggiornare le preferenze
	 * @param preferences le preferenze aggiornate
	 * @return l'utente con le preferenze aggiornate
	 * @throws NoSuchElementException se non esiste un utente con l'id specificato
	 */
	public User updatePreferences(String uid, Map<String, Object> preferences) throws NoSuchElementException;

	/**
	 * Restituisce le preferenze dell'utente specificato
	 * 
	 * @param uid l'id dell'utente del quale si vogliono recuperare le preferenze 
	 * @return una Map contente le preferenze dell'utente specificato
	 * @throws NoSuchElementException se non esiste un utente con l'id specificato
	 */
	public Map<String, Object> getPreferences(String uid) throws NoSuchElementException;

}
