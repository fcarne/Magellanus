package com.unibg.magellanus.backend.route.service.impl;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unibg.magellanus.backend.route.service.Coordinates;
import com.unibg.magellanus.backend.route.service.Route;
import com.unibg.magellanus.backend.route.service.RouteRepository;
import com.unibg.magellanus.backend.route.service.RouteService;
import com.unibg.magellanus.backend.route.service.RoutedPOI;
import com.unibg.magellanus.backend.route.service.alg.ChristofidesMetricTSPAlgorithm;
import com.unibg.magellanus.backend.route.service.alg.TSPAlgorithm;
import com.unibg.magellanus.backend.route.service.alg.TwoApproximateMetricTSPAlgorithm;
import com.unibg.magellanus.backend.route.service.distance.MatrixAPI;

@Service
public class RouteServiceImpl implements RouteService {

	RouteRepository repository;
	MatrixAPI matrixAPI;
	Map<Class<?>, TSPAlgorithm> algorithms;

	@Autowired
	public RouteServiceImpl(RouteRepository repository, MatrixAPI matrixAPI, List<TSPAlgorithm> algorithms) {
		this.repository = repository;
		this.matrixAPI = matrixAPI;
		this.algorithms = algorithms.stream().collect(Collectors.toMap(TSPAlgorithm::getClass, Function.identity()));
	}

	@Override
	public Route findById(String id) throws NoSuchElementException {
		return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Requested route not found"));
	}
	

	@Override
	public Route findByItineraryId(String id) throws NoSuchElementException {
		Route r = repository.findByItineraryId(id).iterator().next();
		if(r == null) throw new NoSuchElementException("Requested route not found");
		else return r;
	}

	@Override
	public Route create(Route route) {
		return repository.save(route);
	}

	@Override
	public Route update(String id, Route route, String issuer) throws IllegalStateException {
		if (!route.getOwner().equals(issuer))
			throw new IllegalStateException("Issuer is not the owner of the route");
		route.setId(id);
		return repository.save(route);
	}

	@Override
	public void delete(String id, String issuer) throws IllegalStateException {
		repository.findById(id).ifPresent(i -> {
			if (!i.getOwner().equals(issuer))
				throw new IllegalStateException("Issuer is not the owner of the itinerary");
			repository.deleteById(id);
		});
	}

	@Override
	public Route autoGenerate(Route route) {
		List<RoutedPOI> pois = route.getRoute();
		List<Coordinates> coordinates = pois.stream().map(RoutedPOI::getCoordinates).collect(Collectors.toList());
		double[][] distances = matrixAPI.getDistances(coordinates).getDistances();

		Graph<RoutedPOI, DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		pois.stream().forEachOrdered(u -> {
			int i = pois.indexOf(u);
			graph.addVertex(u);
			for (int j = 0; j < i; j++) {
				DefaultWeightedEdge edge = graph.addEdge(u, pois.get(j));
				graph.setEdgeWeight(edge, Math.min(distances[i][j], distances[j][i]));
			}
		});

		TSPAlgorithm algorithm;
		if (pois.size() <= 15)
			algorithm = algorithms.get(ChristofidesMetricTSPAlgorithm.class);
		else
			algorithm = algorithms.get(TwoApproximateMetricTSPAlgorithm.class);

		List<RoutedPOI> opt = algorithm.getTour(graph);
		for (int k = 0; k < opt.size() - 1; k++) {
			int i = pois.indexOf(opt.get(k));
			int j = pois.indexOf(opt.get(k + 1));
			opt.get(k).setDistance(distances[i][j]);
		}
		opt.remove(opt.size()-1);
		route.setRoute(opt);
		return create(route);
	}

}
