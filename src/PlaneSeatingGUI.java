import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.InputMismatchException; // Keep for potential internal use if needed

// ========================================================================
// Seat Class (Same as before)
// ========================================================================
/**
 * Represents the type of seat (Window, Aisle, Middle).
 */
enum SeatType {
    WINDOW, AISLE, MIDDLE
}

/**
 * Represents the status of a seat (Available, Occupied).
 */
enum SeatStatus {
    AVAILABLE, OCCUPIED
}

/**
 * Represents a single seat on the plane.
 */
class Seat {
    private int row;
    private char colChar; // A, B, C, D, E, F
    private SeatType type;
    private SeatStatus status;
    private String passengerName; // To store who booked the seat

    /**
     * Constructor for Seat.
     * @param row The row number.
     * @param colChar The column character (A-F).
     * @param type The type of the seat (Window, Aisle, Middle).
     */
    public Seat(int row, char colChar, SeatType type) {
        this.row = row;
        this.colChar = colChar;
        this.type = type;
        this.status = SeatStatus.AVAILABLE; // Initially available
        this.passengerName = null;
    }

    // --- Getters ---
    public int getRow() { return row; }
    public char getColChar() { return colChar; }
    public SeatType getType() { return type; }
    public SeatStatus getStatus() { return status; }
    public String getPassengerName() { return passengerName; }

    // --- Setters ---
    public void setStatus(SeatStatus status) { this.status = status; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }

    /**
     * Returns a string representation of the seat for display.
     * Shows row and column (e.g., "1A") if available, or "XX" if occupied.
     * @return String representation of the seat status.
     */
    @Override
    public String toString() {
        if (status == SeatStatus.AVAILABLE) {
            return String.format("%2d%c", row, colChar);
        } else {
            return "XX"; // Indicate occupied seat
        }
    }
}

// ========================================================================
// Plane Class (Modified slightly to return results instead of printing)
// ========================================================================
/**
 * Represents the airplane's seating arrangement.
 */
class Plane {
    private Seat[][] seats;
    private int totalRows;
    private int seatsPerRow; // Assuming a standard layout like 3-3 (A-F)

    /**
     * Constructor for Plane. Initializes the seating layout.
     * @param totalRows The total number of rows on the plane.
     * @param seatsPerRow The number of seats in each row (e.g., 6 for A-F).
     */
    public Plane(int totalRows, int seatsPerRow) {
        if (seatsPerRow != 6) {
            throw new IllegalArgumentException("Currently only supports 6 seats per row (A-F).");
        }
        this.totalRows = totalRows;
        this.seatsPerRow = seatsPerRow;
        this.seats = new Seat[totalRows][seatsPerRow];
        initializeSeats();
    }

    /**
     * Initializes the seat objects in the 2D array.
     */
    private void initializeSeats() {
        for (int r = 0; r < totalRows; r++) {
            for (int c = 0; c < seatsPerRow; c++) {
                int rowNum = r + 1;
                char colChar = (char) ('A' + c);
                SeatType type;
                if (colChar == 'A' || colChar == 'F') type = SeatType.WINDOW;
                else if (colChar == 'C' || colChar == 'D') type = SeatType.AISLE;
                else type = SeatType.MIDDLE;
                seats[r][c] = new Seat(rowNum, colChar, type);
            }
        }
    }

    /**
     * Generates the current seating map as a string.
     * @return A string representing the seat map.
     */
    public String getSeatMapString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Airplane Seating Map ---\n");
        sb.append("   A  B  C   D  E  F\n");
        sb.append("-------------------------\n");

        for (int r = 0; r < totalRows; r++) {
            sb.append(String.format("%2d ", r + 1));
            for (int c = 0; c < seatsPerRow; c++) {
                sb.append(seats[r][c]).append(" ");
                if (c == 2) { // Add aisle space
                    sb.append(" ");
                }
            }
            sb.append("\n");
        }
        sb.append("-------------------------\n");
        sb.append("XX = Occupied Seat\n");
        sb.append("-------------------------\n");
        return sb.toString();
    }

    /**
     * Assigns a specific seat to a passenger.
     * @param row The desired row number (1-based).
     * @param colChar The desired column character (A-F).
     * @param passengerName The name of the passenger.
     * @return A status message indicating success or failure.
     */
    public String assignSeat(int row, char colChar, String passengerName) {
        int r = row - 1;
        int c = Character.toUpperCase(colChar) - 'A';

        if (r < 0 || r >= totalRows || c < 0 || c >= seatsPerRow) {
            return "Error: Invalid seat selection. Row or column out of bounds.";
        }

        Seat seat = seats[r][c];
        if (seat.getStatus() == SeatStatus.AVAILABLE) {
            seat.setStatus(SeatStatus.OCCUPIED);
            seat.setPassengerName(passengerName);
            return "Seat " + row + colChar + " assigned to " + passengerName + ".";
        } else {
            return "Error: Seat " + row + colChar + " is already occupied by " + seat.getPassengerName() + ".";
        }
    }

     /**
     * Assigns the first available seat matching the preference.
     * @param preference The desired seat type (WINDOW, AISLE, MIDDLE).
     * @param passengerName The name of the passenger.
     * @return A status message indicating success or failure.
     */
    public String assignSeatByPreference(SeatType preference, String passengerName) {
        for (int r = 0; r < totalRows; r++) {
            for (int c = 0; c < seatsPerRow; c++) {
                Seat seat = seats[r][c];
                if (seat.getStatus() == SeatStatus.AVAILABLE && seat.getType() == preference) {
                    return assignSeat(seat.getRow(), seat.getColChar(), passengerName);
                }
            }
        }
        return "Sorry, no available " + preference + " seats found.";
    }

    /**
     * Finds and returns information about a specific seat as a string.
     * @param row The row number (1-based).
     * @param colChar The column character (A-F).
     * @return A string with the seat's information or an error message.
     */
     public String findSeatInfo(int row, char colChar) {
        int r = row - 1;
        int c = Character.toUpperCase(colChar) - 'A';

        if (r < 0 || r >= totalRows || c < 0 || c >= seatsPerRow) {
            return "Error: Invalid seat coordinates.";
        }

        Seat seat = seats[r][c];
        StringBuilder sb = new StringBuilder();
        sb.append("\n--- Seat Information ---\n");
        sb.append("Seat: ").append(seat.getRow()).append(seat.getColChar()).append("\n");
        sb.append("Type: ").append(seat.getType()).append("\n");
        sb.append("Status: ").append(seat.getStatus()).append("\n");
        if (seat.getStatus() == SeatStatus.OCCUPIED) {
            sb.append("Occupied by: ").append(seat.getPassengerName()).append("\n");
        }
        sb.append("------------------------\n");
        return sb.toString();
    }
}


// ========================================================================
// PlaneSeatingGUI Class (New GUI Implementation)
// ========================================================================
/**
 * Creates the GUI for the Plane Seating application using Swing.
 */
public class PlaneSeatingGUI extends JFrame implements ActionListener {

    private Plane plane;
    private JTextArea outputArea; // Mimics the console output
    private JButton displayMapButton, assignSpecificButton, assignPreferenceButton, findSeatButton, exitButton;

    /**
     * Constructor for the GUI.
     */
    public PlaneSeatingGUI() {
        // 1. Initialize the Plane logic
        plane = new Plane(10, 6); // 10 rows, 6 seats (A-F)

        // 2. Set up the main window (JFrame)
        setTitle("Airline Seating Program");
        setSize(550, 500); // Adjusted size for better layout
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setLayout(new BorderLayout(10, 10)); // Use BorderLayout with gaps

        // 3. Create the output area (JTextArea)
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12)); // Monospaced font for map alignment
        outputArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        JScrollPane scrollPane = new JScrollPane(outputArea); // Make it scrollable

        // 4. Create the control panel (JPanel) for buttons
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Flow layout for buttons
        controlPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); // Padding

        // 5. Create Buttons
        displayMapButton = new JButton("Display Seat Map");
        assignSpecificButton = new JButton("Assign Specific Seat");
        assignPreferenceButton = new JButton("Assign by Preference");
        findSeatButton = new JButton("Find Seat Info");
        exitButton = new JButton("Exit");

        // 6. Add Action Listeners to Buttons
        displayMapButton.addActionListener(this);
        assignSpecificButton.addActionListener(this);
        assignPreferenceButton.addActionListener(this);
        findSeatButton.addActionListener(this);
        exitButton.addActionListener(this);

        // 7. Add Buttons to the control panel
        controlPanel.add(displayMapButton);
        controlPanel.add(assignSpecificButton);
        controlPanel.add(assignPreferenceButton);
        controlPanel.add(findSeatButton);
        controlPanel.add(exitButton);

        // 8. Add components to the main window (JFrame)
        add(scrollPane, BorderLayout.CENTER); // Output area in the center
        add(controlPanel, BorderLayout.SOUTH); // Buttons at the bottom

        // 9. Initial message
        outputArea.setText("Welcome to the Airline Seating Program!\nClick a button to perform an action.");

        // 10. Make the window visible
        setVisible(true);
    }

    /**
     * Handles button click events.
     * @param e The ActionEvent triggered by a button press.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        outputArea.append("\n--------------------\n"); // Separator for actions

        try { // Add a general try-catch for unexpected GUI issues
            if (source == displayMapButton) {
                outputArea.setText(plane.getSeatMapString()); // Display fresh map
                outputArea.append("Seat map displayed.");
                 scrollToBottom();
            } else if (source == assignSpecificButton) {
                handleAssignSpecificSeat();
                 scrollToBottom();
            } else if (source == assignPreferenceButton) {
                handleAssignPreferenceSeat();
                 scrollToBottom();
            } else if (source == findSeatButton) {
                handleFindSeatInfo();
                 scrollToBottom();
            } else if (source == exitButton) {
                outputArea.append("Exiting program. Thank you!\n");
                // Optional: Short delay before closing
                // try { Thread.sleep(1000); } catch (InterruptedException ie) {}
                System.exit(0); // Close the application
            }
        } catch (Exception ex) {
             outputArea.append("\nAn unexpected error occurred: " + ex.getMessage());
             ex.printStackTrace(); // Print stack trace to console for debugging
              scrollToBottom();
        }
    }

    /**
     * Handles the process of assigning a specific seat via dialogs.
     */
    private void handleAssignSpecificSeat() {
        String rowStr = JOptionPane.showInputDialog(this, "Enter desired row number:", "Assign Specific Seat", JOptionPane.QUESTION_MESSAGE);
        if (rowStr == null) { outputArea.append("Assignment cancelled.\n"); return; } // User cancelled

        String colStr = JOptionPane.showInputDialog(this, "Enter desired column letter (A-F):", "Assign Specific Seat", JOptionPane.QUESTION_MESSAGE);
        if (colStr == null || colStr.trim().isEmpty()) { outputArea.append("Assignment cancelled.\n"); return; } // User cancelled or empty

        String name = JOptionPane.showInputDialog(this, "Enter passenger name:", "Assign Specific Seat", JOptionPane.QUESTION_MESSAGE);
        if (name == null || name.trim().isEmpty()) { outputArea.append("Assignment cancelled. Passenger name required.\n"); return; } // User cancelled or empty

        try {
            int row = Integer.parseInt(rowStr.trim());
            char colChar = colStr.trim().toUpperCase().charAt(0);

            if (colChar < 'A' || colChar > 'F') {
                 outputArea.append("Error: Invalid column letter. Please enter A-F.\n");
                 return;
            }

            String result = plane.assignSeat(row, colChar, name.trim());
            outputArea.append(result + "\n");
            // Optionally refresh map after assignment
            // outputArea.setText(plane.getSeatMapString());
            // outputArea.append(result + "\n");

        } catch (NumberFormatException nfe) {
            outputArea.append("Error: Invalid row number. Please enter a number.\n");
        } catch (IndexOutOfBoundsException ioobe) {
             outputArea.append("Error: Column letter cannot be empty.\n");
        }
    }

    /**
     * Handles the process of assigning a seat by preference via dialogs.
     */
    private void handleAssignPreferenceSeat() {
        Object[] options = {SeatType.WINDOW, SeatType.AISLE, SeatType.MIDDLE};
        SeatType preference = (SeatType) JOptionPane.showInputDialog(
                this,
                "Select seat preference:",
                "Assign by Preference",
                JOptionPane.PLAIN_MESSAGE,
                null, // icon
                options, // choices
                options[0]); // default choice

        if (preference == null) { outputArea.append("Assignment cancelled.\n"); return; } // User cancelled

        String name = JOptionPane.showInputDialog(this, "Enter passenger name:", "Assign by Preference", JOptionPane.QUESTION_MESSAGE);
        if (name == null || name.trim().isEmpty()) { outputArea.append("Assignment cancelled. Passenger name required.\n"); return; } // User cancelled or empty

        String result = plane.assignSeatByPreference(preference, name.trim());
        outputArea.append(result + "\n");
         // Optionally refresh map after assignment
         // outputArea.setText(plane.getSeatMapString());
         // outputArea.append(result + "\n");
    }

     /**
     * Handles the process of finding seat information via dialogs.
     */
    private void handleFindSeatInfo() {
        String rowStr = JOptionPane.showInputDialog(this, "Enter row number of the seat:", "Find Seat Info", JOptionPane.QUESTION_MESSAGE);
        if (rowStr == null) { outputArea.append("Find operation cancelled.\n"); return; }

        String colStr = JOptionPane.showInputDialog(this, "Enter column letter (A-F) of the seat:", "Find Seat Info", JOptionPane.QUESTION_MESSAGE);
        if (colStr == null || colStr.trim().isEmpty()) { outputArea.append("Find operation cancelled.\n"); return; }

        try {
            int row = Integer.parseInt(rowStr.trim());
            char colChar = colStr.trim().toUpperCase().charAt(0);

             if (colChar < 'A' || colChar > 'F') {
                 outputArea.append("Error: Invalid column letter. Please enter A-F.\n");
                 return;
            }

            String result = plane.findSeatInfo(row, colChar);
            outputArea.append(result); // findSeatInfo already includes formatting

        } catch (NumberFormatException nfe) {
            outputArea.append("Error: Invalid row number. Please enter a number.\n");
        } catch (IndexOutOfBoundsException ioobe) {
             outputArea.append("Error: Column letter cannot be empty.\n");
        }
    }

    /**
     * Utility method to scroll the JTextArea to the bottom.
     */
    private void scrollToBottom() {
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }


    // ========================================================================
    // Main Method (Launches the GUI)
    // ========================================================================
    /**
     * Main method to start the Plane Seating GUI application.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Ensure the GUI is created and updated on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PlaneSeatingGUI(); // Create and show the GUI
            }
        });
    }
}
