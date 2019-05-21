package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {

	SimpleWeightedGraph<Airport, DefaultWeightedEdge> graph;
	Map<Integer, Airport> idMapAirport;
	Map<Airport, Airport> visited;

	public Model() {

		graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		idMapAirport = new HashMap<Integer, Airport>();
		visited = new HashMap<Airport, Airport>();

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
				double newPeso = ((peso + rotta.getDistance()) / 2);
				System.out.println("Aggiornare peso! Peso vecchio: " + peso + " peso nuovo: " + newPeso);

				graph.setEdgeWeight(edge, newPeso);
			}
		}

		System.out.println("Grafo creato!");
		System.out.format("Vertici: %d\nArchi: %d\n", graph.vertexSet().size(), graph.edgeSet().size());

	}

	public Boolean testConnessione(Integer a1, Integer a2) {
		Set<Airport> visited = new HashSet<>();
		Airport source = idMapAirport.get(a1);
		Airport dest = idMapAirport.get(a2);
		System.out.format("Testo la connessione tra %s e %s", source, dest);

		BreadthFirstIterator<Airport, DefaultWeightedEdge> it = new BreadthFirstIterator<>(this.graph, source);

		while (it.hasNext())
			visited.add(it.next());

		if (visited.contains(dest))
			return true;

		return false;

	}

	public List<Airport> trovaPercorso(Integer a1, Integer a2) {
		
		List<Airport> path = new ArrayList<>();
		Airport source = idMapAirport.get(a1);
		Airport dest = idMapAirport.get(a2);
		visited.put(source, null);
		System.out.format("Cerco il percorso tra %s e %s\n", source, dest);

		BreadthFirstIterator<Airport, DefaultWeightedEdge> it = new BreadthFirstIterator<>(this.graph, source);

		it.addTraversalListener(this.new tListener());

		while (it.hasNext())
			it.next();
		
		if(!visited.containsKey(source)||!visited.containsKey(dest)) {
			// i due areoporti non sono collegati
			return null;
		}
		
		Airport step = dest;
		while(!step.equals(source)) {
			path.add(step);
			step = visited.get(step);
		}
		
		path.add(step);
		
		return path;
			
	
	}

	private class tListener implements TraversalListener<Airport, DefaultWeightedEdge> {

		@Override
		public void connectedComponentFinished(ConnectedComponentTraversalEvent arg0) {
		}

		@Override
		public void connectedComponentStarted(ConnectedComponentTraversalEvent arg0) {
		}

		@Override
		public void edgeTraversed(EdgeTraversalEvent<DefaultWeightedEdge> ev) {

			Airport source = graph.getEdgeSource(ev.getEdge());
			Airport dest = graph.getEdgeTarget(ev.getEdge());

			if (!visited.containsKey(dest) && visited.containsKey(source))
				visited.put(dest, source);
			else if (!visited.containsKey(source) && visited.containsKey(dest))
				visited.put(source, dest);

		}

		@Override
		public void vertexFinished(VertexTraversalEvent<Airport> arg0) {
		}

		@Override
		public void vertexTraversed(VertexTraversalEvent<Airport> arg0) {
		}

	}

}
