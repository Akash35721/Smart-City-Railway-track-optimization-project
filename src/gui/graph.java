package src.gui;

import java.util.*;

public class Graph {
	private Map<String, Station> stations = new HashMap<>();

	public void addStation(Station station) {
		stations.put(station.getName(), station);
	}

	public void addEdge(String from, String to, double weight) {
		Station fromStation = stations.get(from);
		Station toStation = stations.get(to);
		if (fromStation != null && toStation != null) {
			// Add edge logic here
		}
	}

	public Station getStation(String name) {
		return stations.get(name);
	}

	public Map<Station, Double> shortestPath(String startName) {
		Station start = stations.get(startName);
		Map<Station, Double> distances = new HashMap<>();
		PriorityQueue<Station> pq = new PriorityQueue<>(Comparator.comparingDouble(distances::get));

		for (Station station : stations.values()) {
			distances.put(station, Double.MAX_VALUE);
		}
		distances.put(start, 0.0);
		pq.add(start);

		while (!pq.isEmpty()) {
			Station current = pq.poll();
			// Update distances for neighbors
		}

		return distances;
	}
}
