package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class SmartCityRoutePlanner extends JFrame {

    private List<Station> stations = new ArrayList<>(); // To store multiple stations
    private NetworkPanel networkPanel = new NetworkPanel(stations); // For drawing the network

    // Constructor to set up the GUI
    public SmartCityRoutePlanner() {
        // Frame settings
        setTitle("Smart City Route Planner");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create a JPanel for input using GridBagLayout for better alignment
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        inputPanel.setBackground(new Color(230, 230, 250)); // Light lavender background
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Labels and fields for station input
        JLabel cityLabel = new JLabel("City Name:");
        JTextField cityField = new JTextField(15);

        JLabel distanceLabel = new JLabel("Distance:");
        JTextField distanceField = new JTextField(15);

        JLabel populationLabel = new JLabel("Population Density:");
        JTextField populationField = new JTextField(15);

        JLabel economicLabel = new JLabel("Economic Wealth:");
        JTextField economicField = new JTextField(15);

        JLabel tourismLabel = new JLabel("Tourism Potential:");
        JTextField tourismField = new JTextField(15);

        // Button to add station
        JButton addStationButton = new JButton("Add Station");
        addStationButton.setBackground(new Color(60, 179, 113)); // Medium Sea Green button
        addStationButton.setForeground(Color.WHITE);

        // Algorithm selection
        JLabel algorithmLabel = new JLabel("Choose Algorithm:");
        String[] algorithms = {"Dijkstra", "A*", "Bellman-Ford"};
        JComboBox<String> algorithmBox = new JComboBox<>(algorithms);

        // Button to calculate route
        JButton calculateButton = new JButton("Calculate Route");
        calculateButton.setBackground(new Color(30, 144, 255)); // Dodger Blue button
        calculateButton.setForeground(Color.WHITE);

        // Set GridBag constraints and add components to input panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(cityLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        inputPanel.add(cityField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(distanceLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        inputPanel.add(distanceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(populationLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        inputPanel.add(populationField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(economicLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        inputPanel.add(economicField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        inputPanel.add(tourismLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        inputPanel.add(tourismField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        inputPanel.add(algorithmLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        inputPanel.add(algorithmBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        inputPanel.add(addStationButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        inputPanel.add(calculateButton, gbc);

        // Add the input panel and network panel to the frame
        add(inputPanel, BorderLayout.NORTH);
        add(networkPanel, BorderLayout.CENTER);

        // Action listener to add stations dynamically
        addStationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String cityName = cityField.getText();
                    int distance = Integer.parseInt(distanceField.getText());
                    int population = Integer.parseInt(populationField.getText());
                    int economic = Integer.parseInt(economicField.getText());
                    int tourism = Integer.parseInt(tourismField.getText());

                    // Calculate significance (e.g., based on population, economic, and tourism potential)
                    int significance = (population + economic + tourism) / 3;

                    // Add station to the list
                    stations.add(new Station(cityName, distance, significance));

                    // Clear the input fields after adding a station
                    cityField.setText("");
                    distanceField.setText("");
                    populationField.setText("");
                    economicField.setText("");
                    tourismField.setText("");

                    networkPanel.repaint(); // Redraw the network with the new station
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter valid numerical values.");
                }
            }
        });

        // Action listener for route calculation (placeholder for actual algorithm implementation)
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedAlgorithm = (String) algorithmBox.getSelectedItem();
                // Placeholder: Call the selected algorithm based on the input values
                JOptionPane.showMessageDialog(null, "Optimal route calculated using " + selectedAlgorithm);
            }
        });
    }

    // Inner class to represent a station
    class Station {
        String cityName;
        int distance;
        int size; // Size will vary based on significance

        Station(String cityName, int distance, int size) {
            this.cityName = cityName;
            this.distance = distance;
            this.size = size;
        }
    }

    // Inner class for drawing the network
    class NetworkPanel extends JPanel {
        private List<Station> stations;

        NetworkPanel(List<Station> stations) {
            this.stations = stations;
            setPreferredSize(new Dimension(800, 500));
            setBackground(Color.WHITE); // Set background to white for better contrast
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(new Color(0, 102, 204)); // Set line color to dark blue

            int x = 50;
            int y = 100;

            // Draw each station
            for (Station station : stations) {
                // Vary the size of the station dot based on significance
                int size = Math.max(15, station.size); // Ensure the dot isn't too small
                g.setColor(new Color(100, 149, 237)); // Cornflower Blue fill color for circles
                g.fillOval(x, y, size, size);

                g.setColor(Color.BLACK); // Border color
                g.drawOval(x, y, size, size); // Add a border around the circle

                // Draw the city name centered below the circle
                FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth(station.cityName);
                g.drawString(station.cityName, x + size / 2 - textWidth / 2, y + size + 20);

                // Move the x position for the next station
                x += 150;
            }

            // Draw lines connecting the stations
            for (int i = 0; i < stations.size() - 1; i++) {
                int x1 = 50 + i * 150 + stations.get(i).size / 2;
                int y1 = 100 + stations.get(i).size / 2;
                int x2 = 50 + (i + 1) * 150 + stations.get(i + 1).size / 2;
                int y2 = 100 + stations.get(i + 1).size / 2;
                g.setColor(new Color(0, 102, 204)); // Line color
                g.drawLine(x1, y1, x2, y2);
            }
        }
    }

    public static void main(String[] args) {
        // Create and show the GUI
        SmartCityRoutePlanner frame = new SmartCityRoutePlanner();
        frame.setVisible(true);
    }
}