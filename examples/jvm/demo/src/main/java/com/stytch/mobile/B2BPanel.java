package com.stytch.mobile;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Placeholder for the B2B experience. Full implementation coming in a future release.
 */
public class B2BPanel extends JPanel {

    public B2BPanel(AppFrame frame, AppPreferences prefs) {
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(32, 40, 32, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel label = new JLabel("B2B — coming soon!", SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 20f));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 32, 0);
        add(label, gbc);

        JButton switchButton = makeSwitchDemosButton(frame);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 64, 0, 64);
        add(switchButton, gbc);
    }

    static JButton makeSwitchDemosButton(AppFrame frame) {
        JButton btn = new JButton("SWITCH DEMOS");
        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 13f));
        btn.setForeground(new Color(180, 30, 30));
        btn.addActionListener(e -> frame.onSwitchDemos());
        return btn;
    }
}
