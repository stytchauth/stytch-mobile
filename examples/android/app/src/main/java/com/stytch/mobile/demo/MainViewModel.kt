package com.stytch.mobile.demo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.stytch.sdk.consumer.StytchConsumer
import com.stytch.sdk.consumer.createStytchConsumer
import com.stytch.sdk.consumer.data.ConsumerAuthenticationState
import com.stytch.sdk.consumer.networking.OtpAuthenticateRequest
import com.stytch.sdk.consumer.networking.OtpAuthenticateResponse
import com.stytch.sdk.consumer.networking.OtpSmsLoginOrCreateRequest
import com.stytch.sdk.consumer.networking.OtpSmsLoginOrCreateResponse
import com.stytch.sdk.consumer.otp.authenticate
import com.stytch.sdk.data.StytchClientConfiguration
import com.stytch.sdk.data.StytchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val stytchConsumerClient: StytchConsumer,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _state = MutableStateFlow(DemoAppState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            stytchConsumerClient.authenticationStateFlow.collect { authenticationState ->
                _state.value = _state.value.copy(authenticationState = authenticationState)
            }
        }
    }

    fun sendSms(phoneNumber: String) {
        val request =
            OtpSmsLoginOrCreateRequest(
                phoneNumber = phoneNumber,
                expirationMinutes = 5,
            )
        viewModelScope.launch {
            when (val response = stytchConsumerClient.otp.sms.loginOrCreate(request)) {
                is StytchResult.Error -> {
                    _state.emit(
                        state.value.copy(
                            methodId = null,
                            rawResponse = response,
                        ),
                    )
                }

                is StytchResult.Success<OtpSmsLoginOrCreateResponse> -> {
                    _state.emit(
                        state.value.copy(
                            methodId = response.data.methodId,
                            step = Step.SUBMIT_TOKEN,
                            rawResponse = response,
                        ),
                    )
                }
            }
        }
    }

    fun authSms(token: String) {
        val methodId = _state.value.methodId ?: return
        val request =
            OtpAuthenticateRequest(
                token = token,
                methodId = methodId,
                sessionDurationMinutes = 5,
            )

        // let's do this one with a callback, instead of the regular coroutine:
        stytchConsumerClient.otp.authenticate(request) { response ->
            when (response) {
                is StytchResult.Error -> {
                    _state.value =
                        state.value.copy(
                            rawResponse = response,
                        )
                }

                is StytchResult.Success<OtpAuthenticateResponse> -> {
                    // reset the state
                    _state.value =
                        DemoAppState(
                            authenticationState = stytchConsumerClient.authenticationStateFlow.value,
                            rawResponse = response,
                        )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            val response = stytchConsumerClient.session.revoke()
            _state.emit(
                state.value.copy(
                    rawResponse = response,
                ),
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    val application = checkNotNull(extras[APPLICATION_KEY])
                    val savedStateHandle = extras.createSavedStateHandle()
                    val stytchConsumer =
                        createStytchConsumer(
                            StytchClientConfiguration(
                                context = application.applicationContext,
                                publicToken = BuildConfig.STYTCH_PUBLIC_TOKEN,
                            ),
                        )
                    return MainViewModel(stytchConsumer, savedStateHandle) as T
                }
            }
    }
}

data class DemoAppState(
    val authenticationState: ConsumerAuthenticationState = ConsumerAuthenticationState.Loading,
    val methodId: String? = null,
    val step: Step = Step.SUBMIT_PHONE_NUMBER,
    val rawResponse: StytchResult<Any>? = null,
)

enum class Step {
    SUBMIT_PHONE_NUMBER,
    SUBMIT_TOKEN,
}
