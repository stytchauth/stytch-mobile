package com.stytch.mobile.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stytch.mobile.demo.ui.theme.StytchMobileAndroidDemoTheme
import com.stytch.sdk.biometrics.BiometricsAvailability
import com.stytch.sdk.consumer.data.ConsumerAuthenticationState
import com.stytch.sdk.oauth.OAuthProviderType

class MainActivity : FragmentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val state by viewModel.state.collectAsState()

            StytchMobileAndroidDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier =
                            Modifier
                                .padding(
                                    top = innerPadding.calculateTopPadding(),
                                    bottom = innerPadding.calculateBottomPadding(),
                                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr) + 16.dp,
                                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr) + 16.dp,
                                ).fillMaxSize(),
                    ) {
                        when (state.screen) {
                            is AppScreen.Selector -> {
                                SelectorScreen(
                                    onSelect = viewModel::selectDemoType,
                                )
                            }

                            is AppScreen.TokenEntry -> {
                                TokenEntryScreen(
                                    demoType = (state.screen as AppScreen.TokenEntry).demoType,
                                    onSubmit = viewModel::submitToken,
                                )
                            }

                            is AppScreen.Consumer -> {
                                ConsumerScreen(
                                    state = state,
                                    onSendSms = viewModel::sendSms,
                                    onAuthSms = viewModel::authSms,
                                    onStartOAuth = viewModel::startOAuth,
                                    onBiometricsAction = viewModel::biometricsAction,
                                    onRefreshBiometrics = viewModel::refreshBiometrics,
                                    onSwitchDemos = viewModel::switchDemos,
                                )
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SelectorScreen(onSelect: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Stytch Demo",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Select a demo to get started",
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onSelect("CONSUMER") },
        ) {
            Text("Consumer")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onSelect("B2B") },
        ) {
            Text("B2B")
        }
    }
}

@Composable
fun TokenEntryScreen(
    demoType: String,
    onSubmit: (publicToken: String, googleClientId: String?, orgId: String?) -> Unit,
) {
    var publicToken by remember { mutableStateOf("") }
    var googleClientId by remember { mutableStateOf("") }
    var orgId by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Configure SDK",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = publicToken,
            onValueChange = { publicToken = it },
            label = { Text("Public Token") },
            singleLine = true,
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (demoType == "CONSUMER") {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = googleClientId,
                onValueChange = { googleClientId = it },
                label = { Text("Google Client ID (optional)") },
                singleLine = true,
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        if (demoType == "B2B") {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = orgId,
                onValueChange = { orgId = it },
                label = { Text("Organization ID (optional)") },
                singleLine = true,
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (publicToken.isNotBlank()) {
                    onSubmit(
                        publicToken.trim(),
                        googleClientId.trim().ifBlank { null },
                        orgId.trim().ifBlank { null },
                    )
                }
            },
        ) {
            Text("Continue")
        }
    }
}

@Composable
fun ConsumerScreen(
    state: DemoAppState,
    onSendSms: (String) -> Unit,
    onAuthSms: (String) -> Unit,
    onStartOAuth: (ComponentActivity, OAuthProviderType) -> Unit,
    onBiometricsAction: (FragmentActivity) -> Unit,
    onRefreshBiometrics: (FragmentActivity) -> Unit,
    onSwitchDemos: () -> Unit,
) {
    val activity = LocalActivity.current as FragmentActivity

    LaunchedEffect(state.authenticationState) {
        onRefreshBiometrics(activity)
    }

    val statusText =
        when (state.authenticationState) {
            is ConsumerAuthenticationState.Loading -> "Loading..."
            is ConsumerAuthenticationState.Authenticated -> "Welcome Back"
            is ConsumerAuthenticationState.Unauthenticated -> "Please Login"
            is ConsumerAuthenticationState.Error -> "Error"
        }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Stytch Consumer Demo",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Text(
            text = statusText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
        ) {
            // OAuth
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onStartOAuth(activity, OAuthProviderType.GOOGLE) },
            ) {
                Text("Google Login")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onStartOAuth(activity, OAuthProviderType.APPLE) },
            ) {
                Text("Apple Login")
            }
            Spacer(modifier = Modifier.height(16.dp))

            // SMS OTP
            SmsOtpForm(
                step = state.smsStep,
                onSendSms = onSendSms,
                onAuthSms = onAuthSms,
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Biometrics
            BiometricsButton(
                availability = state.biometricsAvailability,
                onClick = { onBiometricsAction(activity) },
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Switch Demos
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onSwitchDemos,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                    ),
            ) {
                Text("SWITCH DEMOS")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Response area
        state.lastResponse?.let { response ->
            Text(
                text = "Last response:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
            )
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    text = response,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun SmsOtpForm(
    step: SmsStep,
    onSendSms: (String) -> Unit,
    onAuthSms: (String) -> Unit,
) {
    val inputState = rememberTextFieldState()
    val label = if (step == SmsStep.PHONE) "Phone Number" else "Code"
    val buttonText = if (step == SmsStep.PHONE) "Send Code" else "Verify"

    LaunchedEffect(step) {
        inputState.clearText()
    }

    fun handleSubmit() {
        val text = inputState.text.toString()
        if (text.isBlank()) return
        if (step == SmsStep.PHONE) onSendSms(text) else onAuthSms(text)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            state = inputState,
            label = { Text(label) },
            lineLimits = TextFieldLineLimits.SingleLine,
            keyboardOptions = KeyboardOptions(keyboardType = if (step == SmsStep.PHONE) KeyboardType.Phone else KeyboardType.Number),
        )
        Button(onClick = ::handleSubmit) {
            Text(buttonText)
        }
    }
}

@Composable
fun BiometricsButton(
    availability: BiometricsAvailability?,
    onClick: () -> Unit,
) {
    val (label, enabled) =
        when (availability) {
            BiometricsAvailability.Available -> "Register Biometrics" to true
            BiometricsAvailability.AlreadyRegistered -> "Authenticate Biometrics" to true
            null -> "Checking Biometrics..." to false
            else -> "Biometrics Unavailable" to false
        }
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        enabled = enabled,
    ) {
        Text(label)
    }
}
/*
@Composable
fun B2BScreen(
    b2bAuthenticationState: B2BAuthenticationState,
    lastResponse: String?,
    onStartB2BOAuth: (ComponentActivity) -> Unit,
    onSwitchDemos: () -> Unit,
) {
    val activity = LocalActivity.current as ComponentActivity

    val statusText =
        when (b2bAuthenticationState) {
            is B2BAuthenticationState.Loading -> "Loading..."
            is B2BAuthenticationState.Authenticated -> "Welcome Back"
            is B2BAuthenticationState.Unauthenticated -> "Please Login"
        }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Stytch B2B Demo",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Text(
            text = statusText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onStartB2BOAuth(activity) },
            ) {
                Text("Google Login")
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onSwitchDemos,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                    ),
            ) {
                Text("SWITCH DEMOS")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        lastResponse?.let { response ->
            Text(
                text = "Last response:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
            )
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    text = response,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
*/
