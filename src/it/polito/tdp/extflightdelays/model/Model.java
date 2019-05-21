package it.polito.tdp.extflightdelays.model;

import java.util.HashMap;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {

	SimpleWeightedGraph<Airport, DefaultWeightedEdge> graph;
	Map<Integer, Airport> idMapAirport;

	public Model() {

		graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		idMapAirport = new HashMap<Integer, Airport>();

	}

	public void creaGrafo(int distance) {
		ExtFlightDelaysDAO dao = new ExtFlightDelaysDAO();
		dao.loadAllAirports(idMapAirport);

		// Aggiungere i vertici
		Graphs.addAllVertices(graph, idMapAirport.values());

		for (Rotta rotta : dao.getRotte(distance, idMapAirport)) {
			// controllo se esiste gia un arco
			// se esiste, aggiorno il peso
			DefaultWeightedEdge edge = graph.getEdge(rotta.getSource(), rotta.getDestination());
			if (edge == null) {
				Graphs.addEdge(graph, rotta.getSource(), rotta.getDestination(), rotta.getDistance());
			} else {
				double peso = graph.getEdgeWeight(edge);
				double newPeso = (peso + rotta.getDistance() / 2);
				System.out.println("Aggiornare peso! Peso vecchio: " + peso + " peso nuovo: " + newPeso);
				
				graph.setEdgeWeight(edge, newPeso);
			}
		}

		System.out.println("Grafo creato!");
		System.out.format("Vertici: %d\nArchi: %d\n", graph.vertexSet().size(), graph.edgeSet().size());

	}

}
