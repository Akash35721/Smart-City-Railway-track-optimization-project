package src.gui;

import java.util.HashMap;
import java.util.Map;

public class Station {
	private int distance;
	private String name;

	public Station(int distance, String name) {
		this.distance = distance;
		this.name = name;
	}

	public int getDistance() {
		return distance;
	}

	public String getName() {
		return name;
	}
}