package com.unibg.magellanus.backend.route.service.alg;

import static com.unibg.magellanus.backend.route.service.alg.TwoApproximateMetricTSPAlgorithmTest.assertHamiltonianAndGetWeight;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.junit.jupiter.api.Test;

//test suite dalla libreria JGraphT
class ChristofidesMetricTSPAlgorithmTest {

	@Test
	public void getTour_2V() {
		int[][] edges = { { 1, 2, 5 } };
		getTourOnInstance(edges, 10);
	}

	@Test
	public void getTour_3V() {
		int[][] edges = { { 1, 2, 5 }, { 1, 3, 5 }, { 2, 3, 9 }, };
		getTourOnInstance(edges, 19);
	}

	@Test
	public void getTour_4V() {
		int[][] edges = new int[][] { { 1, 0, 2 }, { 2, 0, 5 }, { 2, 1, 6 }, { 3, 0, 2 }, { 3, 1, 4 }, { 3, 2, 5 } };
		getTourOnInstance(edges, 15);
	}

	@Test
	public void getTour_5V() {
		int[][] edges = new int[][] { { 1, 0, 8 }, { 2, 0, 4 }, { 2, 1, 4 }, { 3, 0, 5 }, { 3, 1, 8 }, { 3, 2, 6 },
				{ 4, 0, 7 }, { 4, 1, 7 }, { 4, 2, 5 }, { 4, 3, 6 } };
		getTourOnInstance(edges, 26);
	}

	@Test
	public void getTour_6V() {
		int[][] edges = new int[][] { { 1, 0, 3 }, { 2, 0, 6 }, { 2, 1, 7 }, { 3, 0, 6 }, { 3, 1, 7 }, { 3, 2, 7 },
				{ 4, 0, 5 }, { 4, 1, 6 }, { 4, 2, 9 }, { 4, 3, 9 }, { 5, 0, 3 }, { 5, 1, 2 }, { 5, 2, 10 },
				{ 5, 3, 10 }, { 5, 4, 9 } };
		getTourOnInstance(edges, 33);
	}

	@Test
	public void getTour_7V() {
		int[][] edges = new int[][] { { 1, 0, 6 }, { 2, 0, 2 }, { 2, 1, 9 }, { 3, 0, 7 }, { 3, 1, 1 }, { 3, 2, 8 },
				{ 4, 0, 2 }, { 4, 1, 7 }, { 4, 2, 3 }, { 4, 3, 8 }, { 5, 0, 5 }, { 5, 1, 5 }, { 5, 2, 6 }, { 5, 3, 6 },
				{ 5, 4, 3 }, { 6, 0, 4 }, { 6, 1, 5 }, { 6, 2, 5 }, { 6, 3, 6 }, { 6, 4, 2 }, { 6, 5, 5 } };
		getTourOnInstance(edges, 24);
	}

	@Test
	public void getTour_8V() {
		int[][] edges = new int[][] { { 1, 0, 1 }, { 2, 0, 3 }, { 2, 1, 2 }, { 3, 0, 5 }, { 3, 1, 6 }, { 3, 2, 8 },
				{ 4, 0, 4 }, { 4, 1, 5 }, { 4, 2, 7 }, { 4, 3, 4 }, { 5, 0, 6 }, { 5, 1, 7 }, { 5, 2, 9 }, { 5, 3, 6 },
				{ 5, 4, 8 }, { 6, 0, 6 }, { 6, 1, 7 }, { 6, 2, 9 }, { 6, 3, 6 }, { 6, 4, 8 }, { 6, 5, 9 }, { 7, 0, 4 },
				{ 7, 1, 5 }, { 7, 2, 7 }, { 7, 3, 4 }, { 7, 4, 6 }, { 7, 5, 7 }, { 7, 6, 6 } };
		getTourOnInstance(edges, 39);
	}

	@Test
	public void getTour_9V() {
		int[][] edges = new int[][] { { 1, 0, 5 }, { 2, 0, 4 }, { 2, 1, 5 }, { 3, 0, 3 }, { 3, 1, 7 }, { 3, 2, 6 },
				{ 4, 0, 5 }, { 4, 1, 7 }, { 4, 2, 6 }, { 4, 3, 5 }, { 5, 0, 5 }, { 5, 1, 8 }, { 5, 2, 7 }, { 5, 3, 6 },
				{ 5, 4, 8 }, { 6, 0, 5 }, { 6, 1, 8 }, { 6, 2, 7 }, { 6, 3, 6 }, { 6, 4, 8 }, { 6, 5, 7 }, { 7, 0, 5 },
				{ 7, 1, 7 }, { 7, 2, 6 }, { 7, 3, 5 }, { 7, 4, 7 }, { 7, 5, 6 }, { 7, 6, 8 }, { 8, 0, 5 }, { 8, 1, 6 },
				{ 8, 2, 5 }, { 8, 3, 4 }, { 8, 4, 6 }, { 8, 5, 5 }, { 8, 6, 8 }, { 8, 7, 7 }, { 9, 0, 5 }, { 9, 1, 5 },
				{ 9, 2, 4 }, { 9, 3, 3 }, { 9, 4, 5 }, { 9, 5, 4 }, { 9, 6, 8 }, { 9, 7, 7 }, { 9, 8, 6 } };
		getTourOnInstance(edges, 52);
	}

	private void getTourOnInstance(int[][] edges, double optWeight) {
		Graph<Integer, DefaultEdge> graph = constructGraph(edges);

		TSPAlgorithm algorithm = new ChristofidesMetricTSPAlgorithm();
		List<Integer> tour = algorithm.getTour(graph);
		double tourWeight = assertHamiltonianAndGetWeight(graph, tour);
		assertTrue(tourWeight <= 1.5 * optWeight);
	}

	private Graph<Integer, DefaultEdge> constructGraph(int[][] edges) {
		Graph<Integer, DefaultEdge> graph = new DefaultUndirectedWeightedGraph<>(DefaultEdge.class);
		for (int[] edge : edges) {
			DefaultEdge graphEdge = Graphs.addEdgeWithVertices(graph, edge[0], edge[1]);
			graph.setEdgeWeight(graphEdge, edge[2]);
		}

		return graph;

	}

}
