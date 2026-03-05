package com.stytch.sdk.dfp

import com.stytch.sdk.data.DFPConfiguration
import com.stytch.sdk.data.DFPProtectedAuthMode
import com.stytch.sdk.data.StytchAPIError
import de.jensklingenberg.ktorfit.annotationsAttributeKey
import io.ktor.client.call.HttpClientCall
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import io.ktor.utils.io.InternalAPI
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class, InternalAPI::class)
internal class AddPropertiesTest {
    private val jsonBody = TextContent("""{"existing":"value"}""", ContentType.Application.Json)
    private val nonJsonBody = TextContent("plain text", ContentType.Text.Plain)

    @Test
    fun `adds properties to a JSON body`() {
        val result = jsonBody.addProperties(mapOf("new_key" to "new_value"))
        val json = Json.parseToJsonElement(result.text).jsonObject
        assertEquals("value", json["existing"]!!.jsonPrimitive.content)
        assertEquals("new_value", json["new_key"]!!.jsonPrimitive.content)
    }

    @Test
    fun `does not modify non-JSON body`() {
        val result = nonJsonBody.addProperties(mapOf("key" to "value"))
        assertEquals(nonJsonBody, result)
    }

    @Test
    fun `serializes null values as JSON null`() {
        val result = jsonBody.addProperties(mapOf("nullable_key" to null))
        val json = Json.parseToJsonElement(result.text).jsonObject
        assertNull(json["nullable_key"]!!.jsonPrimitive.contentOrNull)
    }

    @Test
    fun `empty properties map returns identical JSON`() {
        val result = jsonBody.addProperties(emptyMap())
        val json = Json.parseToJsonElement(result.text).jsonObject
        assertEquals("value", json["existing"]!!.jsonPrimitive.content)
        assertEquals(1, json.size)
    }
}

@OptIn(ExperimentalCoroutinesApi::class, InternalAPI::class)
internal class SetCAPTCHATokenTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val jsonBody = TextContent("""{"key":"value"}""", ContentType.Application.Json)

    @Test
    fun `does not add token when captchaProvider is null`() =
        runTest(dispatcher) {
            val result = jsonBody.setCAPTCHAToken(null)
            val json = Json.parseToJsonElement(result.text).jsonObject
            assert("captcha_token" !in json)
        }

    @Test
    fun `does not add token when captchaProvider is not configured`() =
        runTest(dispatcher) {
            val provider = mockk<CAPTCHAProvider> { every { isConfigured } returns false }
            val result = jsonBody.setCAPTCHAToken(provider)
            val json = Json.parseToJsonElement(result.text).jsonObject
            assert("captcha_token" !in json)
        }

    @Test
    fun `adds captcha token when provider is configured`() =
        runTest(dispatcher) {
            val provider =
                mockk<CAPTCHAProvider> {
                    every { isConfigured } returns true
                    coEvery { getCAPTCHAToken() } returns "tok_abc"
                }
            val result = jsonBody.setCAPTCHAToken(provider)
            val json = Json.parseToJsonElement(result.text).jsonObject
            assertEquals("tok_abc", json["captcha_token"]!!.jsonPrimitive.content)
        }
}

@OptIn(ExperimentalCoroutinesApi::class, InternalAPI::class)
internal class SetTelemetryIDTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val jsonBody = TextContent("""{"key":"value"}""", ContentType.Application.Json)

    @Test
    fun `sets dfp_telemetry_id to null when provider is null`() =
        runTest(dispatcher) {
            val result = jsonBody.setTelemetryID(null)
            val json = Json.parseToJsonElement(result.text).jsonObject
            assertNull(json["dfp_telemetry_id"]!!.jsonPrimitive.contentOrNull)
        }

    @Test
    fun `sets dfp_telemetry_id from provider`() =
        runTest(dispatcher) {
            val provider = mockk<DFPProvider> { coEvery { getTelemetryId() } returns "tel_xyz" }
            val result = jsonBody.setTelemetryID(provider)
            val json = Json.parseToJsonElement(result.text).jsonObject
            assertEquals("tel_xyz", json["dfp_telemetry_id"]!!.jsonPrimitive.content)
        }
}

// --- handlePotentialDFPPARequest ---

private fun apiError(errorType: String) =
    StytchAPIError(
        statusCode = 403,
        requestId = "req-1",
        errorMessage = "error",
        errorType = errorType,
        errorUrl = "https://stytch.com/docs/errors/403",
    )

private fun mockCall(response: HttpResponse = mockk(relaxed = true)): HttpClientCall =
    mockk<HttpClientCall>(relaxed = true) { every { this@mockk.response } returns response }

@OptIn(ExperimentalCoroutinesApi::class, InternalAPI::class)
internal class HandlePotentialDFPPARequestTest {
    private val dispatcher = UnconfinedTestDispatcher()

    private fun config(
        enabled: Boolean = false,
        mode: DFPProtectedAuthMode = DFPProtectedAuthMode.OBSERVATION,
        dfpProvider: DFPProvider? = null,
        captchaProvider: CAPTCHAProvider? = null,
    ) = DFPPAInterceptorConfiguration().apply {
        getDfpConfiguration = { DFPConfiguration(enabled, mode) }
        this.dfpProvider = dfpProvider
        this.captchaProvider = captchaProvider
    }

    private fun jsonRequest(withDfppaAnnotation: Boolean = true): HttpRequestBuilder =
        HttpRequestBuilder().apply {
            body = TextContent("""{"original":"true"}""", ContentType.Application.Json)
            if (withDfppaAnnotation) {
                attributes.put(annotationsAttributeKey, listOf(DFPPAEnabled()))
            }
        }

    // non-DFPPA endpoint
    @Test
    fun `passes through immediately when request has no DFPPA annotation`() =
        runTest(dispatcher) {
            val request = jsonRequest(withDfppaAnnotation = false)
            val expectedCall = mockCall()
            var capturedRequest: HttpRequestBuilder? = null

            val result =
                handlePotentialDFPPARequest(
                    request = request,
                    configuration = config(),
                    proceed = {
                        capturedRequest = it
                        expectedCall
                    },
                    parseResponseAsError = { error("should not be called") },
                )

            assertEquals(expectedCall, result)
            assertEquals(request, capturedRequest)
        }

    // non-TextContent body
    @Test
    fun `passes through when body is not TextContent`() =
        runTest(dispatcher) {
            val request =
                HttpRequestBuilder().apply {
                    body = "not text content"
                    attributes.put(annotationsAttributeKey, listOf(DFPPAEnabled()))
                }
            val expectedCall = mockCall()
            val result =
                handlePotentialDFPPARequest(
                    request = request,
                    configuration = config(),
                    proceed = { expectedCall },
                    parseResponseAsError = { error("should not be called") },
                )
            assertEquals(expectedCall, result)
        }

    // DFP disabled, no CAPTCHA
    @Test
    fun `DFP disabled without CAPTCHA provider sends request body unchanged`() =
        runTest(dispatcher) {
            var capturedBody: String? = null
            handlePotentialDFPPARequest(
                request = jsonRequest(),
                configuration = config(enabled = false, captchaProvider = null),
                proceed = {
                    capturedBody = (it.body as TextContent).text
                    mockCall()
                },
                parseResponseAsError = { error("should not be called") },
            )
            val json = Json.parseToJsonElement(capturedBody!!).jsonObject
            assert("captcha_token" !in json)
            assert("dfp_telemetry_id" !in json)
        }

    // DFP disabled, CAPTCHA configured
    @Test
    fun `DFP disabled with configured CAPTCHA provider adds captcha token`() =
        runTest(dispatcher) {
            val captchaProvider =
                mockk<CAPTCHAProvider> {
                    every { isConfigured } returns true
                    coEvery { getCAPTCHAToken() } returns "captcha_tok"
                }
            var capturedBody: String? = null
            handlePotentialDFPPARequest(
                request = jsonRequest(),
                configuration = config(enabled = false, captchaProvider = captchaProvider),
                proceed = {
                    capturedBody = (it.body as TextContent).text
                    mockCall()
                },
                parseResponseAsError = { error("should not be called") },
            )
            val json = Json.parseToJsonElement(capturedBody!!).jsonObject
            assertEquals("captcha_tok", json["captcha_token"]!!.jsonPrimitive.content)
            assert("dfp_telemetry_id" !in json)
        }

    // DFP enabled, OBSERVATION mode
    @Test
    fun `OBSERVATION mode adds telemetry ID and captcha token`() =
        runTest(dispatcher) {
            val dfpProvider = mockk<DFPProvider> { coEvery { getTelemetryId() } returns "tel_obs" }
            val captchaProvider =
                mockk<CAPTCHAProvider> {
                    every { isConfigured } returns true
                    coEvery { getCAPTCHAToken() } returns "cap_obs"
                }
            var capturedBody: String? = null
            handlePotentialDFPPARequest(
                request = jsonRequest(),
                configuration =
                    config(
                        enabled = true,
                        mode = DFPProtectedAuthMode.OBSERVATION,
                        dfpProvider = dfpProvider,
                        captchaProvider = captchaProvider,
                    ),
                proceed = {
                    capturedBody = (it.body as TextContent).text
                    mockCall()
                },
                parseResponseAsError = { error("should not be called") },
            )
            val json = Json.parseToJsonElement(capturedBody!!).jsonObject
            assertEquals("tel_obs", json["dfp_telemetry_id"]!!.jsonPrimitive.content)
            assertEquals("cap_obs", json["captcha_token"]!!.jsonPrimitive.content)
        }

    // DFP enabled, DECISIONING mode, no retry needed
    @Test
    fun `DECISIONING mode adds telemetry ID and does not retry when no captcha_required`() =
        runTest(dispatcher) {
            val dfpProvider = mockk<DFPProvider> { coEvery { getTelemetryId() } returns "tel_dec" }
            val proceedBodies = mutableListOf<String>()
            handlePotentialDFPPARequest(
                request = jsonRequest(),
                configuration =
                    config(
                        enabled = true,
                        mode = DFPProtectedAuthMode.DECISIONING,
                        dfpProvider = dfpProvider,
                    ),
                proceed = {
                    proceedBodies.add((it.body as TextContent).text)
                    mockCall()
                },
                parseResponseAsError = { apiError("some_other_error") },
            )
            assertEquals(1, proceedBodies.size)
            val json = Json.parseToJsonElement(proceedBodies[0]).jsonObject
            assertEquals("tel_dec", json["dfp_telemetry_id"]!!.jsonPrimitive.content)
            assert("captcha_token" !in json)
        }

    // DFP enabled, DECISIONING mode, captcha_required triggers retry
    @Test
    fun `DECISIONING mode retries with telemetry ID and captcha token on captcha_required`() =
        runTest(dispatcher) {
            val dfpProvider = mockk<DFPProvider> { coEvery { getTelemetryId() } returns "tel_ret" }
            val captchaProvider =
                mockk<CAPTCHAProvider> {
                    every { isConfigured } returns true
                    coEvery { getCAPTCHAToken() } returns "cap_ret"
                }
            val proceedBodies = mutableListOf<String>()
            var parseCallCount = 0

            handlePotentialDFPPARequest(
                request = jsonRequest(),
                configuration =
                    config(
                        enabled = true,
                        mode = DFPProtectedAuthMode.DECISIONING,
                        dfpProvider = dfpProvider,
                        captchaProvider = captchaProvider,
                    ),
                proceed = {
                    proceedBodies.add((it.body as TextContent).text)
                    mockCall()
                },
                parseResponseAsError = {
                    parseCallCount++
                    apiError("captcha_required")
                },
            )

            assertEquals(2, proceedBodies.size)
            assertEquals(1, parseCallCount)

            // first attempt: telemetry ID only
            val firstJson = Json.parseToJsonElement(proceedBodies[0]).jsonObject
            assertEquals("tel_ret", firstJson["dfp_telemetry_id"]!!.jsonPrimitive.content)
            assert("captcha_token" !in firstJson)

            // retry: both telemetry ID and captcha token
            val retryJson = Json.parseToJsonElement(proceedBodies[1]).jsonObject
            assertEquals("tel_ret", retryJson["dfp_telemetry_id"]!!.jsonPrimitive.content)
            assertEquals("cap_ret", retryJson["captcha_token"]!!.jsonPrimitive.content)
        }
}
