package com.unibg.magellanus.backend.route.service.distance;

import java.util.List;

import com.unibg.magellanus.backend.route.service.Coordinates;

/**
 * L'API Rest esposta dal servizio di calcolo della matrice delle distanze.
 * 
 * @since 0.3
 */
public interface MatrixAPI {
	/**
	 * Calcola la matrice delle distanze (in metri) a partire da una lista di coordinate.
	 * 
	 * @param coordinates la lista di coordinate di cui calcolare le distanze
	 * @return la matrice delle distanze delle coordinate
	 */
	public MatrixResponse getDistances(List<Coordinates> coordinates);
}
