package com.unibg.magellanus.backend.route.service.alg;

import java.util.List;

import org.jgrapht.Graph;

public interface TSPAlgorithm {
	public <V, E> List<V> getTour(Graph<V, E> graph);
}
