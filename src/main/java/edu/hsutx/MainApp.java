package edu.hsutx;

import java.util.logging.Logger;
import java.util.logging.Level;

public class MainApp {
    private static final Logger logger = Logger.getLogger(MainApp.class.getName());

    private static void report(String where, Throwable e) {
        e.printStackTrace(); // still print (use a logger if you prefer)
        javax.swing.SwingUtilities.invokeLater(() ->
                javax.swing.JOptionPane.showMessageDialog(
                        null,
                        where + ":\n" + e.getClass().getSimpleName() + ": " + e.getMessage(),
                        "Unexpected Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE
                )
        );
    }

    public static void main(String[] args) {


        // 1) Catch uncaught exceptions from *any* non-EDT thread
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> report("Uncaught in " + t.getName(), e));

        // 2) Catch exceptions on the EDT by pushing a guarding EventQueue
        java.awt.Toolkit.getDefaultToolkit().getSystemEventQueue().push(new java.awt.EventQueue() {
            @Override protected void dispatchEvent(java.awt.AWTEvent event) {
                try {
                    super.dispatchEvent(event);
                } catch (Throwable e) {
                    report("Uncaught on EDT", e);
                }
            }
        });

        CowboySeatTree seatTree = new CowboySeatTree();
        SwingView view = new SwingView();

        javax.swing.SwingUtilities.invokeLater(() -> {
            view.createAndShowGUI();
            ReservationController controller = new ReservationController(seatTree, view);

            // Kick off background work (no arbitrary sleep needed)
            new Thread(() -> controller.processCSV("data/reservations.csv"), "CSV-Worker").start();
        });
    }
}