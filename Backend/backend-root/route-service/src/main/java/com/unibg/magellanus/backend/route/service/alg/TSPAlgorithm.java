package com.unibg.magellanus.backend.route.service.alg;

import java.util.List;

import org.jgrapht.Graph;

/**
 * Un algoritmo che risolve il <a href=
 * "https://it.wikipedia.org/wiki/Problema_del_commesso_viaggiatore">Problema
 * del commesso viaggiatore</a>.
 */
public interface TSPAlgorithm {
	/**
	 * Genera un ciclo hamiltoniano H a partire da un grafo.
	 * 
	 * @param <V>   il tipo dei vertici
	 * @param <E>   il tipo degli archi
	 * @param graph il grafo di cui calcolare un ciclo hamiltoniano
	 * @return la lista dei nodi secondo il ciclo H
	 */
	public <V, E> List<V> getTour(Graph<V, E> graph);
}
