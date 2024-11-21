package GUI;

import javax.swing.*;
import java.awt.*;

public class ExecuteTransactionsFrame extends JFrame {

    public ExecuteTransactionsFrame() {
        setTitle("Execute Transactions");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create panel
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Execute Transactions Panel", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(label, BorderLayout.CENTER);

        // Back button
        JButton backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(e -> {
            dispose();
            new MainFrame().main(null); // Reopen GUI.MainFrame
        });

        panel.add(backButton, BorderLayout.SOUTH);
        add(panel);

        setVisible(true);
    }
}
