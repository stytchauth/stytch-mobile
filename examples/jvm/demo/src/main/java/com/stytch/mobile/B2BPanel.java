package com.stytch.mobile;

import com.stytch.sdk.b2b.StytchB2B;
import com.stytch.sdk.b2b.StytchB2BKt;
import com.stytch.sdk.b2b.data.B2BAuthenticationState;
import com.stytch.sdk.b2b.session.B2BSessionsClientCallbacksKt;
import com.stytch.sdk.data.EndpointOptions;
import com.stytch.sdk.data.StytchClientConfiguration;
import kotlin.Unit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Main B2B experience panel.
 *
 * <p>Layout:
 * <ul>
 *   <li>NORTH — status label ("Loading…" / "Welcome Back" / "Please Login")
 *   <li>CENTER — scrollable column of action sections (OAuth)
 *   <li>SOUTH — response textarea + SWITCH DEMOS button
 * </ul>
 *
 * <p>Threading notes:
 * <ul>
 *   <li>{@code authenticationStateObserver} callbacks arrive on the Swing EDT (because
 *       {@code kotlinx-coroutines-swing} is on the classpath, making {@code Dispatchers.Main}
 *       the EDT). No {@code invokeLater} needed there.
 *   <li>Callback extension calls ({@code B2BSessionsClientCallbacksKt}, etc.) run on
 *       {@code Dispatchers.Default} (a background pool). All UI updates inside those
 *       callbacks must be wrapped in {@code SwingUtilities.invokeLater()}.
 * </ul>
 */
public class B2BPanel extends JPanel {

    // A fixed keystore password is fine for a demo. In a real app you would derive or store this
    // securely — it protects the local encryption keystore, not the network credentials.
    private static final String KEYSTORE_PASSWORD = "stytch-demo-keystore";

    private final AppFrame frame;
    private final StytchB2B stytch;

    // State
    private B2BAuthenticationState currentAuthState = new B2BAuthenticationState.Loading();

    // UI components that need updating at runtime
    private JLabel statusLabel;
    private JButton googleButton;
    private JTextArea responseArea;

    public B2BPanel(AppFrame frame, AppPreferences prefs) {
        this.frame = frame;

        StytchClientConfiguration config = new StytchClientConfiguration(
                prefs.getPublicToken(),
                B2BPanel.class,
                "1.0.0",
                KEYSTORE_PASSWORD,
                new EndpointOptions(),
                null
        );
        stytch = StytchB2BKt.createStytchB2B(config);

        buildUI();

        // authenticationStateObserver uses Dispatchers.Main (= Swing EDT via coroutines-swing),
        // so we can update the UI directly here — no invokeLater needed.
        stytch.authenticationStateObserver(state -> {
            currentAuthState = state;
            updateStatusLabel();
            return Unit.INSTANCE;
        });
    }

    // -------------------------------------------------------------------------
    // UI construction
    // -------------------------------------------------------------------------

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(16, 20, 16, 20));

        add(buildHeaderPanel(), BorderLayout.NORTH);
        add(buildScrollableContent(), BorderLayout.CENTER);
        add(buildFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel appTitle = new JLabel("Stytch B2B Demo", SwingConstants.CENTER);
        appTitle.setFont(appTitle.getFont().deriveFont(Font.BOLD, 18f));
        panel.add(appTitle, BorderLayout.NORTH);

        statusLabel = new JLabel("Loading…", SwingConstants.CENTER);
        statusLabel.setFont(statusLabel.getFont().deriveFont(14f));
        statusLabel.setBorder(new EmptyBorder(6, 0, 0, 0));
        panel.add(statusLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JScrollPane buildScrollableContent() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(4, 0, 4, 0));

        content.add(buildOAuthSection());
        content.add(Box.createVerticalStrut(14));
        content.add(buildSwitchDemosSection());

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        return scrollPane;
    }

    private JPanel buildOAuthSection() {
        JPanel section = titledSection("OAuth");
        section.setLayout(new GridLayout(1, 1, 0, 0));

        // OAuth is not supported on JVM — button is present but permanently disabled.
        googleButton = new JButton("Google Login");
        googleButton.setEnabled(false);
        googleButton.setToolTipText("OAuth is not supported on JVM");

        section.add(googleButton);
        return section;
    }

    private JPanel buildSwitchDemosSection() {
        JPanel section = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton btn = new JButton("SWITCH DEMOS");
        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 13f));
        btn.setForeground(new Color(180, 30, 30));
        btn.addActionListener(e -> handleSwitchDemos(btn));

        section.add(btn);
        return section;
    }

    private JPanel buildFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));

        JLabel responseLabel = new JLabel("Last response:");
        responseLabel.setFont(responseLabel.getFont().deriveFont(Font.BOLD, 12f));
        panel.add(responseLabel, BorderLayout.NORTH);

        responseArea = new JTextArea(7, 0);
        responseArea.setEditable(false);
        responseArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        responseArea.setLineWrap(true);
        responseArea.setWrapStyleWord(true);
        responseArea.setBorder(new EmptyBorder(4, 6, 4, 6));

        JScrollPane scroll = new JScrollPane(responseArea);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // -------------------------------------------------------------------------
    // Runtime updates
    // -------------------------------------------------------------------------

    private void updateStatusLabel() {
        if (currentAuthState instanceof B2BAuthenticationState.Loading) {
            statusLabel.setText("Loading…");
        } else if (currentAuthState instanceof B2BAuthenticationState.Authenticated) {
            statusLabel.setText("Welcome Back");
        } else {
            statusLabel.setText("Please Login");
        }
    }

    private void showResponse(Object response) {
        responseArea.setText(response.toString());
        responseArea.setCaretPosition(0);
    }

    // -------------------------------------------------------------------------
    // Action handlers
    // -------------------------------------------------------------------------

    private void handleSwitchDemos(JButton btn) {
        btn.setEnabled(false);
        if (currentAuthState instanceof B2BAuthenticationState.Authenticated) {
            B2BSessionsClientCallbacksKt.revoke(
                    stytch.getSession(),
                    StytchCallbacks.onSuccess(response -> SwingUtilities.invokeLater(() -> frame.onSwitchDemos())),
                    StytchCallbacks.onFailure(error   -> SwingUtilities.invokeLater(() -> frame.onSwitchDemos()))
            );
        } else {
            frame.onSwitchDemos();
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static JPanel titledSection(String title) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title,
                        TitledBorder.LEFT, TitledBorder.TOP),
                new EmptyBorder(4, 8, 8, 8)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height + 48));
        return panel;
    }
}
