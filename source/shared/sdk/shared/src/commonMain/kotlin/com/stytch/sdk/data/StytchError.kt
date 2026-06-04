package com.stytch.sdk.data

public open class StytchError : Exception() {
    public open val additionalErrorDetails: List<Pair<String, String>> = emptyList()

    public val richErrorDescription: String
        get() {
            val errorName = this::class.simpleName ?: "Stytch Error"
            return buildString {
                appendLine("┌──────────────────────────────────────────────────────────────────────────────────┐")
                appendLine("│ ${errorName.padEnd(80, ' ')} │")
                appendLine("├──────────────────────────────────────────────────────────────────────────────────┤")
                appendLine("│ Message: $message")
                appendLine("│ Cause: $cause")
                appendLine("├──────────────────────────────────────────────────────────────────────────────────┤")
                additionalErrorDetails.forEach { (key, value) -> appendLine("│ $key: $value") }
                append("└──────────────────────────────────────────────────────────────────────────────────┘")
            }
        }
}
