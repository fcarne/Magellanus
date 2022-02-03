package com.unibg.magellanus.backend.route.service.alg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.spanning.PrimMinimumSpanningTree;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.springframework.stereotype.Component;

@Component
public class TwoApproximateMetricTSPAlgorithm implements TSPAlgorithm {

	@Override
	public <V, E> List<V> getTour(Graph<V, E> g) {

		// We should check that G is a undirected complete graph which satisfies the
		// triangle inequality. Since this is always the case, we skip this step

		Set<V> vertices = g.vertexSet();
		int n = vertices.size();

		if (n == 0)
			return new LinkedList<>();

		if (n == 1)
			return new LinkedList<>(vertices);

		// 1 - Create a minimum spanning tree T of G
		Graph<V, DefaultEdge> t = new SimpleGraph<>(DefaultEdge.class);
		vertices.forEach(t::addVertex);

		SpanningTreeAlgorithm<E> spanningTreeAlgorithm = new PrimMinimumSpanningTree<>(g);
		spanningTreeAlgorithm.getSpanningTree().getEdges()
				.forEach(e -> t.addEdge(g.getEdgeSource(e), g.getEdgeTarget(e)));


		/* 2 - Duplicate each edge in T to obatin a Eulerian graph E
         * 3 - Form a Eulerian circuit in E
		 * 4 - Make the circuit found in previous step into a Hamiltonian circuit by skipping repeated vertices (shortcutting)
		 *
		 * 
		 * Steps from 2 to 4 can be summarized as follow:
		 * Let H be the list of vertices, ordered according to when they are first visited in a preorder tree walk of T
		 * H is a Hamiltonian circuit 
		 */
		
		List<V> tour = new ArrayList<>(n + 1);
		V start = vertices.iterator().next();
		Iterator<V> dfsTraversal = new DepthFirstIterator<>(t, start);
		while (dfsTraversal.hasNext()) {
			V v = dfsTraversal.next();
			tour.add(v);
		}

		// Add starting node at the end
		tour.add(tour.get(0));
		return tour;
	}
}
