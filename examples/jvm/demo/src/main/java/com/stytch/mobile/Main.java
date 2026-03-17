package com.stytch.mobile;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppPreferences prefs = new AppPreferences(Main.class);
            AppFrame frame = new AppFrame(prefs);
            frame.setVisible(true);
        });
    }
}
