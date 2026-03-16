package com.stytch.sdk.migrations

import kotlinx.serialization.Serializable

@Serializable
public data class MigrationRecord(
    val id: Int,
    val appliedAt: Long,
    val status: MigrationStatus,
)

@Serializable
public enum class MigrationStatus {
    SUCCESS,
    SKIPPED,
    FAILED,
}
