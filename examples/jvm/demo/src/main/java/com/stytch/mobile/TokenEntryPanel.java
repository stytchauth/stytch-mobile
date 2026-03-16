package com.stytch.mobile;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Second screen. Collects the Stytch public token (and optionally a keystore password) before
 * launching the chosen experience.
 */
public class TokenEntryPanel extends JPanel {

    public TokenEntryPanel(AppFrame frame) {
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(32, 40, 32, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JLabel title = new JLabel("Configure SDK", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 24, 0);
        add(title, gbc);

        JLabel tokenLabel = new JLabel("Public Token");
        gbc.gridy = 1;
        gbc.insets = new Insets(4, 0, 2, 0);
        add(tokenLabel, gbc);

        JTextField tokenField = new JTextField(20);
        tokenField.setToolTipText("public-token-live-...");
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 16, 0);
        add(tokenField, gbc);

        JButton submitButton = new JButton("Launch");
        submitButton.setFont(submitButton.getFont().deriveFont(14f));
        gbc.gridy = 3;
        gbc.insets = new Insets(16, 64, 0, 64);
        add(submitButton, gbc);

        Runnable onSubmit = () -> {
            String token = tokenField.getText().trim();
            if (token.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a public token.", "Missing Token",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            frame.onTokenSubmitted(token);
        };

        submitButton.addActionListener(e -> onSubmit.run());
        // Allow pressing Enter in the text field to submit.
        tokenField.addActionListener(e -> onSubmit.run());
    }
}
