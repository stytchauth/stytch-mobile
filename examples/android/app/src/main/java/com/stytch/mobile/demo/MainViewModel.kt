package com.stytch.mobile.demo

import androidx.activity.ComponentActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.stytch.sdk.biometrics.BiometricsParameters
import com.stytch.sdk.consumer.StytchConsumer
import com.stytch.sdk.consumer.createStytchConsumer
import com.stytch.sdk.consumer.data.ConsumerAuthenticationState
import com.stytch.sdk.consumer.networking.models.OTPsAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.OTPsSMSLoginOrCreateParameters
import com.stytch.sdk.data.StytchAPIResponse
import com.stytch.sdk.data.StytchClientConfiguration
import com.stytch.sdk.data.StytchError
import com.stytch.sdk.oauth.OAuthProviderType
import com.stytch.sdk.oauth.OAuthStartParameters
import com.stytch.sdk.passkeys.PasskeysParameters
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
            OTPsSMSLoginOrCreateParameters(
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
            OTPsAuthenticateParameters(
                token = token,
                methodId = methodId,
                sessionDurationMinutes = 5,
            )
        viewModelScope.launch {
            try {
                // let's do this one with a callback, instead of the regular coroutine:
                val response = stytchConsumerClient.otp.authenticate(request)
                _state.value =
                    DemoAppState(
                        authenticationState = stytchConsumerClient.authenticationStateFlow.value,
                        rawResponse = response,
                        error = null,
                    )
            } catch (e: StytchError) {
                _state.value = _state.value.copy(error = e)
            }
        }
    }

    fun startOAuth(
        activity: ComponentActivity,
        provider: OAuthProviderType,
    ) {
        val request =
            OAuthStartParameters(
                activity = activity,
                loginRedirectUrl = "my-login-redirect-url",
                signupRedirectUrl = "my-signup-redirect-url",
            )
        viewModelScope.launch {
            try {
                val response =
                    if (provider == OAuthProviderType.GOOGLE) {
                        stytchConsumerClient.oauth.google.start(request)
                    } else {
                        stytchConsumerClient.oauth.apple.start(request)
                    } as StytchAPIResponse
                _state.emit(state.value.copy(rawResponse = response))
            } catch (e: StytchError) {
                _state.value = _state.value.copy(error = e)
            }
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

    fun registerBiometrics(context: FragmentActivity) {
        viewModelScope.launch {
            val request = BiometricsParameters(context = context, sessionDurationMinutes = 30)
            stytchConsumerClient.biometrics.register(request)
        }
    }

    fun deleteBiometrics() {
        viewModelScope.launch {
            stytchConsumerClient.biometrics.removeRegistration()
        }
    }

    fun authenticateBiometrics(context: FragmentActivity) {
        viewModelScope.launch {
            val request = BiometricsParameters(context = context, sessionDurationMinutes = 30)
            stytchConsumerClient.biometrics.authenticate(request)
        }
    }

    fun registerPasskey(context: FragmentActivity) {
        viewModelScope.launch {
            val request = PasskeysParameters(activity = context, domain = "stytch.com")
            stytchConsumerClient.passkeys.register(request)
        }
    }

    fun authenticatePasskey(context: FragmentActivity) {
        viewModelScope.launch {
            val request = PasskeysParameters(activity = context, domain = "stytch.com")
            stytchConsumerClient.passkeys.authenticate(request)
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
