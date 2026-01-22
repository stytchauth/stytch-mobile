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
import com.stytch.sdk.consumer.networking.OtpSmsLoginOrCreateRequest
import com.stytch.sdk.consumer.otp.authenticate
import com.stytch.sdk.data.StytchAPIResponse
import com.stytch.sdk.data.StytchClientConfiguration
import com.stytch.sdk.data.StytchError
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
            try {
                val response = stytchConsumerClient.otp.sms.loginOrCreate(request)
                _state.emit(
                    state.value.copy(
                        methodId = response.methodId,
                        step = Step.SUBMIT_TOKEN,
                        rawResponse = response,
                        error = null,
                    ),
                )
            } catch (e: StytchError) {
                _state.emit(
                    state.value.copy(
                        methodId = null,
                        error = e,
                    ),
                )
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
        try {
            // let's do this one with a callback, instead of the regular coroutine:
            stytchConsumerClient.otp.authenticate(request) { response ->
                // reset the state
                _state.value =
                    DemoAppState(
                        authenticationState = stytchConsumerClient.authenticationStateFlow.value,
                        rawResponse = response,
                        error = null,
                    )
            }
        } catch (e: StytchError) {
            _state.value = _state.value.copy(error = e)
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                val response = stytchConsumerClient.session.revoke()
                _state.emit(
                    _state.value.copy(
                        rawResponse = response,
                        error = null,
                    ),
                )
            } catch (e: StytchError) {
                _state.emit(_state.value.copy(error = e))
            }
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
    val authenticationState: ConsumerAuthenticationState = ConsumerAuthenticationState.Loading(),
    val methodId: String? = null,
    val step: Step = Step.SUBMIT_PHONE_NUMBER,
    val rawResponse: StytchAPIResponse? = null,
    val error: StytchError? = null,
)

enum class Step {
    SUBMIT_PHONE_NUMBER,
    SUBMIT_TOKEN,
}
