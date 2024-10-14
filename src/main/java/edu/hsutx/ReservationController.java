package edu.hsutx;

import javax.swing.*;
import java.io.*;
import java.util.HashMap;

public class ReservationController {
    private static final double DELAY_SECONDS = 1.0 ;  // Set this to the number of seconds to pause after updating each entry in the csv file
    private CowboySeatTree seatTree;
    private SwingView view;

    // Constructor to link model and view
    public ReservationController(CowboySeatTree seatTree, SwingView view) {
        this.seatTree = seatTree;
        this.view = view;
    }

    // Method to process the CSV file
    public void processCSV(String filePath) {
        // Logic to read and process the CSV file
        // For each row, perform seat assignment or deletion based on the CSV contents
        // Update the seatTree and refresh the view as necessary
        new Thread(() -> {
            String line;
            HashMap<String, String> keyNamePairs = new HashMap<String, String>();
            BufferedReader br = null;

            seatTree.insert(new SeatAssignment(999999, 1));  //Add starting empty seats to tree


            try {
                File file = new File(filePath);
                br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-16"));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }


            try {
                // Read the file line by line
                while ((line = br.readLine()) != null) {
                    System.out.println("Read " + line);
                    // Split the line by commas
                    String[] values = line.split(",");

                    // Extract the four fields
                    char command = values[0].charAt(0);  // The first field is a single character
                    String name = values[1];             // The second field is a string (name)


                    // The first field is either 'a' for add a reservation or 'd' for delete
                    if (command == 'a') {
                        String seatStr = values[2];          // The third field is a string (seatStr)
                        int preference = Integer.parseInt(values[3]);  // The fourth field is an integer (preference)
                        // add a reservation
                        int seatsNeeded = Integer.parseInt(seatStr);
                        SeatAssignment emptyBlock = seatTree.getUnassignedBlockOverThreshold(seatsNeeded);
                        if (emptyBlock == null) {
                            System.out.println("Error adding " + seatsNeeded + " seats for " + name + " -- no sufficient free block found.");
                            continue;
                        }

                        int openSeats = emptyBlock.getSeatQuantity();
                        int openSeatStart = emptyBlock.getSeatStartIndex();

                        if (openSeats == seatsNeeded) {
                            // Case where the seats needed exactly equal the block size
                            seatTree.delete(emptyBlock);
                            System.out.println("Deleted " + emptyBlock.getKey());
                            SeatAssignment addAsg = new SeatAssignment(name, seatsNeeded, openSeatStart);
                            seatTree.insert(addAsg);
                            System.out.println("Inserted " + addAsg.getKey());
                            keyNamePairs.put(name, addAsg.getKey());
                            SwingUtilities.invokeLater(() -> view.updateStadiumVisualization(openSeatStart, seatsNeeded, true));
                            SwingUtilities.invokeLater(() -> view.updateTreeVisualization(seatTree));
                            continue;
                        }

                        int startSeat = (int) ((openSeats - seatsNeeded) * (preference / 100.0)) + openSeatStart;
                        seatTree.delete(emptyBlock);
                        System.out.println("Deleted " + emptyBlock.getKey());
                        SeatAssignment addAsg = new SeatAssignment(name, seatsNeeded, openSeatStart);
                        seatTree.insert(addAsg);
                        System.out.println("Inserted " + addAsg.getKey());
                        keyNamePairs.put(name, addAsg.getKey());
                        SwingUtilities.invokeLater(() -> view.updateStadiumVisualization(startSeat, seatsNeeded, true));

                        // If needed, add a new unassigned block left of the new SeatAssignment
                        int leftSeats = startSeat - openSeatStart;
                        if (leftSeats > 0) {
                            SeatAssignment addLeft = new SeatAssignment(leftSeats, openSeatStart);
                            seatTree.insert(addLeft);
                            System.out.println("Inserted " + addLeft.getKey());
                        }

                        // If needed, add a new unassigned block right of the new SeatAssignment
                        if (leftSeats + seatsNeeded < openSeats) {
                            SeatAssignment addRight = new SeatAssignment(openSeats - leftSeats - seatsNeeded, startSeat + seatsNeeded);
                            seatTree.insert(addRight);
                            System.out.println("Inserted " + addRight.getKey());
                        }


                        SwingUtilities.invokeLater(() -> view.updateTreeVisualization(seatTree));

                    } else if (command == 'd') {
                        // delete a reservation
                        String key = keyNamePairs.get(name);
                        seatTree.delete(key);
                        System.out.println("Deleted " + key);
                        keyNamePairs.remove(name);
                        int leftSeats = Integer.parseInt(key.substring(0, 6));
                        int openSeatStart = Integer.parseInt(key.substring(6));
                        SeatAssignment addEmpty = new SeatAssignment(leftSeats, openSeatStart);
                        seatTree.insert(addEmpty);
                        System.out.println("Inserted Empty " + addEmpty.getKey());
                        SwingUtilities.invokeLater(() -> view.updateStadiumVisualization(openSeatStart, leftSeats, false));
                    }

                    try {
                        Thread.sleep((long) (DELAY_SECONDS * 1000)); // Sleep for 1000 milliseconds (1 second)
                    } catch (InterruptedException e) {
                        System.err.println("Interrupted: " + e.getMessage());
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
    }
}
