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
import com.stytch.sdk.consumer.networking.Requests
import com.stytch.sdk.consumer.networking.Responses
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

    fun sendSms(phoneNumber: String) {
        val request =
            Requests.OTP.SMS.LoginOrCreate(
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

                is StytchResult.Success<Responses.OTP.SMS.LoginOrCreateResponse> -> {
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
            Requests.OTP.Authenticate(
                token = token,
                methodId = methodId,
                sessionDurationMinutes = 30,
            )
        viewModelScope.launch {
            when (val response = stytchConsumerClient.otp.authenticate(request)) {
                is StytchResult.Error -> {
                    _state.emit(
                        state.value.copy(
                            rawResponse = response,
                        ),
                    )
                }

                is StytchResult.Success<Responses.OTP.AuthenticateResponse> -> {
                    _state.emit(
                        state.value.copy(
                            step = Step.AUTHENTICATED,
                            rawResponse = response,
                        ),
                    )
                }
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
    val methodId: String? = null,
    val step: Step = Step.SUBMIT_PHONE_NUMBER,
    val rawResponse: StytchResult<Any>? = null,
)

enum class Step {
    SUBMIT_PHONE_NUMBER,
    SUBMIT_TOKEN,
    AUTHENTICATED,
}
