package ee.kruskal.algorithm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KruskalAlgorithmVisualizer extends JFrame {
    private final GraphPanel graphPanel;
    private final List<Edge> allEdges;
    private final List<Edge> mstEdges;
    private final Map<Integer, Point> nodePositions;
    private int numVertices;
    private int currentStep;
    private List<Edge> sortedEdges;
    private UnionFind unionFind;
    private Timer animationTimer;

    private final JButton startButton;
    private final JButton stepButton;
    private final JButton resetButton;
    private final JLabel statusLabel;
    private final JLabel costLabel;

    public KruskalAlgorithmVisualizer() {
        setTitle("Kruskal's Algorithm Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(1000, 700);

        allEdges = new ArrayList<>();
        mstEdges = new ArrayList<>();
        nodePositions = new HashMap<>();

        graphPanel = new GraphPanel();
        add(graphPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(240, 240, 240));

        startButton = new JButton("Start Animation");
        stepButton = new JButton("Next Step");
        resetButton = new JButton("Reset");
        statusLabel = new JLabel("Ready to start");
        costLabel = new JLabel("Total MST Cost: 0");

        Font buttonFont = new Font("Arial", Font.BOLD, 13);
        Font labelFont = new Font("Arial", Font.BOLD, 14);

        startButton.setFont(buttonFont);
        startButton.setBackground(new Color(46, 204, 113));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        stepButton.setFont(buttonFont);
        stepButton.setBackground(new Color(52, 152, 219));
        stepButton.setForeground(Color.WHITE);
        stepButton.setFocusPainted(false);
        stepButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        stepButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        resetButton.setFont(buttonFont);
        resetButton.setBackground(new Color(231, 76, 60));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        statusLabel.setFont(labelFont);
        costLabel.setFont(labelFont);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startAnimation();
            }
        });

        stepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performStep();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });

        addHoverEffect(startButton, new Color(46, 204, 113), new Color(39, 174, 96));
        addHoverEffect(stepButton, new Color(52, 152, 219), new Color(41, 128, 185));
        addHoverEffect(resetButton, new Color(231, 76, 60), new Color(192, 57, 43));

        controlPanel.add(startButton);
        controlPanel.add(stepButton);
        controlPanel.add(resetButton);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(statusLabel);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(costLabel);

        add(controlPanel, BorderLayout.SOUTH);

        initializeSampleGraph();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeSampleGraph() {
        numVertices = 7;

        nodePositions.put(0, new Point(200, 150));
        nodePositions.put(1, new Point(400, 100));
        nodePositions.put(2, new Point(600, 150));
        nodePositions.put(3, new Point(150, 350));
        nodePositions.put(4, new Point(400, 300));
        nodePositions.put(5, new Point(650, 350));
        nodePositions.put(6, new Point(400, 500));

        allEdges.add(new Edge(0, 1, 7));
        allEdges.add(new Edge(0, 3, 5));
        allEdges.add(new Edge(1, 2, 8));
        allEdges.add(new Edge(1, 3, 9));
        allEdges.add(new Edge(1, 4, 7));
        allEdges.add(new Edge(2, 4, 5));
        allEdges.add(new Edge(3, 4, 15));
        allEdges.add(new Edge(3, 6, 6));
        allEdges.add(new Edge(4, 5, 8));
        allEdges.add(new Edge(4, 6, 9));
        allEdges.add(new Edge(5, 6, 11));
        allEdges.add(new Edge(2, 5, 12));

        reset();
    }

    private void reset() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        currentStep = 0;
        mstEdges.clear();

        for (Edge edge : allEdges) {
            edge.setInMST(false);
        }

        sortedEdges = new ArrayList<>(allEdges);
        Collections.sort(sortedEdges);

        unionFind = new UnionFind(numVertices);

        statusLabel.setText("Ready to start - " + allEdges.size() + " edges to process");
        costLabel.setText("Total MST Cost: 0");
        startButton.setEnabled(true);
        stepButton.setEnabled(true);

        graphPanel.repaint();
    }

    private void startAnimation() {
        startButton.setEnabled(false);
        stepButton.setEnabled(false);

        animationTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!performStep()) {
                    animationTimer.stop();
                    startButton.setEnabled(true);
                    stepButton.setEnabled(true);
                }
            }
        });
        animationTimer.start();
    }

    private boolean performStep() {
        if (currentStep >= sortedEdges.size()) {
            int totalCost = mstEdges.stream().mapToInt(Edge::getWeight).sum();
            statusLabel.setText("Algorithm Complete! MST has " + mstEdges.size() + " edges");
            costLabel.setText("Total MST Cost: " + totalCost);
            return false;
        }

        Edge currentEdge = sortedEdges.get(currentStep);
        int src = currentEdge.getSource();
        int dest = currentEdge.getDestination();

        if (unionFind.union(src, dest)) {
            currentEdge.setInMST(true);
            mstEdges.add(currentEdge);
            statusLabel.setText("Step " + (currentStep + 1) + ": Added edge " + currentEdge + " to MST");
        } else {
            statusLabel.setText("Step " + (currentStep + 1) + ": Rejected edge " + currentEdge + " (creates cycle)");
        }

        int totalCost = mstEdges.stream().mapToInt(Edge::getWeight).sum();
        costLabel.setText("Total MST Cost: " + totalCost);

        currentStep++;
        graphPanel.repaint();

        return currentStep < sortedEdges.size();
    }

    private void addHoverEffect(JButton button, Color normalColor, Color hoverColor) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(hoverColor);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(normalColor);
            }
        });
    }

    private class GraphPanel extends JPanel {
        private static final int NODE_RADIUS = 25;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setStroke(new BasicStroke(2));
            for (Edge edge : allEdges) {
                Point p1 = nodePositions.get(edge.getSource());
                Point p2 = nodePositions.get(edge.getDestination());

                if (edge.isInMST()) {
                    g2d.setColor(new Color(34, 139, 34));
                    g2d.setStroke(new BasicStroke(4));
                } else {
                    g2d.setColor(Color.LIGHT_GRAY);
                    g2d.setStroke(new BasicStroke(2));
                }

                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);

                int midX = (p1.x + p2.x) / 2;
                int midY = (p1.y + p2.y) / 2;
                g2d.setColor(Color.WHITE);
                g2d.fillRect(midX - 15, midY - 12, 30, 24);

                if (edge.isInMST()) {
                    g2d.setColor(new Color(0, 100, 0));
                } else {
                    g2d.setColor(Color.BLACK);
                }
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                String weight = String.valueOf(edge.getWeight());
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(weight);
                g2d.drawString(weight, midX - textWidth / 2, midY + 5);
            }

            for (Map.Entry<Integer, Point> entry : nodePositions.entrySet()) {
                int nodeId = entry.getKey();
                Point pos = entry.getValue();

                g2d.setColor(new Color(70, 130, 180));
                g2d.fillOval(pos.x - NODE_RADIUS, pos.y - NODE_RADIUS,
                           NODE_RADIUS * 2, NODE_RADIUS * 2);

                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(pos.x - NODE_RADIUS, pos.y - NODE_RADIUS,
                           NODE_RADIUS * 2, NODE_RADIUS * 2);

                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                String label = String.valueOf(nodeId);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(label);
                int textHeight = fm.getAscent();
                g2d.drawString(label, pos.x - textWidth / 2, pos.y + textHeight / 2 - 2);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new KruskalAlgorithmVisualizer();
            }
        });
    }
}
