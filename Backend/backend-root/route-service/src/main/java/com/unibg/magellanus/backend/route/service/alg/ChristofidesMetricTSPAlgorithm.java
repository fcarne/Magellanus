package com.unibg.magellanus.backend.route.service.alg;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.HierholzerEulerianCycle;
import org.jgrapht.alg.interfaces.EulerianCycleAlgorithm;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.interfaces.MatchingAlgorithm.Matching;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm.SpanningTree;
import org.jgrapht.alg.matching.blossom.v5.KolmogorovWeightedPerfectMatching;
import org.jgrapht.alg.spanning.KruskalMinimumSpanningTree;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;
import org.springframework.stereotype.Component;

@Component
public class ChristofidesMetricTSPAlgorithm implements TSPAlgorithm {

	@Override
	public <V, E> List<V> getTour(Graph<V, E> g) {
		
		// We should check that G is a undirected complete graph which satisfies the
		// triangle inequality. Since this is always the case, we skip this step
		
		Set<V> vertices = g.vertexSet();
		int n = vertices.size();
		if (n == 0)
            return new LinkedList<>();
		
		if(n == 1)
			return new LinkedList<>(vertices);
		
        // 1 - Create a minimum spanning tree T of G
        SpanningTreeAlgorithm<E> spanningTreeAlgorithm = new KruskalMinimumSpanningTree<>(g);
        SpanningTree<E> t = spanningTreeAlgorithm.getSpanningTree();
        
        // Add edges to a graph because SpanningTree only has edges
        Graph<V, DefaultEdge> h = new Multigraph<>(null, DefaultEdge::new, false);
        g.vertexSet().forEach(h::addVertex);
        t.getEdges().forEach(e -> h.addEdge(g.getEdgeSource(e), g.getEdgeTarget(e)));

        // 2 - Let O be the set of vertices with odd degree in T (odd checking using last bit)
        Set<V> o = h.vertexSet().stream().filter(v -> (h.edgesOf(v).size() & 1) == 1).collect(Collectors.toSet());
        
        // 3 - Find a minimum-weight perfect matching M in the induced subgraph given by the vertices from O.
        Graph<V, E> subgraph = new AsSubgraph<>(g, o);
        MatchingAlgorithm<V, E> matchingAlgorithm = new KolmogorovWeightedPerfectMatching<>(subgraph);
        Matching<V,E> m = matchingAlgorithm.getMatching();
        
        // 4 - Combine the edges of M and T to form a connected multigraph H in which each vertex has even degree
        m.getEdges().forEach(e -> h.addEdge(g.getEdgeSource(e), g.getEdgeTarget(e)));

        // 5 - Form a Eulerian circuit in H
        EulerianCycleAlgorithm<V, DefaultEdge> eulerianCycleAlgorithm = new HierholzerEulerianCycle<>();
        List<V> cycle = eulerianCycleAlgorithm.getEulerianCycle(h).getVertexList();

        // 6 - Make the circuit found in previous step into a Hamiltonian circuit by skipping repeated vertices (shortcutting)
        int actualSize = (int) (n * 4.0/3.0) + 1; // big enough to avoid re-hashing
        Set<V> visited = new HashSet<>(actualSize);
        List<V> tour = cycle.stream().filter(visited::add).collect(Collectors.toList());
        
		// Add starting node at the end
		tour.add(tour.get(0));
        return tour;
	}
}