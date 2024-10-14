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

    // Constructor to set up the JFrame
    public SwingView() {
        setTitle("Stadium Seat Reservations");
        setSize(1200, 1100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Add components for visualization (e.g., tree and stadium layout)
        JPanel treePanel = new JPanel();
        //add(treePanel, BorderLayout.CENTER);

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
        SwingView view = new SwingView();
        view.setVisible(true);
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
        File outputfile = new File("image.jpg");
        try {
            ImageIO.write(stadiumImage, "jpg", outputfile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Methods to update the GUI based on the state of the model (e.g., refresh the tree display)
    public void updateTreeVisualization(CowboySeatTree tree) {
        // TODO - Implement This
        // Logic to update the display of the Red-Black Tree
        // E.g., Repaint the panel where the tree is shown
    }

}

