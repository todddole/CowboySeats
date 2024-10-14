package edu.hsutx;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class SwingView extends JFrame {

    private BufferedImage stadiumImage;
    private JPanel stadiumPanel;
    private int updateCount=0;

    // Constructor to set up the JFrame
    public SwingView() {
        setTitle("Stadium Seat Reservations");
        setSize(2200, 1100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Add components for visualization (e.g., tree and stadium layout)
        JPanel treePanel = new JPanel();
        add(treePanel, BorderLayout.CENTER);

        // Create the stadium image (1000x1000 pixels, all black initially)
        stadiumImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 1000; x++) {
            for (int y = 0; y < 1000; y++) {
                stadiumImage.setRGB(x, y, Color.BLACK.getRGB());
            }
        }

        // Create a JPanel to display the stadium image
        stadiumPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(stadiumImage, 0, 0, this);
            }
        };
        stadiumPanel.setPreferredSize(new Dimension(1000, 1000));
        add(stadiumPanel, BorderLayout.EAST);

        // Add buttons or controls for user interactions (if needed)
        //JPanel controlPanel = new JPanel();
        //add(controlPanel, BorderLayout.SOUTH);
    }

    // Method to show the GUI
    public void createAndShowGUI() {
        this.setVisible(true);
    }

    // Method to update the stadium visualization with colored seats
    public void updateStadiumVisualization(int start, int length, boolean colored) {
        Random rand = new Random();
        Color randomColor = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
        int firstrow=999;
        int lastrow = 0;
        for (int i = start; i < start + length; i++) {
            // Calculate pixel coordinates (x, y) for seat i
            int row = i / 1000;
            if (row<firstrow) firstrow=row;
            if (row>lastrow) lastrow = row;
            int col = (row % 2 == 0) ? (i % 1000) : (999 - (i % 1000));

            if (colored) {
                stadiumImage.setRGB(col, row, randomColor.getRGB());
            } else {
                stadiumImage.setRGB(col, row, Color.BLACK.getRGB());
            }
        }

        // Revalidate and repaint the panel to show the updated image
        stadiumPanel.revalidate();
        stadiumPanel.repaint();
        updateCount++;
        if (updateCount % 20 == 0) {
            File outputfile = new File("image.jpg");
            try {
                ImageIO.write(stadiumImage, "jpg", outputfile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void updateTreeVisualization(CowboySeatTree tree) {
        JPanel treePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (tree != null && tree.getRoot() != null) {
                    drawTree(g, tree.getRoot(), getWidth() / 2, 50, getWidth() / 4);
                }
            }

            // Recursive method to draw the tree nodes and edges
            private void drawTree(Graphics g, CowboySeatTree.Node node, int x, int y, int xOffset) {
                if (node == null) return;

                // Set the node color (red or black)
                g.setColor(node.color ? Color.RED : Color.BLACK);

                // Draw the node as a circle with the key inside it
                g.fillOval(x - 15, y - 15, 30, 30);  // Draw the node circle
                g.setColor(Color.WHITE);  // Set color for the text
                g.drawString(node.key, x - 10, y + 5);  // Draw the key inside the node

                // Draw the left child and the connecting line
                if (node.left != null) {
                    g.setColor(Color.BLACK);  // Line color
                    g.drawLine(x, y, x - xOffset, y + 50);  // Draw a line to the left child
                    drawTree(g, node.left, x - xOffset, y + 50, xOffset / 2);  // Recursive call for the left child
                }

                // Draw the right child and the connecting line
                if (node.right != null) {
                    g.setColor(Color.BLACK);  // Line color
                    g.drawLine(x, y, x + xOffset, y + 50);  // Draw a line to the right child
                    drawTree(g, node.right, x + xOffset, y + 50, xOffset / 2);  // Recursive call for the right child
                }
            }
        };

        treePanel.setPreferredSize(new Dimension(1000, 600));  // Adjust size as needed
        add(treePanel, BorderLayout.CENTER);  // Add the tree panel to the main frame
        treePanel.repaint();  // Repaint to refresh the visualization
    }


}

