package com.stytch.mobile.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.stytch.mobile.demo.ui.theme.StytchMobileAndroidDemoTheme
import com.stytch.sdk.consumer.StytchConsumer
import com.stytch.sdk.consumer.createStytchConsumer
import com.stytch.sdk.consumer.networking.Requests
import com.stytch.sdk.data.StytchClientConfiguration

class MainActivity : ComponentActivity() {
    val stytchConsumer: StytchConsumer by lazy {
        createStytchConsumer(StytchClientConfiguration(applicationContext, BuildConfig.STYTCH_PUBLIC_TOKEN))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LaunchedEffect(Unit) {
                val response = stytchConsumer.otp.sms.loginOrCreate(Requests.OTP.SMS.LoginOrCreate("+14434189653", expirationMinutes = 5))
                println(response)
            }
            StytchMobileAndroidDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StytchMobileAndroidDemoTheme {
        Greeting("Android")
    }
}