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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.stytch.sdk.consumer.networking.OtpAuthenticateResponse
import com.stytch.sdk.consumer.networking.OtpSmsLoginOrCreateResponse
import com.stytch.sdk.data.StytchResult

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels { MainViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val state = viewModel.state.collectAsState()
            val inputState = rememberTextFieldState()
            var textFieldLabel by remember { mutableStateOf("Phone Number") }

            LaunchedEffect(state.value.step) {
                inputState.clearText()
                textFieldLabel =
                    when (state.value.step) {
                        Step.SUBMIT_PHONE_NUMBER -> "Phone Number"
                        Step.SUBMIT_TOKEN -> "Code"
                        Step.AUTHENTICATED -> ""
                    }
            }

            fun handleSubmit() {
                when (state.value.step) {
                    Step.SUBMIT_PHONE_NUMBER -> {
                        viewModel.sendSms(inputState.text.toString())
                    }

                    Step.SUBMIT_TOKEN -> {
                        viewModel.authSms(inputState.text.toString())
                    }

                    Step.AUTHENTICATED -> {}
                }
            }

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
                        if (state.value.step != Step.AUTHENTICATED) {
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

private fun <T> StytchResult<T>.toFriendlyDisplay(): String =
    when (this) {
        is StytchResult.Success<T> -> {
            when (val content = data) {
                is OtpSmsLoginOrCreateResponse -> {
                    """
                    Code Sent!

                    request_id:
                    ${content.requestId}

                    status_code:
                    ${content.statusCode}

                    method_id:
                    ${content.methodId}
                    """.trimIndent()
                }

                is OtpAuthenticateResponse -> {
                    """
                    Logged In!

                    request_id:
                    ${content.requestId}

                    status_code:
                    ${content.statusCode}

                    session_token:
                    ${content.sessionToken}
                    """.trimIndent()
                }

                else -> {
                    "This will never be displayed!"
                }
            }
        }

        is StytchResult.Error -> {
            buildString {
                append("Error!\n")
                append(exception.localizedMessage ?: "Unknown error")
            }
        }
    }
