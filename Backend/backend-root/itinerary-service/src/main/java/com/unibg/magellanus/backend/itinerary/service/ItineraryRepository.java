package com.unibg.magellanus.backend.itinerary.service;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository che si interfaccia con le istanze di MongoDB Atlas. Le opzioni di
 * connessione sono specificate nel file application-dev.yml.
 * 
 * L'oggetto concreto viene costruito da Spring.
 * 
 * @since 0.2
 *
 */
public interface ItineraryRepository extends CrudRepository<Itinerary, String> {

	/**
	 * Restituisce la lista di tutti gli itinerari creati da un utente.
	 * 
	 * Non viene incluso l'insieme dei POI aggiunti all'itinerario
	 * 
	 * @param owner l'id dell'utente che ha creato gli itinerari
	 * @return la lista di tutti gli itinerari creati da un utente
	 */
	@Query(value = "{ 'owner' : ?0 }", fields = "{ 'poiSet' : 0}")
	public Iterable<Itinerary> findByOwner(String owner);

	/**
	 * Restituisce la lista di tutti gli itinerari di un utente la cui data di completamento è non nulla.
	 * 
	 * Non viene incluso l'insieme dei POI aggiunti all'itinerario
	 * 
	 * @param owner l'id dell'utente che ha creato gli itinerari
	 * @return la lista di tutti gli itinerari creati da un utente con data non nulla
	 */
	@Query(value = "{ 'owner' : ?0, 'completionDate' : {'$ne' : null} }", fields = "{ 'poiSet' : 0}")
	public Iterable<Itinerary> findByOwnerAndCompletionDateNotNull(String owner);

	/**
	 * Restituisce la lista di tutti gli itinerari di un utente la cui data di completamento è nulla.
	 * 
	 * Non viene incluso l'insieme dei POI aggiunti all'itinerario
	 * 
	 * @param owner l'id dell'utente che ha creato gli itinerari
	 * @return la lista di tutti gli itinerari creati da un utente con data nulla
	 */
	@Query(value = "{ 'owner' : ?0, 'completionDate' : null }", fields = "{ 'poiSet' : 0}")
	public Iterable<Itinerary> findByOwnerAndCompletionDateIsNull(String owner);

}
