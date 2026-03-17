package com.stytch.mobile.demo

import android.app.Application
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.core.content.edit
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.b2b.StytchB2B
import com.stytch.sdk.b2b.createStytchB2B
import com.stytch.sdk.b2b.data.B2BAuthenticationState
import com.stytch.sdk.biometrics.BiometricsAvailability
import com.stytch.sdk.biometrics.BiometricsParameters
import com.stytch.sdk.consumer.StytchConsumer
import com.stytch.sdk.consumer.createStytchConsumer
import com.stytch.sdk.consumer.data.ConsumerAuthenticationState
import com.stytch.sdk.consumer.networking.models.OTPsAuthenticateParameters
import com.stytch.sdk.consumer.networking.models.OTPsSMSLoginOrCreateParameters
import com.stytch.sdk.data.GoogleCredentialConfiguration
import com.stytch.sdk.data.StytchClientConfiguration
import com.stytch.sdk.data.StytchError
import com.stytch.sdk.oauth.B2BOAuthDiscoveryStartParameters
import com.stytch.sdk.oauth.B2BOAuthStartParameters
import com.stytch.sdk.oauth.OAuthProviderType
import com.stytch.sdk.oauth.OAuthStartParameters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val PREFS_NAME = "demo_app_prefs"
private const val KEY_DEMO_TYPE = "DEMO_APP_TYPE"
private const val KEY_PUBLIC_TOKEN = "STYTCH_PUBLIC_TOKEN"
private const val KEY_GOOGLE_CLIENT_ID = "GOOGLE_CLIENT_ID"
private const val KEY_ORG_ID = "STYTCH_ORG_ID"

sealed class AppScreen {
    object Selector : AppScreen()

    data class TokenEntry(
        val demoType: String,
    ) : AppScreen()

    object Consumer : AppScreen()

    object B2B : AppScreen()
}

enum class SmsStep { PHONE, CODE }

data class DemoAppState(
    val screen: AppScreen = AppScreen.Selector,
    val authenticationState: ConsumerAuthenticationState = ConsumerAuthenticationState.Loading(),
    val b2bAuthenticationState: B2BAuthenticationState = B2BAuthenticationState.Loading(),
    val smsStep: SmsStep = SmsStep.PHONE,
    val methodId: String? = null,
    val biometricsAvailability: BiometricsAvailability? = null,
    val lastResponse: String? = null,
)

class MainViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private var stytchConsumer: StytchConsumer? = null
    private var stytchB2B: StytchB2B? = null
    private var b2bOrgId: String? = null

    private val _state = MutableStateFlow(DemoAppState())
    val state = _state.asStateFlow()

    init {
        val demoType = prefs.getString(KEY_DEMO_TYPE, null)
        if (demoType != null) {
            val token = prefs.getString(KEY_PUBLIC_TOKEN, null)
            if (token != null) {
                if (demoType == "CONSUMER") {
                    initConsumerClient(token)
                    _state.value = _state.value.copy(screen = AppScreen.Consumer)
                } else {
                    initB2BClient(token)
                    _state.value = _state.value.copy(screen = AppScreen.B2B)
                }
            } else {
                _state.value = _state.value.copy(screen = AppScreen.TokenEntry(demoType))
            }
        }
        // default state already has screen = Selector
    }

    private fun initB2BClient(publicToken: String) {
        b2bOrgId = prefs.getString(KEY_ORG_ID, null)
        val config =
            StytchClientConfiguration(
                context = getApplication<Application>().applicationContext,
                publicToken = publicToken,
            )
        stytchB2B = createStytchB2B(config)
        viewModelScope.launch {
            stytchB2B!!.authenticationStateFlow.collect { authState ->
                _state.value = _state.value.copy(b2bAuthenticationState = authState)
            }
        }
    }

    private fun initConsumerClient(publicToken: String) {
        val googleClientId = prefs.getString(KEY_GOOGLE_CLIENT_ID, null)
        val config =
            StytchClientConfiguration(
                context = getApplication<Application>().applicationContext,
                publicToken = publicToken,
                googleCredentialConfiguration =
                    googleClientId?.let {
                        GoogleCredentialConfiguration(googleClientId = it)
                    },
            )
        stytchConsumer = createStytchConsumer(config)
        viewModelScope.launch {
            stytchConsumer!!.authenticationStateFlow.collect { authState ->
                _state.value = _state.value.copy(authenticationState = authState)
            }
        }
    }

    fun selectDemoType(demoType: String) {
        prefs.edit { putString(KEY_DEMO_TYPE, demoType) }
        _state.value = _state.value.copy(screen = AppScreen.TokenEntry(demoType))
    }

    fun submitToken(
        publicToken: String,
        googleClientId: String?,
        orgId: String?,
    ) {
        prefs.edit { putString(KEY_PUBLIC_TOKEN, publicToken) }
        if (!googleClientId.isNullOrBlank()) {
            prefs.edit { putString(KEY_GOOGLE_CLIENT_ID, googleClientId) }
        }
        if (!orgId.isNullOrBlank()) {
            prefs.edit { putString(KEY_ORG_ID, orgId) }
        }
        val demoType = prefs.getString(KEY_DEMO_TYPE, "CONSUMER")
        if (demoType == "CONSUMER") {
            initConsumerClient(publicToken)
            _state.value = _state.value.copy(screen = AppScreen.Consumer)
        } else {
            initB2BClient(publicToken)
            _state.value = _state.value.copy(screen = AppScreen.B2B)
        }
    }

    fun switchDemos() {
        viewModelScope.launch {
            try {
                if (_state.value.authenticationState is ConsumerAuthenticationState.Authenticated) {
                    stytchConsumer?.session?.revoke()
                } else if (_state.value.b2bAuthenticationState is B2BAuthenticationState.Authenticated) {
                    stytchB2B?.session?.revoke()
                }
            } catch (_: Exception) {
            }
            prefs.edit {
                remove(KEY_PUBLIC_TOKEN)
                    .remove(KEY_GOOGLE_CLIENT_ID)
                    .remove(KEY_ORG_ID)
                    .remove(KEY_DEMO_TYPE)
            }
            stytchConsumer = null
            stytchB2B = null
            b2bOrgId = null
            _state.value = DemoAppState(screen = AppScreen.Selector)
        }
    }

    fun refreshBiometrics(context: FragmentActivity) {
        val consumer = stytchConsumer ?: return
        viewModelScope.launch {
            val availability =
                try {
                    consumer.biometrics.getAvailability(
                        BiometricsParameters(context = context, sessionDurationMinutes = 30),
                    )
                } catch (_: Exception) {
                    BiometricsAvailability.Unavailable(reason = null)
                }
            _state.value = _state.value.copy(biometricsAvailability = availability)
        }
    }

    fun biometricsAction(context: FragmentActivity) {
        val consumer = stytchConsumer ?: return
        viewModelScope.launch {
            val params = BiometricsParameters(context = context, sessionDurationMinutes = 30)
            try {
                val response =
                    when (_state.value.biometricsAvailability) {
                        BiometricsAvailability.Available -> consumer.biometrics.register(params)
                        BiometricsAvailability.AlreadyRegistered -> consumer.biometrics.authenticate(params)
                        else -> return@launch
                    }
                _state.value = _state.value.copy(lastResponse = response.toString())
            } catch (e: StytchError) {
                _state.value = _state.value.copy(lastResponse = e.toString())
            }
            refreshBiometrics(context)
        }
    }

    fun sendSms(phoneNumber: String) {
        val consumer = stytchConsumer ?: return
        viewModelScope.launch {
            try {
                val response =
                    consumer.otp.sms.loginOrCreate(
                        OTPsSMSLoginOrCreateParameters(phoneNumber = phoneNumber, expirationMinutes = 5),
                    )
                _state.value =
                    _state.value.copy(
                        methodId = response.methodId,
                        smsStep = SmsStep.CODE,
                        lastResponse = response.toString(),
                    )
            } catch (e: StytchError) {
                _state.value = _state.value.copy(lastResponse = e.toString())
            }
        }
    }

    fun authSms(token: String) {
        val consumer = stytchConsumer ?: return
        val methodId = _state.value.methodId ?: return
        viewModelScope.launch {
            try {
                val response =
                    consumer.otp.authenticate(
                        OTPsAuthenticateParameters(token = token, methodId = methodId, sessionDurationMinutes = 5),
                    )
                _state.value =
                    _state.value.copy(
                        methodId = null,
                        smsStep = SmsStep.PHONE,
                        lastResponse = response.toString(),
                    )
            } catch (e: StytchError) {
                _state.value =
                    _state.value.copy(
                        methodId = null,
                        smsStep = SmsStep.PHONE,
                        lastResponse = e.toString(),
                    )
            }
        }
    }

    fun startB2BOAuth(activity: ComponentActivity) {
        val b2b = stytchB2B ?: return
        viewModelScope.launch {
            try {
                val response =
                    if (!b2bOrgId.isNullOrBlank()) {
                        b2b.oauth.google.start(
                            B2BOAuthStartParameters(
                                activity = activity,
                                loginRedirectUrl = "my-login-redirect-url",
                                signupRedirectUrl = "my-signup-redirect-url",
                                organizationId = b2bOrgId,
                            ),
                        )
                    } else {
                        b2b.oauth.google.discovery.start(
                            B2BOAuthDiscoveryStartParameters(
                                activity = activity,
                                discoveryRedirectUrl = "my-discovery-redirect-url",
                            ),
                        )
                    }
                _state.value = _state.value.copy(lastResponse = response.toString())
            } catch (e: StytchError) {
                _state.value = _state.value.copy(lastResponse = e.toString())
            }
        }
    }

    fun startOAuth(
        activity: ComponentActivity,
        provider: OAuthProviderType,
    ) {
        val consumer = stytchConsumer ?: return
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
                        consumer.oauth.google.start(request)
                    } else {
                        consumer.oauth.apple.start(request)
                    }
                _state.value = _state.value.copy(lastResponse = response.toString())
            } catch (e: StytchError) {
                _state.value = _state.value.copy(lastResponse = e.toString())
            }
        }
    }
}
