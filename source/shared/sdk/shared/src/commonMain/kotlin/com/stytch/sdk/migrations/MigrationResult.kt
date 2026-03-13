package com.stytch.sdk.migrations

public sealed class MigrationResult {
    public data object Success : MigrationResult()

    public data class Skipped(
        val reason: String,
    ) : MigrationResult()

    /** Non-fatal — logged and recorded, but does not block SDK initialization. */
    public data class Failed(
        val error: Throwable,
    ) : MigrationResult()
}
