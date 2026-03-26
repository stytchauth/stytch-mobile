package com.stytch.sdk.utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
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

    private data class NavGroup(val label: String, val entries: MutableList<Any> = mutableListOf())

    @TaskAction
    fun generate() {
        val mapFile = Json.decodeFromString<MethodMapFile>(methodMapFile.get().asFile.readText())
        val vertical = mapFile.vertical
        val out = outputDir.get().asFile
        val examples = if (examplesDir.isPresent) examplesDir.get().asFile else null

        val navGroups: MutableMap<String, MutableList<NavGroup>> =
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
            val groups = generateClient(client, vertical, slug, label, examples, out)
            for ((platform, group) in groups) {
                navGroups[platform]!!.add(group)
            }
        }

        // Write nav patch with grouped structure matching Mintlify's nav format
        val navJson =
            Json { prettyPrint = true }.encodeToString(
                buildJsonObject {
                    put(
                        vertical,
                        buildJsonObject {
                            for ((platform, groups) in navGroups) {
                                put(
                                    platform,
                                    buildJsonArray {
                                        for (group in groups) add(navGroupToJson(group))
                                    },
                                )
                            }
                        },
                    )
                },
            )
        out.resolve("nav-patch-$vertical.json").also { it.parentFile.mkdirs() }.writeText(navJson)
        logger.lifecycle("GenerateMintlifyDocsTask: generated docs for $vertical")
    }

    private fun navGroupToJson(group: NavGroup): JsonElement =
        buildJsonObject {
            put("group", group.label)
            put(
                "pages",
                buildJsonArray {
                    for (entry in group.entries) {
                        when (entry) {
                            is String -> add(entry)
                            is NavGroup -> add(navGroupToJson(entry))
                        }
                    }
                },
            )
        }

    private fun generateClient(
        client: ClientEntry,
        vertical: String,
        slug: String,
        label: String,
        examples: File?,
        out: File,
        ancestorInterfaces: Set<String> = emptySet(),
    ): Map<String, NavGroup> {
        if (client.interfaceName in ancestorInterfaces) return emptyMap()
        val ancestors = ancestorInterfaces + client.interfaceName

        val groups = PLATFORMS.associate { (platform, _, _) -> platform to NavGroup(label) }

        for (method in client.methods) {
            val pages = generateMethodPages(method, vertical, slug, examples, out)
            for ((platform, pagePath) in pages) {
                groups[platform]!!.entries.add(pagePath)
            }
        }
        for (sub in client.subClients) {
            val subSlug = "$slug/${sub.propName.toSlug()}"
            val subLabel = sub.propName.toTitle()
            val subGroups = generateClient(sub, vertical, subSlug, subLabel, examples, out, ancestors)
            for ((platform, subGroup) in subGroups) {
                groups[platform]!!.entries.add(subGroup)
            }
        }

        return groups
    }

    private fun generateMethodPages(
        method: MethodEntry,
        vertical: String,
        slug: String,
        examples: File?,
        out: File,
    ): Map<String, String> {
        val methodSlug = method.name.toSlug()
        val relPath = "$slug/$methodSlug"

        // Shared snippet
        val snippetContent = buildSnippet(method)
        val snippetRel = "snippets/api-reference/$vertical/mobile-sdks/methods/$relPath.mdx"
        out.resolve(snippetRel).also { it.parentFile.mkdirs() }.writeText(snippetContent)

        // Per-platform pages
        val pages = mutableMapOf<String, String>()
        for ((platform, platformLabel, lang) in PLATFORMS) {
            val fileExample =
                examples?.let {
                    val ext = LANG_EXT[lang] ?: lang
                    it.resolve("$slug/${method.name.toSlug()}.$ext").takeIf { f -> f.exists() }?.readText()
                }
            // Prefer any hand-written examples; fall back to the KDoc-embedded example
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
            pages[platform] = pageRel.removeSuffix(".mdx")
        }
        return pages
    }

    private fun buildSnippet(method: MethodEntry): String =
        buildString {
            if (method.description.isNotBlank()) {
                appendLine(method.description)
                appendLine()
            }
            if (method.paramFields.isNotEmpty()) {
                appendLine("## Parameters")
                appendLine()
                append(buildFields(method.paramFields, "ParamField", "body"))
                appendLine()
            }
            when {
                method.returnFields.isNotEmpty() -> {
                    appendLine("## Returns")
                    appendLine()
                    append(buildFields(method.returnFields, "ResponseField", "name"))
                }
                !method.returnDoc.isNullOrBlank() -> {
                    appendLine("## Returns")
                    appendLine()
                    append(method.returnDoc.trimIndent())
                }
            }
        }

    private fun buildFields(
        fields: List<ResponseFieldEntry>,
        element: String,
        attr: String,
        indent: Int = 0,
    ): String =
        buildString {
            val pad = "  ".repeat(indent)
            for (field in fields) {
                val requiredAttr = if (field.required) " required" else ""
                appendLine("$pad<$element $attr=\"${field.name}\" type=\"${field.type}\"$requiredAttr>")
                if (field.doc.isNotBlank()) appendLine("$pad  ${field.doc}")
                if (field.children.isNotEmpty()) {
                    appendLine("$pad  <Expandable title=\"properties\">")
                    append(buildFields(field.children, element, attr, indent + 2))
                    appendLine("$pad  </Expandable>")
                }
                appendLine("$pad</$element>")
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
        val snippetImportPath = "/snippets/api-reference/$sdk/mobile-sdks/methods/$relPath.mdx"
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
                "passkeys" to Pair("webauthn", "Passkeys & WebAuthn"),
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

        fun String.toSlug(): String =
            replace(Regex("([A-Z]+)([A-Z][a-z])"), "$1-$2")
                .replace(Regex("([a-z\\d])([A-Z])"), "$1-$2")
                .lowercase()

        fun String.toTitle(): String =
            replace(Regex("([A-Z]+)([A-Z][a-z])"), "$1 $2")
                .replace(Regex("([a-z\\d])([A-Z])"), "$1 $2")
                .replaceFirstChar { it.uppercase() }
    }
}
