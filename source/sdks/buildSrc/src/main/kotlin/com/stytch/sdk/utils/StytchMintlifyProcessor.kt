package com.stytch.sdk.utils

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

class StytchMintlifyProcessor(
    private val logger: KSPLogger,
    private val options: Map<String, String>,
    private val isCommonMain: Boolean,
) : SymbolProcessor {
    private var finished = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (!isCommonMain || finished) return emptyList()
        finished = true

        val outputDir =
            options["stytchMintlifyOutputDir"] ?: run {
                logger.warn("stytchMintlifyOutputDir not set, skipping Mintlify doc generation")
                return emptyList()
            }

        val stytchApiClasses =
            resolver
                .getSymbolsWithAnnotation("com.stytch.sdk.StytchApi")
                .filterIsInstance<KSClassDeclaration>()

        // b2b or consumer
        val (entryPoint, vertical) =
            ENTRY_POINTS
                .firstNotNullOfOrNull { (className, path) ->
                    stytchApiClasses.find { it.simpleName.asString() == className }?.let { it to path }
                } ?: run {
                logger.warn("StytchMintlifyProcessor: no SDK entry point found, skipping")
                return emptyList()
            }

        val clients = buildClientList(entryPoint, ancestorInterfaces = emptySet())
        val mapFile = MethodMapFile(vertical = vertical, clients = clients)

        val outFile = File(outputDir, "method-map.json").also { it.parentFile.mkdirs() }
        outFile.writeText(JSON.encodeToString(mapFile))
        logger.info("StytchMintlifyProcessor: wrote ${outFile.absolutePath} ($vertical, ${clients.size} top-level clients)")

        return emptyList()
    }

    private fun buildClientList(
        parent: KSClassDeclaration,
        ancestorInterfaces: Set<String>,
    ): List<ClientEntry> =
        parent
            .getAllProperties()
            .filter { it.simpleName.asString() !in SKIP_PROPS }
            .mapNotNull { prop ->
                val typeDec =
                    prop.type.resolve().declaration as? KSClassDeclaration
                        ?: return@mapNotNull null
                if (!typeDec.simpleName.asString().endsWith("Client")) return@mapNotNull null
                buildClientEntry(prop.simpleName.asString(), typeDec, ancestorInterfaces)
            }.toList()

    private fun buildClientEntry(
        propName: String,
        cls: KSClassDeclaration,
        ancestorInterfaces: Set<String>,
    ): ClientEntry {
        val interfaceName = cls.simpleName.asString()
        return ClientEntry(
            propName = propName,
            interfaceName = interfaceName,
            doc = cls.docString?.cleanDoc() ?: "",
            methods =
                cls
                    .getAllFunctions()
                    .filter { fn -> fn.simpleName.asString() !in SKIP_FUNCTIONS }
                    .map { buildMethodEntry(it) }
                    .toList(),
            subClients =
                if (interfaceName in ancestorInterfaces) {
                    emptyList()
                } else {
                    buildClientList(cls, ancestorInterfaces + interfaceName)
                },
        )
    }

    private fun buildMethodEntry(fn: KSFunctionDeclaration): MethodEntry {
        val parsed = parseKDoc(fn.docString?.cleanDoc() ?: "")
        return MethodEntry(
            name = fn.simpleName.asString(),
            description = parsed.description,
            kotlinExample = parsed.kotlinExample,
            iosExample = parsed.iosExample,
            rnExample = parsed.rnExample,
            paramDoc = parsed.paramDoc,
            returnDoc = parsed.returnDoc,
            paramType =
                fn.parameters
                    .firstOrNull()
                    ?.type
                    ?.resolve()
                    ?.declaration
                    ?.simpleName
                    ?.asString(),
            returnType =
                fn.returnType
                    ?.resolve()
                    ?.declaration
                    ?.simpleName
                    ?.asString(),
        )
    }

    private data class ParsedDoc(
        val description: String,
        val kotlinExample: String?,
        val iosExample: String?,
        val rnExample: String?,
        val paramDoc: String?,
        val returnDoc: String?,
    )

    private fun parseKDoc(raw: String): ParsedDoc {
        if (raw.isBlank()) return ParsedDoc("", null, null, null, null, null)

        fun extractCodeBlock(lang: String): String? =
            Regex("```${lang}\\s*\\n(.*?)```", RegexOption.DOT_MATCHES_ALL)
                .find(raw)
                ?.groupValues
                ?.get(1)
                ?.trimEnd()

        fun extractTag(tag: String): String? =
            Regex("\n@${tag}\\s+(.*?)(?=\n@|$)", RegexOption.DOT_MATCHES_ALL)
                .find(raw)
                ?.groupValues
                ?.get(1)
                ?.trim()
                ?.takeIf { it.isNotEmpty() }

        val description =
            raw
                .substringBefore("\n@")
                .trim()
                .replace(Regex("```(?:kotlin|swift|js).*?```", RegexOption.DOT_MATCHES_ALL), "")
                .replace(Regex("\\*{1,2}(?:Kotlin(?: \\(Android\\))?|iOS|React Native):\\*{0,2}"), "")
                .lines()
                .joinToString(" ") { it.trim() }
                .replace(Regex("\\s{2,}"), " ")
                .trim()

        return ParsedDoc(
            description = description,
            kotlinExample = extractCodeBlock("kotlin"),
            iosExample = extractCodeBlock("swift"),
            rnExample = extractCodeBlock("js"),
            paramDoc = extractTag("param"),
            returnDoc = extractTag("return"),
        )
    }

    companion object {
        // map of top-level client name to vertical (which is the path differentiator in the docs site)
        private val ENTRY_POINTS =
            listOf(
                "StytchConsumer" to "consumer",
                "StytchB2B" to "b2b",
            )
        private val SKIP_PROPS = setOf("authenticationStateFlow")
        private val SKIP_FUNCTIONS =
            setOf(
                "equals",
                "hashCode",
                "toString",
                "copy",
                "authenticationStateObserver",
            )
        private val JSON = Json { prettyPrint = true }

        // clean up any *s in the docs
        private fun String.cleanDoc(): String =
            trim()
                .removePrefix("/**")
                .removeSuffix("*/")
                .lines()
                .joinToString("\n") { line ->
                    val t = line.trim()
                    if (t.startsWith("* ") || t == "*") t.removePrefix("*").trim() else t
                }.trim()
    }
}

class StytchMintlifyProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        StytchMintlifyProcessor(
            logger = environment.logger,
            options = environment.options,
            isCommonMain = environment.platforms.size > 1,
        )
}

@Serializable
data class MethodMapFile(
    val vertical: String,
    val clients: List<ClientEntry>,
)

@Serializable
data class ClientEntry(
    val propName: String,
    val interfaceName: String,
    val doc: String,
    val methods: List<MethodEntry>,
    val subClients: List<ClientEntry>,
)

@Serializable
data class MethodEntry(
    val name: String,
    val description: String,
    val kotlinExample: String? = null,
    val iosExample: String? = null,
    val rnExample: String? = null,
    val paramDoc: String? = null,
    val returnDoc: String? = null,
    val paramType: String? = null,
    val returnType: String? = null,
)
