package com.stytch.mobile.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.stytch.mobile.demo.ui.theme.StytchMobileAndroidDemoTheme
import com.stytch.sdk.consumer.data.ConsumerAuthenticationState
import com.stytch.sdk.consumer.networking.AuthenticatedResponse
import com.stytch.sdk.consumer.networking.OtpSmsLoginOrCreateResponse
import com.stytch.sdk.consumer.networking.SessionsRevokeResponse
import com.stytch.sdk.data.BasicResponse
import com.stytch.sdk.data.StytchAPIResponse

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels { MainViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val state = viewModel.state.collectAsState()

            StytchMobileAndroidDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier =
                            Modifier.padding(
                                top = innerPadding.calculateTopPadding(),
                                bottom = innerPadding.calculateBottomPadding(),
                                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr) + 16.dp,
                                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr) + 16.dp,
                            ),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        when (state.value.authenticationState) {
                            is ConsumerAuthenticationState.Loading -> {
                                Text("Loading...")
                            }

                            is ConsumerAuthenticationState.Authenticated -> {
                                Text("Authenticated!")
                                Button(onClick = viewModel::logout) {
                                    Text("Logout")
                                }
                            }

                            is ConsumerAuthenticationState.Unauthenticated -> {
                                UnauthenticatedStateView(
                                    step = state.value.step,
                                    onSendSms = viewModel::sendSms,
                                    onAuthSms = viewModel::authSms,
                                )
                            }
                        }
                        state.value.rawResponse?.let { response ->
                            Spacer(modifier = Modifier.height(16.dp))
                            SelectionContainer {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = response.toFriendlyDisplay(),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UnauthenticatedStateView(
    step: Step,
    onSendSms: (String) -> Unit,
    onAuthSms: (String) -> Unit,
) {
    val inputState = rememberTextFieldState()
    var textFieldLabel by remember { mutableStateOf("Phone Number") }

    LaunchedEffect(step) {
        inputState.clearText()
        textFieldLabel =
            when (step) {
                Step.SUBMIT_PHONE_NUMBER -> "Phone Number"
                Step.SUBMIT_TOKEN -> "Code"
            }
    }

    fun handleSubmit() {
        when (step) {
            Step.SUBMIT_PHONE_NUMBER -> {
                onSendSms(inputState.text.toString())
            }

            Step.SUBMIT_TOKEN -> {
                onAuthSms(inputState.text.toString())
            }
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Testing SMS OTP...")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(0.8f),
                state = inputState,
                label = { Text(textFieldLabel) },
                lineLimits = TextFieldLineLimits.SingleLine,
            )
            IconButton(
                onClick = ::handleSubmit,
                modifier = Modifier.wrapContentWidth(align = Alignment.CenterHorizontally),
            ) {
                Icon(
                    modifier = Modifier.width(48.dp),
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = null,
                )
            }
        }
    }
}

private fun StytchAPIResponse.toFriendlyDisplay(): String {
    val response = this
    return buildString {
        append(
            when (response) {
                OtpSmsLoginOrCreateResponse -> "Code Sent\n"
                is AuthenticatedResponse -> "Logged In\n"
                SessionsRevokeResponse -> "Logged Out\n"
                else -> "Got Response\n"
            },
        )
        if (response is BasicResponse) {
            append(
                """
                status_code:
                ${response.statusCode}
                
                request_id:
                ${response.requestId}
                """.trimIndent(),
            )
        }
        if (response is OtpSmsLoginOrCreateResponse) {
            append(
                """
                method_id:
                ${response.methodId}
                """.trimIndent(),
            )
        }
        if (response is AuthenticatedResponse) {
            append(
                """
                session_token:
                ${response.sessionToken}
                
                session_jwt:
                ${response.sessionJwt}
                """.trimIndent(),
            )
        }
    }
}
