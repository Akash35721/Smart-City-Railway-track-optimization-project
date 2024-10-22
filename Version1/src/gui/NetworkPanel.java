package src.gui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

public class NetworkPanel extends JPanel {
    private List<Station> stations;
    private Set<String> highlightedStations = new HashSet<>();
    private Set<String> highlightedEdges = new HashSet<>();
    private Map<String, Point> stationPoints = new HashMap<>();

    public NetworkPanel(List<Station> stations) {
        this.stations = stations;
        setBackground(Color.WHITE);
    }

    public void addStation(Station station) {
        stations.add(station);
        repaint();
    }

    public void addStation(String stationName, int distance) {
        // Add station with a random position for simplicity
        stationPoints.put(stationName, new Point((int) (Math.random() * getWidth()), (int) (Math.random() * getHeight())));
    }

    public void highlightStation(String stationName) {
        highlightedStations.add(stationName);
    }

    public void highlightEdge(String from, String to) {
        highlightedEdges.add(from + "-" + to);
    }

    public void clearHighlights() {
        highlightedStations.clear();
        highlightedEdges.clear();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the network
        for (Map.Entry<String, Point> entry : stationPoints.entrySet()) {
            String stationName = entry.getKey();
            Point point = entry.getValue();
            g.setColor(highlightedStations.contains(stationName) ? Color.RED : Color.BLACK);
            g.fillOval(point.x - 5, point.y - 5, 10, 10);
            g.drawString(stationName, point.x + 5, point.y - 5);
        }

        // Draw the edges
        for (String edge : highlightedEdges) {
            String[] stations = edge.split("-");
            Point from = stationPoints.get(stations[0]);
            Point to = stationPoints.get(stations[1]);
            if (from != null && to != null) {
                g.setColor(Color.RED);
                g.drawLine(from.x, from.y, to.x, to.y);
            }
        }
    }
}