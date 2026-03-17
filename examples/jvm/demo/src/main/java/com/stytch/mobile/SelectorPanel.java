package com.stytch.mobile;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * First screen. Shown when no {@code DEMO_APP_TYPE} is stored. The user picks which SDK
 * experience to configure.
 */
public class SelectorPanel extends JPanel {

    public SelectorPanel(AppFrame frame) {
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(32, 32, 32, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JLabel title = new JLabel("Stytch Demo", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 32, 0);
        add(title, gbc);

        JLabel subtitle = new JLabel("Select an SDK to get started:", SwingConstants.CENTER);
        subtitle.setFont(subtitle.getFont().deriveFont(16f));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 24, 0);
        add(subtitle, gbc);

        JButton consumerButton = new JButton("Consumer");
        consumerButton.setFont(consumerButton.getFont().deriveFont(15f));
        consumerButton.addActionListener(e -> frame.onTypeSelected(AppPreferences.TYPE_CONSUMER));
        gbc.gridy = 2;
        gbc.insets = new Insets(8, 64, 8, 64);
        add(consumerButton, gbc);

        JButton b2bButton = new JButton("B2B");
        b2bButton.setFont(b2bButton.getFont().deriveFont(15f));
        b2bButton.addActionListener(e -> frame.onTypeSelected(AppPreferences.TYPE_B2B));
        gbc.gridy = 3;
        add(b2bButton, gbc);
    }
}
