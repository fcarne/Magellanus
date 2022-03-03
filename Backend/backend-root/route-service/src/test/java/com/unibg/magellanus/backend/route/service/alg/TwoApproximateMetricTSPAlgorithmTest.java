package com.unibg.magellanus.backend.route.service.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.spanning.KruskalMinimumSpanningTree;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.util.SupplierUtil;
import org.junit.jupiter.api.Test;

// test suite dalla libreria JGraphT
class TwoApproximateMetricTSPAlgorithmTest {

	@Test
	public void getTour_4Cities() {
		SimpleWeightedGraph<String, DefaultWeightedEdge> g = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

		g.addVertex("A");
		g.addVertex("B");
		g.addVertex("C");
		g.addVertex("D");

		g.setEdgeWeight(g.addEdge("A", "B"), 20);
		g.setEdgeWeight(g.addEdge("A", "C"), 42);
		g.setEdgeWeight(g.addEdge("A", "D"), 35);
		g.setEdgeWeight(g.addEdge("B", "C"), 30);
		g.setEdgeWeight(g.addEdge("B", "D"), 34);
		g.setEdgeWeight(g.addEdge("C", "D"), 12);

		TSPAlgorithm algorithm = new TwoApproximateMetricTSPAlgorithm();
		List<String> tour = algorithm.getTour(g);

		double tourWeight = assertHamiltonianAndGetWeight(g, tour);
		double mstWeight = new KruskalMinimumSpanningTree<>(g).getSpanningTree().getWeight();
		assertTrue(2 * mstWeight >= tourWeight);
	}
	
	@Test
	public void getTour_Star() {
		SimpleWeightedGraph<String, DefaultWeightedEdge> g = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		g.addVertex("1");
		g.addVertex("2");
		g.addVertex("3");
		g.addVertex("4");
		g.addVertex("5");
		g.addVertex("6");

		g.setEdgeWeight(g.addEdge("1", "2"), 1);
		g.setEdgeWeight(g.addEdge("1", "3"), 1);
		g.setEdgeWeight(g.addEdge("1", "4"), 1);
		g.setEdgeWeight(g.addEdge("1", "5"), 2);
		g.setEdgeWeight(g.addEdge("1", "6"), 2);

		g.setEdgeWeight(g.addEdge("2", "3"), 2);
		g.setEdgeWeight(g.addEdge("2", "4"), 1);
		g.setEdgeWeight(g.addEdge("2", "5"), 1);
		g.setEdgeWeight(g.addEdge("2", "6"), 2);

		g.setEdgeWeight(g.addEdge("3", "4"), 1);
		g.setEdgeWeight(g.addEdge("3", "5"), 2);
		g.setEdgeWeight(g.addEdge("3", "6"), 1);

		g.setEdgeWeight(g.addEdge("4", "5"), 1);
		g.setEdgeWeight(g.addEdge("4", "6"), 1);

		g.setEdgeWeight(g.addEdge("5", "6"), 1);

		TSPAlgorithm algorithm = new TwoApproximateMetricTSPAlgorithm();
		List<String> tour = algorithm.getTour(g);

		double tourWeight = assertHamiltonianAndGetWeight(g, tour);
		double mstWeight = new KruskalMinimumSpanningTree<>(g).getSpanningTree().getWeight();
		assertTrue(2 * mstWeight >= tourWeight);
	}


	@Test
	public void getTour_Complete() {
		final int maxSize = 100;

		for (int i = 1; i < maxSize; i++) {
			SimpleGraph<Object, DefaultEdge> g = new SimpleGraph<>(SupplierUtil.OBJECT_SUPPLIER,
					SupplierUtil.DEFAULT_EDGE_SUPPLIER, false);
			CompleteGraphGenerator<Object, DefaultEdge> generator = new CompleteGraphGenerator<>(i);
			generator.generateGraph(g);

			TSPAlgorithm algorithm = new TwoApproximateMetricTSPAlgorithm();
			List<Object> tour = algorithm.getTour(g);

			double tourWeight = assertHamiltonianAndGetWeight(g, tour);
			double mstWeight = new KruskalMinimumSpanningTree<>(g).getSpanningTree().getWeight();

			assertTrue(2 * mstWeight >= tourWeight);
		}
	}

	static <V, E> double assertHamiltonianAndGetWeight(Graph<V, E> g, List<V> tour) {
		if (tour.size() <= 1)
			return 0.0;

		List<E> tourEdges = new ArrayList<>();
		double tourWeight = 0.0;
		V u = tour.get(0);
		for (V v : tour.subList(1, tour.size())) {
			E e = g.getEdge(u, v);
			tourEdges.add(e);
			tourWeight += g.getEdgeWeight(e);
			u = v;
		}

		// check tour length, beginning and end of the tour
		assertEquals(tour.get(0), tour.get(tour.size() - 1));
		assertEquals(g.vertexSet().size(), tour.size() - 1);
		assertEquals(g.vertexSet().size(), tourEdges.size());

		// check tour with edges
		double weight = 0.0;
		V v = tour.get(0);
		Set<V> visited = new HashSet<>();
		for (E e : tourEdges) {
			v = Graphs.getOppositeVertex(g, e, v);
			assertTrue(visited.add(v));
			weight += g.getEdgeWeight(e);
		}
		assertEquals(tourWeight, weight, 1e-9);
		assertEquals(visited.size(), g.vertexSet().size());

		// check tour with vertices
		visited.clear();
		Iterator<V> it = tour.iterator();
		V start = it.next();
		visited.add(start);
		while (it.hasNext()) {
			v = it.next();
			if (!it.hasNext()) {
				assertTrue(v.equals(start));
			} else {
				assertTrue(visited.add(v));
			}
		}
		assertEquals(visited.size(), g.vertexSet().size());

		return tourWeight;
	}

}
