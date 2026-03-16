package com.stytch.mobile;

import com.stytch.sdk.biometrics.BiometricsAvailability;
import com.stytch.sdk.biometrics.BiometricsParameters;
import com.stytch.sdk.consumer.StytchConsumer;
import com.stytch.sdk.consumer.StytchConsumerKt;
import com.stytch.sdk.consumer.biometrics.BiometricsClientCallbacksKt;
import com.stytch.sdk.consumer.data.ConsumerAuthenticationState;
import com.stytch.sdk.consumer.networking.models.OTPsAuthenticateParameters;
import com.stytch.sdk.consumer.networking.models.OTPsSMSLoginOrCreateParameters;
import com.stytch.sdk.consumer.otp.OtpClientCallbacksKt;
import com.stytch.sdk.consumer.otp.SmsOtpClientCallbacksKt;
import com.stytch.sdk.consumer.session.SessionClientCallbacksKt;
import com.stytch.sdk.data.EndpointOptions;
import com.stytch.sdk.data.StytchClientConfiguration;
import kotlin.Unit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Main consumer experience panel.
 *
 * <p>Layout:
 * <ul>
 *   <li>NORTH — status label ("Loading…" / "Welcome Back" / "Please Login")
 *   <li>CENTER — scrollable column of action sections (OAuth, SMS OTP, Biometrics)
 *   <li>SOUTH — response textarea + SWITCH DEMOS button
 * </ul>
 *
 * <p>Threading notes:
 * <ul>
 *   <li>{@code authenticationStateObserver} callbacks arrive on the Swing EDT (because
 *       {@code kotlinx-coroutines-swing} is on the classpath, making {@code Dispatchers.Main}
 *       the EDT). No {@code invokeLater} needed there.
 *   <li>Callback extension calls ({@code SmsOtpClientCallbacksKt}, etc.) run on
 *       {@code Dispatchers.Default} (a background pool). All UI updates inside those
 *       callbacks must be wrapped in {@code SwingUtilities.invokeLater()}.
 * </ul>
 */
public class ConsumerPanel extends JPanel {

    // A fixed keystore password is fine for a demo. In a real app you would derive or store this
    // securely — it protects the local encryption keystore, not the network credentials.
    private static final String KEYSTORE_PASSWORD = "stytch-demo-keystore";

    private enum SmsStep { PHONE, CODE }

    private final AppFrame frame;
    private final StytchConsumer stytch;

    // State
    private SmsStep smsStep = SmsStep.PHONE;
    private String methodId = null;
    private ConsumerAuthenticationState currentAuthState = new ConsumerAuthenticationState.Loading();

    // UI components that need updating at runtime
    private JLabel statusLabel;
    private JButton googleButton;
    private JButton appleButton;
    private JLabel smsInputLabel;
    private JTextField smsInputField;
    private JButton smsSubmitButton;
    private JButton biometricsButton;
    private JTextArea responseArea;

    public ConsumerPanel(AppFrame frame, AppPreferences prefs) {
        this.frame = frame;

        StytchClientConfiguration config = new StytchClientConfiguration(
                prefs.getPublicToken(),
                ConsumerPanel.class,
                "1.0.0",
                KEYSTORE_PASSWORD,
                new EndpointOptions(),
                null
        );
        stytch = StytchConsumerKt.createStytchConsumer(config);

        buildUI();
        checkBiometricsAvailability();

        // authenticationStateObserver uses Dispatchers.Main (= Swing EDT via coroutines-swing),
        // so we can update the UI directly here — no invokeLater needed.
        stytch.authenticationStateObserver(state -> {
            currentAuthState = state;
            updateStatusLabel();
            checkBiometricsAvailability();
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

        JLabel appTitle = new JLabel("Stytch Consumer Demo", SwingConstants.CENTER);
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
        content.add(Box.createVerticalStrut(10));
        content.add(buildSmsOtpSection());
        content.add(Box.createVerticalStrut(10));
        content.add(buildBiometricsSection());
        content.add(Box.createVerticalStrut(14));
        content.add(buildSwitchDemosSection());

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        return scrollPane;
    }

    private JPanel buildOAuthSection() {
        JPanel section = titledSection("OAuth");
        section.setLayout(new GridLayout(1, 2, 8, 0));

        // OAuth is not supported on JVM — buttons are present but permanently disabled.
        googleButton = new JButton("Google Login");
        googleButton.setEnabled(false);
        googleButton.setToolTipText("OAuth is not supported on JVM");

        appleButton = new JButton("Apple Login");
        appleButton.setEnabled(false);
        appleButton.setToolTipText("OAuth is not supported on JVM");

        section.add(googleButton);
        section.add(appleButton);
        return section;
    }

    private JPanel buildSmsOtpSection() {
        JPanel section = titledSection("SMS OTP");
        section.setLayout(new BorderLayout(8, 0));

        smsInputLabel = new JLabel("Phone Number");
        smsInputLabel.setPreferredSize(new Dimension(100, 24));
        section.add(smsInputLabel, BorderLayout.WEST);

        smsInputField = new JTextField();
        section.add(smsInputField, BorderLayout.CENTER);

        smsSubmitButton = new JButton("Send Code");
        smsSubmitButton.addActionListener(e -> handleSmsSubmit());
        section.add(smsSubmitButton, BorderLayout.EAST);

        smsInputField.addActionListener(e -> handleSmsSubmit());
        return section;
    }

    private JPanel buildBiometricsSection() {
        JPanel section = titledSection("Biometrics");

        // Initially set to a neutral placeholder; updated by checkBiometricsAvailability().
        biometricsButton = new JButton("Checking…");
        biometricsButton.setEnabled(false);
        biometricsButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, biometricsButton.getPreferredSize().height));

        section.setLayout(new BorderLayout());
        section.add(biometricsButton, BorderLayout.CENTER);
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
        if (currentAuthState instanceof ConsumerAuthenticationState.Loading) {
            statusLabel.setText("Loading…");
        } else if (currentAuthState instanceof ConsumerAuthenticationState.Authenticated) {
            statusLabel.setText("Welcome Back");
        } else {
            statusLabel.setText("Please Login");
        }
    }

    /**
     * Queries {@code biometrics.getAvailability()} and updates the biometrics button.
     * On JVM the result is always {@link BiometricsAvailability.Unavailable}, so the button
     * will be disabled. The call is made anyway to demonstrate the API to Java developers.
     */
    private void checkBiometricsAvailability() {
        BiometricsParameters params = new BiometricsParameters(30);
        BiometricsClientCallbacksKt.getAvailability(
                stytch.getBiometrics(),
                params,
                StytchCallbacks.onSuccess(availability -> SwingUtilities.invokeLater(() ->
                        updateBiometricsButton(availability))),
                StytchCallbacks.onFailure(error -> SwingUtilities.invokeLater(() ->
                        updateBiometricsButton(new BiometricsAvailability.Unavailable(error.getMessage(), null))))
        );
    }

    private void updateBiometricsButton(BiometricsAvailability availability) {
        for (java.awt.event.ActionListener al : biometricsButton.getActionListeners()) {
            biometricsButton.removeActionListener(al);
        }
        if (availability instanceof BiometricsAvailability.Available) {
            biometricsButton.setText("Register Biometrics");
            biometricsButton.setEnabled(true);
            biometricsButton.addActionListener(e -> handleRegisterBiometrics());
        } else if (availability instanceof BiometricsAvailability.AlreadyRegistered) {
            biometricsButton.setText("Authenticate Biometrics");
            biometricsButton.setEnabled(true);
            biometricsButton.addActionListener(e -> handleAuthenticateBiometrics());
        } else {
            biometricsButton.setText("Biometrics Unavailable");
            biometricsButton.setEnabled(false);
            biometricsButton.setToolTipText("Biometrics are not supported on JVM");
        }
    }

    private void showResponse(Object response) {
        responseArea.setText(response.toString());
        responseArea.setCaretPosition(0);
    }

    // -------------------------------------------------------------------------
    // Action handlers
    // -------------------------------------------------------------------------

    private void handleSmsSubmit() {
        String input = smsInputField.getText().trim();
        if (input.isEmpty()) return;
        smsSubmitButton.setEnabled(false);

        if (smsStep == SmsStep.PHONE) {
            OTPsSMSLoginOrCreateParameters request = new OTPsSMSLoginOrCreateParameters(input, 5);
            SmsOtpClientCallbacksKt.loginOrCreate(
                    stytch.getOtp().getSms(),
                    request,
                    StytchCallbacks.onSuccess(response -> SwingUtilities.invokeLater(() -> {
                        methodId = response.getMethodId();
                        smsStep = SmsStep.CODE;
                        smsInputLabel.setText("Enter Code");
                        smsSubmitButton.setText("Verify");
                        smsInputField.setText("");
                        smsSubmitButton.setEnabled(true);
                        showResponse(response);
                    })),
                    StytchCallbacks.onFailure(error -> SwingUtilities.invokeLater(() -> {
                        smsSubmitButton.setEnabled(true);
                        showResponse(error);
                    }))
            );
        } else {
            if (methodId == null) return;
            OTPsAuthenticateParameters request = new OTPsAuthenticateParameters(input, methodId, 5);
            OtpClientCallbacksKt.authenticate(
                    stytch.getOtp(),
                    request,
                    StytchCallbacks.onSuccess(response -> SwingUtilities.invokeLater(() -> {
                        resetSmsForm();
                        smsSubmitButton.setEnabled(true);
                        showResponse(response);
                    })),
                    StytchCallbacks.onFailure(error -> SwingUtilities.invokeLater(() -> {
                        smsSubmitButton.setEnabled(true);
                        showResponse(error);
                    }))
            );
        }
    }

    private void handleRegisterBiometrics() {
        biometricsButton.setEnabled(false);
        BiometricsParameters params = new BiometricsParameters(30);
        BiometricsClientCallbacksKt.register(
                stytch.getBiometrics(),
                params,
                StytchCallbacks.onSuccess(response -> SwingUtilities.invokeLater(() -> {
                    showResponse(response);
                    checkBiometricsAvailability();
                })),
                StytchCallbacks.onFailure(error -> SwingUtilities.invokeLater(() -> {
                    showResponse(error);
                    checkBiometricsAvailability();
                }))
        );
    }

    private void handleAuthenticateBiometrics() {
        biometricsButton.setEnabled(false);
        BiometricsParameters params = new BiometricsParameters(30);
        BiometricsClientCallbacksKt.authenticate(
                stytch.getBiometrics(),
                params,
                StytchCallbacks.onSuccess(response -> SwingUtilities.invokeLater(() -> {
                    showResponse(response);
                    checkBiometricsAvailability();
                })),
                StytchCallbacks.onFailure(error -> SwingUtilities.invokeLater(() -> {
                    showResponse(error);
                    checkBiometricsAvailability();
                }))
        );
    }

    private void handleSwitchDemos(JButton btn) {
        btn.setEnabled(false);
        if (currentAuthState instanceof ConsumerAuthenticationState.Authenticated) {
            SessionClientCallbacksKt.revoke(
                    stytch.getSession(),
                    StytchCallbacks.onSuccess(response -> SwingUtilities.invokeLater(() -> frame.onSwitchDemos())),
                    StytchCallbacks.onFailure(error   -> SwingUtilities.invokeLater(() -> frame.onSwitchDemos()))
            );
        } else {
            frame.onSwitchDemos();
        }
    }

    private void resetSmsForm() {
        smsStep = SmsStep.PHONE;
        methodId = null;
        smsInputLabel.setText("Phone Number");
        smsSubmitButton.setText("Send Code");
        smsInputField.setText("");
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
