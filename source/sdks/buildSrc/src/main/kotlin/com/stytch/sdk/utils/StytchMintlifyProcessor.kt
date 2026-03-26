package com.stytch.sdk.utils

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
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

        val clients = buildClientList(entryPoint, ancestorInterfaces = emptySet(), resolver)
        val mapFile = MethodMapFile(vertical = vertical, clients = clients)

        val outFile = File(outputDir, "method-map.json").also { it.parentFile.mkdirs() }
        outFile.writeText(JSON.encodeToString(mapFile))
        logger.info("StytchMintlifyProcessor: wrote ${outFile.absolutePath} ($vertical, ${clients.size} top-level clients)")

        return emptyList()
    }

    private fun buildClientList(
        parent: KSClassDeclaration,
        ancestorInterfaces: Set<String>,
        resolver: Resolver,
    ): List<ClientEntry> =
        parent
            .getAllProperties()
            .filter { it.simpleName.asString() !in SKIP_PROPS }
            .mapNotNull { prop ->
                val typeDec =
                    prop.type.resolve().declaration as? KSClassDeclaration
                        ?: return@mapNotNull null
                if (!typeDec.simpleName.asString().endsWith("Client")) return@mapNotNull null
                buildClientEntry(prop.simpleName.asString(), typeDec, ancestorInterfaces, resolver)
            }.toList()

    private fun buildClientEntry(
        propName: String,
        cls: KSClassDeclaration,
        ancestorInterfaces: Set<String>,
        resolver: Resolver,
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
                    .map { buildMethodEntry(it, resolver) }
                    .toList(),
            subClients =
                if (interfaceName in ancestorInterfaces) {
                    emptyList()
                } else {
                    buildClientList(cls, ancestorInterfaces + interfaceName, resolver)
                },
        )
    }

    private fun buildMethodEntry(fn: KSFunctionDeclaration, resolver: Resolver): MethodEntry {
        val parsed = parseKDoc(fn.docString?.cleanDoc() ?: "")
        val paramType = fn.parameters.firstOrNull()?.type?.resolveParamType(resolver)
        val returnType = fn.returnType?.resolve()
        return MethodEntry(
            name = fn.simpleName.asString(),
            description = parsed.description,
            kotlinExample = parsed.kotlinExample,
            iosExample = parsed.iosExample,
            rnExample = parsed.rnExample,
            returnDoc = parsed.returnDoc,
            paramType = paramType?.declaration?.simpleName?.asString(),
            paramFields =
                paramType
                    ?.let { extractResponseFields(it, depth = 0) }
                    ?.filter { it.name !in SKIP_REQUEST_PARAMS }
                    ?: emptyList(),
            returnType = returnType?.declaration?.simpleName?.asString(),
            returnFields = returnType?.let { extractResponseFields(it, depth = 0) } ?: emptyList(),
        )
    }

    /**
     * Resolves the parameter type for a method, always using the OpenAPI-generated `*Request` model
     * for `I*Parameters` types
     * Maps `IFooParameters` → `FooRequest` and looks it up directly.
     */
    private fun KSTypeReference.resolveParamType(resolver: Resolver): KSType? {
        val direct = resolve()
        val declName = direct.declaration.simpleName.asString()
        // Get the actual type name, unwrapping error type syntax if needed
        val typeName =
            if (declName.startsWith("<ERROR")) {
                declName.removePrefix("<ERROR TYPE: ").removeSuffix(">").trim().takeIf { it.isNotBlank() } ?: return null
            } else {
                declName
            }
        // I*Parameters → always use the corresponding OpenAPI *Request model for docs
        if (!typeName.startsWith("I") || !typeName.endsWith("Parameters")) return direct
        val requestName = typeName.removePrefix("I").removeSuffix("Parameters") + "Request"
        val found =
            resolver
                .getAllFiles()
                .flatMap { it.declarations }
                .filterIsInstance<KSClassDeclaration>()
                .find { it.simpleName.asString() == requestName }
        if (found == null) logger.warn("StytchMintlifyProcessor: could not find $requestName for $typeName")
        return found?.asType(emptyList())
    }

    /** Recursively extracts fields from a response type up to [depth] levels of nesting. */
    private fun extractResponseFields(type: KSType, depth: Int): List<ResponseFieldEntry> {
        if (depth > 2) return emptyList()
        val decl = type.declaration as? KSClassDeclaration ?: return emptyList()
        if (!decl.packageName.asString().startsWith("com.stytch")) return emptyList()

        return decl
            .getAllProperties()
            .filter { it.extensionReceiver == null }
            .map { prop ->
                val propType = prop.type.resolve()
                val expandType = listElementType(propType) ?: propType
                ResponseFieldEntry(
                    name = prop.simpleName.asString(),
                    type = typeDisplayName(propType),
                    required = !propType.isMarkedNullable,
                    doc = prop.docString?.cleanDoc() ?: "",
                    children = if (depth < 2) extractResponseFields(expandType, depth + 1) else emptyList(),
                )
            }.toList()
    }

    private fun typeDisplayName(type: KSType): String {
        val name = type.declaration.simpleName.asString()
        val args = type.arguments
        val base =
            if (args.isEmpty()) {
                name
            } else {
                "$name<${args.joinToString(", ") { arg -> arg.type?.resolve()?.let { typeDisplayName(it) } ?: "*" }}>"
            }
        return if (type.isMarkedNullable) "$base?" else base
    }

    /** Returns the element type if [type] is a List/Set/Collection/Array, otherwise null. */
    private fun listElementType(type: KSType): KSType? {
        val name = type.declaration.simpleName.asString()
        return if (name in setOf("List", "Set", "Collection", "Array")) {
            type.arguments.firstOrNull()?.type?.resolve()
        } else {
            null
        }
    }

    private data class ParsedDoc(
        val description: String,
        val kotlinExample: String?,
        val iosExample: String?,
        val rnExample: String?,
        val returnDoc: String?,
    )

    private fun parseKDoc(raw: String): ParsedDoc {
        if (raw.isBlank()) return ParsedDoc("", null, null, null, null)

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
        // Fields managed internally by the SDK (DFP/CAPTCHA tokens, PKCE codes, etc.) — not user-facing
        private val SKIP_REQUEST_PARAMS = INTERNALLY_MANAGED_PARAMETERS.toSet()
        private val SKIP_FUNCTIONS =
            setOf(
                "equals",
                "hashCode",
                "toString",
                "copy",
                "authenticationStateObserver",
            )
        private val JSON = Json { prettyPrint = true }

        private fun String.cleanDoc(): String =
            trim()
                .removePrefix("/**")
                .removeSuffix("*/")
                .lines()
                .joinToString("\n") { line ->
                    val t = line.trim()
                    when {
                        // Safety net for raw KDoc (shouldn't occur since KSP pre-strips these)
                        t.startsWith("* ") -> t.removePrefix("* ")
                        t == "*" -> ""
                        // KSP's docString leaves exactly 1 leading space per line; strip just that
                        else -> line.removePrefix(" ")
                    }
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
    val returnDoc: String? = null,
    val paramType: String? = null,
    val paramFields: List<ResponseFieldEntry> = emptyList(),
    val returnType: String? = null,
    val returnFields: List<ResponseFieldEntry> = emptyList(),
)

@Serializable
data class ResponseFieldEntry(
    val name: String,
    val type: String,
    val required: Boolean,
    val doc: String,
    val children: List<ResponseFieldEntry> = emptyList(),
)
