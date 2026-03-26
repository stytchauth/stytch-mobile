package com.stytch.sdk.utils

import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class GenerateMintlifyDocsTask : DefaultTask() {
    @get:InputFile
    abstract val methodMapFile: RegularFileProperty

    @get:InputDirectory @get:Optional
    abstract val examplesDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val mapFile = Json.decodeFromString<MethodMapFile>(methodMapFile.get().asFile.readText())
        val vertical = mapFile.vertical
        val out = outputDir.get().asFile
        val examples = if (examplesDir.isPresent) examplesDir.get().asFile else null

        val navPages: MutableMap<String, MutableList<String>> =
            mutableMapOf(
                "react-native" to mutableListOf(),
                "android" to mutableListOf(),
                "ios" to mutableListOf(),
            )

        val productMap = if (vertical == "b2b") B2B_PRODUCTS else CONSUMER_PRODUCTS

        for (client in mapFile.clients) {
            val (slug, label) =
                productMap[client.propName]
                    ?: run {
                        logger.warn("No product mapping for ${client.propName}, skipping")
                        continue
                    }
            generateClient(client, vertical, slug, label, examples, out, navPages)
        }

        // Write nav patch
        val navJson =
            buildString {
                appendLine("{")
                appendLine("  \"$vertical\": {")
                navPages.entries.forEachIndexed { platformIdx, (platform, pages) ->
                    appendLine("    \"$platform\": [")
                    pages.forEachIndexed { i, page ->
                        val comma = if (i < pages.lastIndex) "," else ""
                        appendLine("      \"$page\"$comma")
                    }
                    val comma = if (platformIdx < navPages.size - 1) "," else ""
                    appendLine("    ]$comma")
                }
                appendLine("  }")
                append("}")
            }
        out.resolve("nav-patch-$vertical.json").also { it.parentFile.mkdirs() }.writeText(navJson)
        logger.lifecycle("GenerateMintlifyDocsTask: generated docs for $vertical")
    }

    private fun generateClient(
        client: ClientEntry,
        vertical: String,
        slug: String,
        label: String,
        examples: File?,
        out: File,
        navPages: MutableMap<String, MutableList<String>>,
        ancestorInterfaces: Set<String> = emptySet(),
    ) {
        if (client.interfaceName in ancestorInterfaces) return
        val ancestors = ancestorInterfaces + client.interfaceName

        for (method in client.methods) {
            generateMethodPages(method, vertical, slug, examples, out, navPages)
        }
        for (sub in client.subClients) {
            val subSlug = "$slug/${sub.propName.toSlug()}"
            val subLabel = "$label / ${sub.propName.toTitle()}"
            generateClient(sub, vertical, subSlug, subLabel, examples, out, navPages, ancestors)
        }
    }

    private fun generateMethodPages(
        method: MethodEntry,
        vertical: String,
        slug: String,
        examples: File?,
        out: File,
        navPages: MutableMap<String, MutableList<String>>,
    ) {
        val methodSlug = method.name.toSlug()
        val relPath = "$slug/$methodSlug"

        // Shared snippet
        val snippetContent = buildSnippet(method)
        val snippetRel = "snippets/api-reference/$vertical/mobile-sdks/methods/$relPath.mdx"
        out.resolve(snippetRel).also { it.parentFile.mkdirs() }.writeText(snippetContent)

        // Per-platform pages
        for ((platform, platformLabel, lang) in PLATFORMS) {
            val fileExample =
                examples?.let {
                    val ext = LANG_EXT[lang] ?: lang
                    it.resolve("$slug/${method.name.toSlug()}.$ext").takeIf { f -> f.exists() }?.readText()
                }
            // Prefer any hand-written examples (which admittedly don't exist yet!); fall back to the KDoc-embedded example
            val example =
                fileExample ?: when (platform) {
                    "react-native" -> method.rnExample
                    "android" -> method.kotlinExample
                    "ios" -> method.iosExample
                    else -> null
                }
            val pageContent = buildPlatformPage(method, vertical, relPath, platformLabel, lang, example)
            val pageRel = "api-reference/$vertical/mobile-sdks/$platform/methods/$relPath.mdx"
            out.resolve(pageRel).also { it.parentFile.mkdirs() }.writeText(pageContent)
            navPages[platform]!!.add(pageRel.removeSuffix(".mdx"))
        }
    }

    private fun buildSnippet(method: MethodEntry): String =
        buildString {
            if (method.description.isNotBlank()) {
                appendLine(method.description)
                appendLine()
            }
            if (!method.paramDoc.isNullOrBlank()) {
                appendLine("## Parameters")
                appendLine()
                append(buildParamFields(method.paramDoc))
                appendLine()
            }
            if (!method.returnDoc.isNullOrBlank()) {
                appendLine("## Returns")
                appendLine()
                append(method.returnDoc.trimIndent())
            }
        }

    /**
     * Converts @param doc text into Mintlify <ParamField> components.
     *
     * Input format:
     *   request - [TypeName]
     *   - `fieldName` — description
     *   - `optionalField?` — description
     *   - *(Android only)* `platformField` — description
     */
    private fun buildParamFields(paramDoc: String): String {
        // Pattern: `- [*(qualifier)*] `fieldName[?]` — description`
        val fieldPattern =
            Regex(
                """^-\s+(?:\*\(([^)]+)\)\*\s+)?`(\w+)(\?)?`\s*[—-]\s*(.+)$""",
            )
        return paramDoc
            .lines()
            .drop(1) // skip "request - [TypeName]" header line
            .filter { it.isNotBlank() }
            .joinToString("\n") { line ->
                val m = fieldPattern.find(line.trim())
                if (m != null) {
                    val qualifier = m.groupValues[1] // e.g. "Android only"
                    val name = m.groupValues[2]
                    val optional = m.groupValues[3] == "?"
                    val desc = m.groupValues[4].trim()
                    val qualifierPrefix = if (qualifier.isNotEmpty()) "*($qualifier)* " else ""
                    val requiredAttr = if (optional) "" else " required"
                    "<ParamField body=\"$name\"$requiredAttr>\n  $qualifierPrefix$desc\n</ParamField>"
                } else {
                    line // pass through any lines that don't match (shouldn't happen)
                }
            }
    }

    private fun buildPlatformPage(
        method: MethodEntry,
        sdk: String,
        relPath: String,
        platformLabel: String,
        lang: String,
        example: String?,
    ): String {
        val title = method.name.toTitle()
        val desc =
            method.description
                .split('.')
                .first()
                .replace("\"", "'")
                .trim()
        val snippetImportPath = "/snippets/api-reference/$sdk/mobile-sdks/methods/$relPath"
        val codeBlock =
            if (!example.isNullOrBlank()) {
                "```$lang\n${example.trim()}\n```"
            } else {
                "```$lang\n// TODO: add $platformLabel example\n```"
            }
        return """---
title: $title
description: "$desc"
public: true
---

import MethodDoc from "$snippetImportPath";

<MethodDoc />

<RequestExample>
$codeBlock
</RequestExample>
"""
    }

    companion object {
        // Maps property name to (url-slug, display-label)
        val CONSUMER_PRODUCTS =
            mapOf(
                "otp" to Pair("otps", "OTPs"),
                "session" to Pair("sessions", "Sessions"),
                "crypto" to Pair("crypto-wallets", "Crypto Wallets"),
                "magicLinks" to Pair("email-magic-links", "Email Magic Links"),
                "totp" to Pair("totps", "TOTPs"),
                "passwords" to Pair("passwords", "Passwords"),
                "user" to Pair("users", "Users"),
                "passkeys" to Pair("webauthn", "Passkeys"),
                "biometrics" to Pair("biometrics", "Biometrics"),
                "oauth" to Pair("oauth", "OAuth"),
                "dfp" to Pair("device-fingerprinting", "Device Fingerprinting"),
            )

        val B2B_PRODUCTS =
            mapOf(
                "session" to Pair("sessions", "Sessions"),
                "magicLinks" to Pair("email-magic-links", "Email Magic Links"),
                "otp" to Pair("otps", "OTPs"),
                "passwords" to Pair("passwords", "Passwords"),
                "totp" to Pair("totps", "TOTPs"),
                "discovery" to Pair("discovery", "Discovery"),
                "members" to Pair("members", "Members"),
                "organizations" to Pair("organizations", "Organizations"),
                "recoveryCodes" to Pair("recovery-codes", "Recovery Codes"),
                "scim" to Pair("scim", "SCIM"),
                "oauth" to Pair("oauth", "OAuth"),
                "sso" to Pair("sso", "SSO"),
                "rbac" to Pair("rbac", "RBAC"),
                "dfp" to Pair("device-fingerprinting", "Device Fingerprinting"),
            )

        val PLATFORMS =
            listOf(
                Triple("react-native", "React Native", "js"),
                Triple("android", "Android", "kotlin"),
                Triple("ios", "iOS", "swift"),
            )

        val LANG_EXT = mapOf("js" to "ts", "kotlin" to "kt", "swift" to "swift")

        fun String.toSlug(): String {
            val s = replace(Regex("([A-Z]+)([A-Z][a-z])"), "$1-$2")
            return replace(Regex("([a-z\\d])([A-Z])"), "$1-$2").lowercase()
        }

        fun String.toTitle(): String {
            val s = replace(Regex("([A-Z]+)([A-Z][a-z])"), "$1 $2")
            return replace(Regex("([a-z\\d])([A-Z])"), "$1 $2")
                .replaceFirstChar { it.uppercase() }
        }
    }
}
