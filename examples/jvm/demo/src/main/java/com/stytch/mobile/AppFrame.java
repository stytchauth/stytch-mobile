package com.stytch.mobile;

import javax.swing.*;
import java.awt.*;

/**
 * Root window. Uses a {@link CardLayout} to navigate between the three top-level screens:
 * Selector → TokenEntry → Consumer (or B2B placeholder).
 */
public class AppFrame extends JFrame {

    static final String CARD_SELECTOR    = "SELECTOR";
    static final String CARD_TOKEN_ENTRY = "TOKEN_ENTRY";
    static final String CARD_CONSUMER    = "CONSUMER";
    static final String CARD_B2B         = "B2B";

    private final AppPreferences prefs;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    public AppFrame(AppPreferences prefs) {
        this.prefs = prefs;

        setTitle("Stytch Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 700);
        setMinimumSize(new Dimension(420, 580));
        setLocationRelativeTo(null);
        setContentPane(cardPanel);

        addCard(CARD_SELECTOR,    new SelectorPanel(this));
        addCard(CARD_TOKEN_ENTRY, new TokenEntryPanel(this));

        route();
    }

    /** Called at startup: jump directly to the stored experience or show the selector. */
    private void route() {
        String type = prefs.getDemoAppType();
        if (type == null) {
            cardLayout.show(cardPanel, CARD_SELECTOR);
        } else if (AppPreferences.TYPE_CONSUMER.equals(type)) {
            launchConsumer();
        } else {
            launchB2B();
        }
    }

    /** User tapped a type button on the selector — save type, show token entry. */
    public void onTypeSelected(String type) {
        prefs.setDemoAppType(type);
        // Recreate the panel now that the type is known so it can show the correct fields.
        replaceCard(CARD_TOKEN_ENTRY, new TokenEntryPanel(this));
        cardLayout.show(cardPanel, CARD_TOKEN_ENTRY);
        revalidate();
    }

    /** User submitted a token — launch the appropriate experience. */
    public void onTokenSubmitted(String token, String orgId) {
        prefs.setPublicToken(token);
        prefs.setOrgId(orgId);
        if (AppPreferences.TYPE_CONSUMER.equals(prefs.getDemoAppType())) {
            launchConsumer();
        } else {
            launchB2B();
        }
    }

    /**
     * User tapped SWITCH DEMOS — clear storage and return to the selector.
     * Note: {@link com.stytch.sdk.consumer.DefaultStytchConsumer} is a singleton, so if the
     * user re-enters a different public token, they must restart the app for it to take effect.
     */
    public void onSwitchDemos() {
        prefs.clearAll();
        replaceCard(CARD_TOKEN_ENTRY, new TokenEntryPanel(this));
        removeCard(CARD_CONSUMER);
        removeCard(CARD_B2B);
        cardLayout.show(cardPanel, CARD_SELECTOR);
        revalidate();
    }

    public AppPreferences getPrefs() {
        return prefs;
    }

    // ---- private helpers ----

    private void launchConsumer() {
        replaceCard(CARD_CONSUMER, new ConsumerPanel(this, prefs));
        cardLayout.show(cardPanel, CARD_CONSUMER);
        revalidate();
    }

    private void launchB2B() {
        replaceCard(CARD_B2B, new B2BPanel(this, prefs));
        cardLayout.show(cardPanel, CARD_B2B);
        revalidate();
    }

    /**
     * Adds a panel as a named card, setting the component name so we can look it up later.
     */
    private void addCard(String name, JPanel panel) {
        panel.setName(name);
        cardPanel.add(panel, name);
    }

    private void replaceCard(String name, JPanel panel) {
        removeCard(name);
        addCard(name, panel);
    }

    private void removeCard(String name) {
        for (Component c : cardPanel.getComponents()) {
            if (name.equals(c.getName())) {
                cardPanel.remove(c);
                return;
            }
        }
    }
}
