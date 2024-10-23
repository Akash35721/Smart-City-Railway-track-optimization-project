
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class GraphApp extends JPanel {

    static class Node {
        String id;
        Point position;

        public Node(String id, Point position) {
            this.id = id;
            this.position = position;
        }
    }

    static class Edge {
        String source;
        String target;
        int weight;

        public Edge(String source, String target, int weight) {
            this.source = source;
            this.target = target;
            this.weight = weight;
        }
    }

    static class Graph {
        private final Map<String, Node> nodes;
        private final Map<String, java.util.List<Edge>> edges; // Explicitly specify java.util.List

        public Graph() {
            nodes = new HashMap<>();
            edges = new HashMap<>();
        }

        public void addNode(Node node) {
            nodes.put(node.id, node);
        }

        public void addEdge(Edge edge) {
            edges.computeIfAbsent(edge.source, k -> new ArrayList<>()).add(edge);
            edges.computeIfAbsent(edge.target, k -> new ArrayList<>()).add(new Edge(edge.target, edge.source, edge.weight));
        }

        public Map<String, Node> getNodes() {
            return nodes;
        }

        public Map<String, java.util.List<Edge>> getEdges() { // Explicitly specify java.util.List
            return edges;
        }
    }

    public static void main(String[] args) {
        String nodeCsvFile = "nodes.csv";
        String edgeCsvFile = "edges.csv";

        Graph graph = readGraphFromCSV(nodeCsvFile, edgeCsvFile);
        visualizeGraph(graph);

        String startNodeId = "Delhi"; // Specify the starting node
        java.util.List<Edge> optimizedEdges = computeShortestPath(graph, startNodeId); // Explicitly specify java.util.List

        Graph optimizedGraph = new Graph();
        for (Edge edge : optimizedEdges) {
            optimizedGraph.addEdge(edge);
            optimizedGraph.addNode(graph.getNodes().get(edge.source));
            optimizedGraph.addNode(graph.getNodes().get(edge.target));
        }
        visualizeGraph(optimizedGraph);
    }

    private static Graph readGraphFromCSV(String nodeCsvFile, String edgeCsvFile) {
        Graph graph = new Graph();
        readNodes(graph, nodeCsvFile);
        readEdges(graph, edgeCsvFile);
        return graph;
    }

    private static void readNodes(Graph graph, String nodeCsvFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(nodeCsvFile))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] values = line.split(",");
                String id = values[0];
                int x = Integer.parseInt(values[1]);
                int y = Integer.parseInt(values[2]);
                graph.addNode(new Node(id, new Point(x, y)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readEdges(Graph graph, String edgeCsvFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(edgeCsvFile))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] values = line.split(",");
                String source = values[0];
                String target = values[1];
                int weight = Integer.parseInt(values[2]);
                graph.addEdge(new Edge(source, target, weight));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static java.util.List<Edge> computeShortestPath(Graph graph, String startNodeId) {
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previousNodes = new HashMap<>();
        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        for (String nodeId : graph.getNodes().keySet()) {
            distances.put(nodeId, Integer.MAX_VALUE);
            previousNodes.put(nodeId, null);
        }
        distances.put(startNodeId, 0);
        queue.add(startNodeId);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            for (Edge edge : graph.getEdges().getOrDefault(current, Collections.emptyList())) {
                String neighbor = edge.target;
                int newDist = distances.get(current) + edge.weight;
                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    previousNodes.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        java.util.List<Edge> optimizedEdges = constructOptimizedEdges(previousNodes, distances); // Explicitly specify java.util.List
        writeOptimizedEdgesToCSV(optimizedEdges);
        
        // Print optimized edges in the terminal
        System.out.println("Optimized Edges:");
        for (Edge edge : optimizedEdges) {
            System.out.printf("Source: %s, Target: %s, Weight: %d%n", edge.source, edge.target, edge.weight);
        }

        return optimizedEdges;
    }

    private static void writeOptimizedEdgesToCSV(java.util.List<Edge> optimizedEdges) { // Explicitly specify java.util.List
        try (PrintWriter writer = new PrintWriter(new FileWriter("optimized_edges.csv"))) {
            writer.println("Source,Target,Weight");
            for (Edge edge : optimizedEdges) {
                writer.printf("%s,%s,%d%n", edge.source, edge.target, edge.weight);
            }
            System.out.println("Optimized edges data written to optimized_edges.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static java.util.List<Edge> constructOptimizedEdges(Map<String, String> previousNodes, Map<String, Integer> distances) { // Explicitly specify java.util.List
        java.util.List<Edge> optimizedEdges = new ArrayList<>(); // Explicitly specify java.util.List
        for (String nodeId : previousNodes.keySet()) {
            String previous = previousNodes.get(nodeId);
            if (previous != null && distances.get(nodeId) < Integer.MAX_VALUE) {
                optimizedEdges.add(new Edge(previous, nodeId, distances.get(nodeId) - distances.get(previous)));
            }
        }
        return optimizedEdges;
    }

    private static void visualizeGraph(Graph graph) {
        JFrame frame = new JFrame("Graph Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1400, 800);
        frame.add(new GraphVisualizer(graph));
        frame.setVisible(true);
    }

    static class GraphVisualizer extends JPanel {
        private Map<String, Node> nodePositions;
        private java.util.List<Edge> edges; // Explicitly specify java.util.List

        public GraphVisualizer(Graph graph) {
            this.nodePositions = graph.getNodes();
            this.edges = flattenEdges(graph.getEdges());
        }

        private java.util.List<Edge> flattenEdges(Map<String, java.util.List<Edge>> edgesMap) { // Explicitly specify java.util.List
            java.util.List<Edge> allEdges = new ArrayList<>(); // Explicitly specify java.util.List
            for (java.util.List<Edge> edgeList : edgesMap.values()) { // Explicitly specify java.util.List
                allEdges.addAll(edgeList);
            }
            return allEdges;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw edges with offsets
            g2d.setColor(new Color(100, 100, 255, 150));
            Map<String, Integer> edgeOffsets = new HashMap<>();

            for (Edge edge : edges) {
                Point sourcePos = nodePositions.get(edge.source).position;
                Point targetPos = nodePositions.get(edge.target).position;
                if (sourcePos != null && targetPos != null) {
                    int offset = edgeOffsets.getOrDefault(edge.source + edge.target, 0);
                    edgeOffsets.put(edge.source + edge.target, offset + 10); // Increment offset for overlapping edges

                    // Calculate mid-point for drawing the edge
                    int midX = (sourcePos.x + targetPos.x) / 2;
                    int midY = (sourcePos.y + targetPos.y) / 2;

                    // Adjust mid-point based on offset
                    double angle = Math.atan2(targetPos.y - sourcePos.y, targetPos.x - sourcePos.x);
                    int offsetX = (int) (offset * Math.cos(angle + Math.PI / 2));
                    int offsetY = (int) (offset * Math.sin(angle + Math.PI / 2));
                    
                    // Draw line with offset
                    g2d.drawLine(sourcePos.x, sourcePos.y, targetPos.x, targetPos.y);
                }
            }

            // Draw nodes
            for (Node node : nodePositions.values()) {
                Point pos = node.position;
                g2d.setColor(new Color(255, 0, 0));
                g2d.fillOval(pos.x - 15, pos.y - 15, 30, 30);
                g2d.setColor(Color.BLACK);
                g2d.drawString(node.id, pos.x - 10, pos.y - 25);
            }
        }
    }
}
