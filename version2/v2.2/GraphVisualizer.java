import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Node {
    String name;
    int x, y;

    public Node(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }
}

class Edge {
    Node u, v;
    double distance;

    public Edge(Node u, Node v, double distance) {
        this.u = u;
        this.v = v;
        this.distance = distance;
    }
}

public class GraphVisualizer extends JPanel {
    private List<Node> nodes;
    private List<Edge> edges;

    public GraphVisualizer() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        loadNodes("nodes.csv");
        loadEdges("edges.csv");
        repaint();

        // Initialize the combo box for algorithm selection
        String[] algorithms = {"Dijkstra", "A*", "Bellman-Ford"};
        JComboBox<String> algorithmSelector = new JComboBox<>(algorithms);

        // Add ComboBox and Button to UI
        JPanel controlsPanel = new JPanel();
        JButton runAlgorithmButton = new JButton("Run Algorithm");
        runAlgorithmButton.addActionListener(new RunAlgorithmListener(algorithmSelector));

        controlsPanel.add(new JLabel("Select Algorithm:"));
        controlsPanel.add(algorithmSelector);
        controlsPanel.add(runAlgorithmButton);

        JFrame controlFrame = new JFrame("Controls");
        controlFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        controlFrame.setSize(300, 100);
        controlFrame.add(controlsPanel);
        controlFrame.setVisible(true);
    }

    private class RunAlgorithmListener implements ActionListener {
        private JComboBox<String> algorithmSelector;

        public RunAlgorithmListener(JComboBox<String> algorithmSelector) {
            this.algorithmSelector = algorithmSelector;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedAlgorithm = (String) algorithmSelector.getSelectedItem();
            if (selectedAlgorithm.equals("Dijkstra")) {
                Node startNode = nodes.get(0); // Set your starting node here, e.g., first node
                Map<Node, Double> distances = dijkstra(startNode);
                StringBuilder result = new StringBuilder("Shortest distances from " + startNode.name + ":\n");
                for (Map.Entry<Node, Double> entry : distances.entrySet()) {
                    result.append(entry.getKey().name).append(": ").append(entry.getValue()).append(" km\n");
                }
                JOptionPane.showMessageDialog(null, result.toString());
            } else {
                JOptionPane.showMessageDialog(null, "Selected algorithm is not yet implemented.");
            }
        }
    }

    private Map<Node, Double> dijkstra(Node start) {
        Map<Node, Double> distances = new HashMap<>();
        Map<Node, Node> previous = new HashMap<>();
        List<Node> unvisited = new ArrayList<>(nodes);

        for (Node node : nodes) {
            distances.put(node, Double.MAX_VALUE);
            previous.put(node, null);
        }
        distances.put(start, 0.0);

        while (!unvisited.isEmpty()) {
            Node current = getClosestNode(unvisited, distances);
            unvisited.remove(current);

            for (Edge edge : edges) {
                if (edge.u.equals(current)) {
                    Node neighbor = edge.v;
                    double newDist = distances.get(current) + edge.distance;
                    if (newDist < distances.get(neighbor)) {
                        distances.put(neighbor, newDist);
                        previous.put(neighbor, current);
                    }
                }
            }
        }

        return distances;
    }

    private Node getClosestNode(List<Node> unvisited, Map<Node, Double> distances) {
        Node closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Node node : unvisited) {
            double distance = distances.get(node);
            if (distance < minDistance) {
                minDistance = distance;
                closest = node;
            }
        }
        return closest;
    }

    // Load nodes from nodes.csv
    private void loadNodes(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null && !line.trim().isEmpty()) {
                String[] parts = line.split(",");
                String name = parts[0].trim();
                int x = Integer.parseInt(parts[1].trim());
                int y = Integer.parseInt(parts[2].trim());
                nodes.add(new Node(name, x, y));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load edges from edges.csv
    private void loadEdges(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null && !line.trim().isEmpty()) {
                String[] parts = line.split(",");
                String source = parts[0].trim();
                String target = parts[1].trim();
                double distance = Double.parseDouble(parts[2].trim());
                Node u = findNodeByName(source);
                Node v = findNodeByName(target);
                if (u != null && v != null) {
                    edges.add(new Edge(u, v, distance));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Node findNodeByName(String name) {
        for (Node node : nodes) {
            if (node.name.equals(name)) {
                return node;
            }
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw edges
        for (Edge edge : edges) {
            drawEdge(g2, edge.u.x, edge.u.y, edge.v.x, edge.v.y, edge.distance);
        }

        // Draw nodes
        for (Node node : nodes) {
            g2.setColor(Color.RED);
            g2.fillOval(node.x - 15, node.y - 15, 30, 30); // Node size
            g2.setColor(Color.BLACK);
            g2.drawString(node.name, node.x - 15, node.y - 20); // Adjusted position for label
        }
    }

    private void drawEdge(Graphics2D g2, int x1, int y1, int x2, int y2, double weight) {
        g2.setColor(Color.BLACK);
        g2.drawLine(x1, y1, x2, y2);
        int midX = (x1 + x2) / 2;
        int midY = (y1 + y2) / 2;
        g2.setColor(Color.BLUE);
        g2.drawString(String.format("%.0f", weight), midX + 5, midY - 5); // Weight label
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Graph Visualizer");
        GraphVisualizer graphVisualizer = new GraphVisualizer();
        frame.add(graphVisualizer);
        frame.setSize(1000, 800); // Adjust size for better visibility
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
