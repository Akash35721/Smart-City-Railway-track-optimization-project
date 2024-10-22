package src.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Set;
import java.util.HashSet;

public class SmartCityRoutePlanner extends JFrame {
    private JTextField cityField, distanceField, populationField, economicField, terrainField, disasterField, strategicField;
    private JCheckBox priorityCheckBox;
    private JButton addStationButton, calculateButton;
    private JPanel inputPanel;
    private NetworkPanel networkPanel;
    private Graph graph;

    public SmartCityRoutePlanner() {
        setTitle("Smart City Route Planner");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        graph = new Graph();

        inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        cityField = new JTextField(10);
        distanceField = new JTextField(10);
        populationField = new JTextField(10);
        economicField = new JTextField(10);
        terrainField = new JTextField(10);
        disasterField = new JTextField(10);
        strategicField = new JTextField(10);
        priorityCheckBox = new JCheckBox("Priority");

        JLabel distanceLabel = new JLabel("Distance:");
        JLabel populationLabel = new JLabel("Population Density:");
        JLabel economicLabel = new JLabel("Economic Factor:");
        JLabel terrainLabel = new JLabel("Terrain Factor:");
        JLabel disasterLabel = new JLabel("Disaster Risk:");
        JLabel strategicLabel = new JLabel("Strategic Importance:");

        addStationButton = new JButton("Add Station");
        calculateButton = new JButton("Calculate Route");

        gbc.gridx = 1; gbc.gridy = 0; inputPanel.add(cityField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(distanceLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; inputPanel.add(distanceField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(populationLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; inputPanel.add(populationField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; inputPanel.add(economicLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 3; inputPanel.add(economicField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; inputPanel.add(terrainLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 4; inputPanel.add(terrainField, gbc);
        gbc.gridx = 0; gbc.gridy = 5; inputPanel.add(disasterLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 5; inputPanel.add(disasterField, gbc);
        gbc.gridx = 0; gbc.gridy = 6; inputPanel.add(strategicLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 6; inputPanel.add(strategicField, gbc);
        gbc.gridx = 0; gbc.gridy = 7; inputPanel.add(priorityCheckBox, gbc);
        gbc.gridx = 0; gbc.gridy = 8; inputPanel.add(addStationButton, gbc);
        gbc.gridx = 1; gbc.gridy = 8; inputPanel.add(calculateButton, gbc);

        add(inputPanel, BorderLayout.NORTH);
        networkPanel = new NetworkPanel();
        add(networkPanel, BorderLayout.CENTER);

        addStationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String cityName = cityField.getText();
                    int distance = Integer.parseInt(distanceField.getText());
                    int populationDensity = Integer.parseInt(populationField.getText());
                    int wealthFactor = Integer.parseInt(economicField.getText());
                    int terrainFactor = Integer.parseInt(terrainField.getText());
                    int disasterRisk = Integer.parseInt(disasterField.getText());
                    int strategicImportance = Integer.parseInt(strategicField.getText());
                    boolean isCapitalOrDowntown = priorityCheckBox.isSelected();

                    double weight = WeightCalculator.calculateWeight(populationDensity, wealthFactor, terrainFactor, disasterRisk, false, strategicImportance > 0, isCapitalOrDowntown);
                    graph.addStation(cityName);
                    graph.addEdge(cityName, cityName, weight); // Assuming self-loop for simplicity

                    networkPanel.addStation(cityName, distance);
                    networkPanel.repaint();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error adding station: " + ex.getMessage());
                }
            }
        });

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String startStationName = cityField.getText();
                    String endStationName = distanceField.getText();

                    // Calculate the shortest path
                    Map<Station, Double> distances = Dijkstra.shortestPath(graph, startStationName);
                    List<Station> path = getPath(graph, startStationName, endStationName, distances);

                    // Highlight the path in the GUI
                    highlightPath(path);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error calculating route: " + ex.getMessage());
                }
            }
        });
    }

    private List<Station> getPath(Graph graph, String startName, String endName, Map<Station, Double> distances) {
        List<Station> path = new ArrayList<>();
        Station end = graph.getStation(endName);
        while (end != null) {
            path.add(end);
            double minDistance = Double.MAX_VALUE;
            Station next = null;
            for (Map.Entry<Station, Double> neighborEntry : end.neighbors.entrySet()) {
                Station neighbor = neighborEntry.getKey();
                double distance = distances.get(neighbor);
                if (distance < minDistance) {
                    minDistance = distance;
                    next = neighbor;
                }
            }
            end = next;
        }
        Collections.reverse(path);
        return path;
    }

    private void highlightPath(List<Station> path) {
        // Clear previous highlights
        networkPanel.clearHighlights();

        // Highlight the stations and edges in the path
        for (int i = 0; i < path.size() - 1; i++) {
            Station from = path.get(i);
            Station to = path.get(i + 1);
            networkPanel.highlightStation(from.name);
            networkPanel.highlightStation(to.name);
            networkPanel.highlightEdge(from.name, to.name);
        }

        // Repaint the network panel to show the highlights
        networkPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SmartCityRoutePlanner().setVisible(true);
            }
        });
    }
}

class WeightCalculator {
    public static double calculateWeight(int populationDensity, int wealthFactor, int terrainFactor, int disasterFrequency, boolean isTouristHotspot, boolean isStrategicInterest, boolean isPriority) {
        double weight = 0.0;
        
        weight += populationDensity * 0.2;
        weight += wealthFactor * 0.2;
        weight += terrainFactor * 0.1;
        weight += disasterFrequency * 0.3;
        weight += isTouristHotspot ? 0.1 : 0.0;
        weight += isStrategicInterest ? 0.1 : 0.0;
        weight *= isPriority ? 0.8 : 1.0; // Priority reduces the weight
        
        return weight;
    }
}

class Station {
    String name;
    Map<Station, Double> neighbors;

    public Station(String name) {
        this.name = name;
        this.neighbors = new HashMap<>();
    }

    public void addNeighbor(Station neighbor, double weight) {
        neighbors.put(neighbor, weight);
    }
}

class Graph {
    Map<String, Station> stations;

    public Graph() {
        stations = new HashMap<>();
    }

    public void addStation(String name) {
        stations.put(name, new Station(name));
    }

    public void addEdge(String from, String to, double weight) {
        Station fromStation = stations.get(from);
        Station toStation = stations.get(to);
        if (fromStation != null && toStation != null) {
            fromStation.addNeighbor(toStation, weight);
        }
    }

    public Station getStation(String name) {
        return stations.get(name);
    }
}

class Dijkstra {
    public static Map<Station, Double> shortestPath(Graph graph, String startName) {
        Station start = graph.getStation(startName);
        Map<Station, Double> distances = new HashMap<>();
        PriorityQueue<Station> pq = new PriorityQueue<>(Comparator.comparingDouble(distances::get));

        for (Station station : graph.stations.values()) {
            distances.put(station, Double.MAX_VALUE);
        }
        distances.put(start, 0.0);
        pq.add(start);

        while (!pq.isEmpty()) {
            Station current = pq.poll();
            for (Map.Entry<Station, Double> neighborEntry : current.neighbors.entrySet()) {
                Station neighbor = neighborEntry.getKey();
                double weight = neighborEntry.getValue();
                double newDist = distances.get(current) + weight;
                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    pq.add(neighbor);
                }
            }
        }

        return distances;
    }
}

class NetworkPanel extends JPanel {
    private Map<String, Point> stationPoints = new HashMap<>();

    public void addStation(String name, int distance) {
        // For simplicity, let's just add the station at a random position
        int x = (int) (Math.random() * getWidth());
        int y = (int) (Math.random() * getHeight());
        stationPoints.put(name, new Point(x, y));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw stations
        for (Map.Entry<String, Point> entry : stationPoints.entrySet()) {
            Point p = entry.getValue();
            g.fillOval(p.x - 5, p.y - 5, 10, 10);
            g.drawString(entry.getKey(), p.x, p.y);
        }
    }

    public void clearHighlights() {
        // Implement this method to clear highlights
    }

    public void highlightStation(String name) {
        // Implement this method to highlight a station
    }

    public void highlightEdge(String from, String to) {
        // Implement this method to highlight an edge
    }
}
