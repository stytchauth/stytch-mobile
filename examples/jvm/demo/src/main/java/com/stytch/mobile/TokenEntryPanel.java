package com.stytch.mobile;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Second screen. Collects the Stytch public token (and, for B2B, an optional Organization ID)
 * before launching the chosen experience.
 */
public class TokenEntryPanel extends JPanel {

    public TokenEntryPanel(AppFrame frame) {
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(32, 40, 32, 40));

        boolean isB2B = AppPreferences.TYPE_B2B.equals(frame.getPrefs().getDemoAppType());

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

        // Optional Organization ID — shown only for the B2B experience.
        JTextField orgIdField = null;
        if (isB2B) {
            JLabel orgIdLabel = new JLabel("Organization ID (optional)");
            gbc.gridy = 3;
            gbc.insets = new Insets(4, 0, 2, 0);
            add(orgIdLabel, gbc);

            orgIdField = new JTextField(20);
            orgIdField.setToolTipText("organization-id-...");
            gbc.gridy = 4;
            gbc.insets = new Insets(0, 0, 16, 0);
            add(orgIdField, gbc);
        }

        JButton submitButton = new JButton("Launch");
        submitButton.setFont(submitButton.getFont().deriveFont(14f));
        gbc.gridy = isB2B ? 5 : 3;
        gbc.insets = new Insets(16, 64, 0, 64);
        add(submitButton, gbc);

        final JTextField finalOrgIdField = orgIdField;
        Runnable onSubmit = () -> {
            String token = tokenField.getText().trim();
            if (token.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a public token.", "Missing Token",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            String orgId = (finalOrgIdField != null) ? finalOrgIdField.getText().trim() : "";
            frame.onTokenSubmitted(token, orgId);
        };

        submitButton.addActionListener(e -> onSubmit.run());
        tokenField.addActionListener(e -> onSubmit.run());
        if (orgIdField != null) {
            orgIdField.addActionListener(e -> onSubmit.run());
        }
    }
}
