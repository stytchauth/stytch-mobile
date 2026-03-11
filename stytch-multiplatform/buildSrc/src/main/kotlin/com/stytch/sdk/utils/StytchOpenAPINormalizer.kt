package com.stytch.sdk.utils

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import org.openapitools.codegen.OpenAPINormalizer

/**
 * OpenAPI Generators built in filters only filter on "path starts with X", whereas we want (for consumer endpoints) to
 * filter out paths that DON'T contain X. So, enter this custom normalizer where we just add a custom filter that will
 * check if we want a "negative match" and do that, otherwise fallback to existing behavior. So B2B will use the
 * "standard" filter, and consumer will use this one. It lives in buildSrc because it needs to be on the buildpath
 * for the gradle plugin to access it
 */
class StytchOpenAPINormalizer(
    openAPI: OpenAPI,
    inputRules: MutableMap<String, String>,
) : OpenAPINormalizer(openAPI, inputRules) {
    override fun createFilter(
        openApi: OpenAPI,
        filters: String,
    ): Filter = NegatingSupportingFilter(filters)

    private class NegatingSupportingFilter(
        val filters: String,
    ) : Filter(filters) {
        override fun hasCustomFilterMatch(
            path: String,
            operation: Operation,
        ): Boolean {
            val (_, filter) = filters.split("path:")
            if (filter.startsWith("!")) {
                val pathWeDontWant = filter.removePrefix("!")
                return !path.contains(pathWeDontWant)
            } else {
                return path.contains(filter)
            }
        }
    }
}
