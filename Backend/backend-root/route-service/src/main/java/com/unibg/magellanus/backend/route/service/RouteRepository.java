package com.unibg.magellanus.backend.route.service;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

/**
 * Repository che si interfaccia con le istanze di MongoDB Atlas. Le opzioni di
 * connessione sono specificate nel file application-dev.yml.
 * 
 * L'oggetto concreto viene costruito da Spring.
 * 
 * @since 0.3
 *
 */
public interface RouteRepository extends CrudRepository<Route, String> {
	/**
	 * Cerca i percorsi (di norma solo uno) relativi all'itinerario specificato
	 * attraverso l'id
	 * 
	 * @param itineraryId l'id dell'itinerario di cui cercare i percorsi
	 * @return la lista di percorsi relativi a un itinerario
	 */
	List<Route> findByItineraryId(String itineraryId);
}
