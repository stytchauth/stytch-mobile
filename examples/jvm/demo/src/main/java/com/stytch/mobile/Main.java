package com.stytch.mobile;

import com.stytch.sdk.consumer.StytchConsumer;
import com.stytch.sdk.consumer.data.ConsumerAuthenticationState;
import com.stytch.sdk.consumer.networking.*;
import com.stytch.sdk.data.EndpointOptions;
import com.stytch.sdk.data.StytchClientConfiguration;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.Consumer;

import static com.stytch.sdk.consumer.StytchConsumerKt.createStytchConsumer;

public class Main {
    public static void main(String[] args) {
        try (InputStream input = new FileInputStream("local.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            SwingUtilities.invokeLater(() -> {
                DemoApp app = new DemoApp(prop.getProperty("STYTCH_PUBLIC_TOKEN"));
                app.setVisible(true);
            });
        } catch (IOException ex) {
            System.out.println("Create a local.properties file in the root and add your STYTCH_PUBLIC_TOKEN");
        }
    }
}

class DemoApp extends JFrame {
    private final StytchConsumer stytch;
    private String methodId;

    public DemoApp(String STYTCH_PUBLIC_TOKEN) {
        System.out.println("Using public token: " + STYTCH_PUBLIC_TOKEN);
        StytchClientConfiguration configuration = new StytchClientConfiguration(
                STYTCH_PUBLIC_TOKEN,
                Main.class,
                "0.0.1",
                new EndpointOptions(),
                30
        );
        stytch = createStytchConsumer(configuration);
        stytch.authenticationStateObserver((state) -> {
            SwingUtilities.invokeLater(() -> {
                if (state instanceof ConsumerAuthenticationState.Loading) {
                    displayLoadingState();
                } else if (state instanceof ConsumerAuthenticationState.Authenticated) {
                    displayAuthenticatedState();
                } else {
                    displayUnauthenticatedState();
                }
                repaint();
            });
            return null;
        });
        setTitle("Stytch Demo App - in JAVA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(800, 600);
    }

    private void displayLoadingState() {
        System.out.println("Loading...");
    }

    private void displayAuthenticatedState() {
        removeAll();
        setLayout(new BorderLayout());
        JLabel inputLabel = new JLabel("Authenticated!");
        JButton submitButton = new JButton("Logout");

        submitButton.addActionListener(e -> {
            stytch.getSession().revoke(new Continuation<SessionsRevokeResponse>() {
                @Override
                public @NotNull CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NotNull Object o) {
                    System.out.println(o);
                }
            });
        });

        JPanel upperPanel = new JPanel();
        upperPanel.add(inputLabel);
        upperPanel.add(submitButton);
        add(upperPanel, BorderLayout.NORTH);
        pack();
        repaint();
    }

    private void displayUnauthenticatedState() {
        setLayout(new BorderLayout());
        Consumer<String> sendSms = (phoneNumber) -> {
            OtpSmsLoginOrCreateRequest request = new OtpSmsLoginOrCreateRequest(phoneNumber, 5, false);
            stytch.getOtp().getSms().loginOrCreate(request, new Continuation<OtpSmsLoginOrCreateResponse>() {
                @Override
                public @NotNull CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NotNull Object obj) {
                    if (obj instanceof OtpSmsLoginOrCreateResponse response) {
                        methodId = response.getMethodId();
                    } else {
                        // it's a failure
                    }
                }
            });
        };
        Form phoneForm = new Form("Phone Number", sendSms);
        Consumer<String> authSms = (token) -> {
            OtpAuthenticateRequest request = new OtpAuthenticateRequest(token, methodId, 39);
            stytch.getOtp().authenticate(request, new Continuation<OtpAuthenticateResponse>() {
                @Override
                public @NotNull CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NotNull Object obj) {
                    if (obj instanceof OtpAuthenticateResponse) {
                        methodId = null;
                    } else {
                        // it's a failure
                    }
                }
            });
        };
        Form codeForm = new Form("SMS Code", authSms);
        JPanel upperPanel = new JPanel();
        JPanel lowerPanel = new JPanel();
        upperPanel.add(phoneForm);
        lowerPanel.add(codeForm);
        add(upperPanel, BorderLayout.NORTH);
        add(lowerPanel, BorderLayout.SOUTH);
        pack();
        repaint();
    }
}

class Form extends JPanel {
    private final JTextField inputTextField;

    public Form(String label, Consumer<String> callback) {
        setLayout(new BorderLayout());

        JLabel inputLabel = new JLabel(label);
        inputTextField = new JTextField(15);
        JButton submitButton = new JButton("Submit");

        submitButton.addActionListener(e -> {
            String inputText = inputTextField.getText();
            callback.accept(inputText);
        });

        JPanel upperPanel = new JPanel();
        upperPanel.add(inputLabel);
        upperPanel.add(inputTextField);
        upperPanel.add(submitButton);

        add(upperPanel, BorderLayout.NORTH);
    }
}
