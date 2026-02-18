package com.stytch.sdk.utils

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toKModifier
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import kotlin.collections.toTypedArray

val INTERNALLY_MANAGED_PARAMETERS =
    listOf(
        "sessionToken",
        "sessionJwt",
        "intermediateSessionToken",
        "dfpTelemetryId",
        "captchaToken",
        "pkceCodeVerifier",
        "pkceCodeChallenge",
        "codeChallenge",
        "codeVerifier",
    )

class StytchDtoProcessor(
    val codeGenerator: CodeGenerator,
    val logger: KSPLogger,
    val options: Map<String, String>,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("com.stytch.sdk.networking.NetworkModel")
        val unprocessedSymbols = mutableListOf<KSAnnotated>()

        symbols.filterIsInstance<KSClassDeclaration>().forEach {
            if (it.simpleName.getShortName().endsWith("Request")) {
                if (!tryProcessRequest(it)) {
                    unprocessedSymbols.add(it)
                }
            } else if (it.simpleName.getShortName().endsWith("Response")) {
                if (!tryProcessResponse(it)) {
                    unprocessedSymbols.add(it)
                }
            }
        }

        return unprocessedSymbols
    }

    private fun tryProcessRequest(requestClass: KSClassDeclaration): Boolean {
        try {
            val packageName = requestClass.qualifiedName?.getQualifier() ?: return false
            val networkModelName = requestClass.simpleName.getShortName()
            val dtoName = networkModelName.replace("Request", "Parameters")
            val interfaceName = "I$dtoName"
            logger.info("Processing ${requestClass.qualifiedName}")
            val (requiredParams, optionalParams, internalParams) = sortParameters(requestClass)
            FileSpec
                .builder(packageName, dtoName)
                .addType(
                    buildInterface(
                        interfaceName = interfaceName,
                        dtoName = dtoName,
                        requiredParams = requiredParams,
                        optionalParams = optionalParams,
                    ),
                ).addType(
                    buildImplementingClass(
                        packageName = packageName,
                        dtoName = dtoName,
                        requiredParams = requiredParams,
                        optionalParams = optionalParams,
                    ),
                ).addFunction(
                    buildDtoToNetworkModelMapper(
                        packageName = packageName,
                        interfaceName = interfaceName,
                        networkModelName = networkModelName,
                        publicParams = (requiredParams + optionalParams),
                        internalParams = internalParams,
                    ),
                ).build()
                .writeTo(codeGenerator, aggregating = true)
            return true
        } catch (e: Exception) {
            logger.exception(e)
            return false
        }
    }

    private fun tryProcessResponse(responseClass: KSClassDeclaration): Boolean {
        // placeholder for if we need/want to do any response parsing
        return true
    }

    private fun buildInterface(
        interfaceName: String,
        dtoName: String,
        requiredParams: List<ParameterSpec>,
        optionalParams: List<ParameterSpec>,
    ): TypeSpec {
        val typeSpec =
            TypeSpec
                .interfaceBuilder(interfaceName)
                .addModifiers(KModifier.PUBLIC)
                .addAnnotation(ClassName("kotlin.js", "JsExport"))
                .addAnnotation(AnnotationSpec.builder(ClassName("kotlin.js", "JsName")).addMember("\"$dtoName\"").build())
        (requiredParams + optionalParams).forEach {
            typeSpec.addProperty(PropertySpec.builder(it.name, it.type).build())
        }
        return typeSpec.build()
    }

    private fun buildImplementingClass(
        packageName: String,
        dtoName: String,
        requiredParams: List<ParameterSpec>,
        optionalParams: List<ParameterSpec>,
    ): TypeSpec {
        val typeSpec =
            TypeSpec
                .classBuilder(dtoName)
                .addModifiers(KModifier.PUBLIC)
                .primaryConstructor(
                    FunSpec
                        .constructorBuilder()
                        .addParameters(requiredParams)
                        .addParameters(optionalParams)
                        .build(),
                ).addFunctions(
                    generateSecondaryConstructors(
                        requiredParams = requiredParams,
                        optionalParams = optionalParams,
                    ),
                )
        (requiredParams + optionalParams).forEach {
            typeSpec.addProperty(
                PropertySpec
                    .builder(it.name, it.type)
                    .initializer(it.name)
                    .addModifiers(KModifier.OVERRIDE)
                    .build(),
            )
        }
        typeSpec.addSuperinterface(ClassName(packageName, "I$dtoName"))
        return typeSpec.build()
    }

    private fun sortParameters(requestClass: KSClassDeclaration): List<List<ParameterSpec>> {
        // Filter out blacklisted params and sort them into required/optional/internal
        val requiredParams = mutableListOf<ParameterSpec>()
        val optionalParams = mutableListOf<ParameterSpec>()
        val internalParams = mutableListOf<ParameterSpec>()
        requestClass
            .getAllProperties()
            .forEach { property ->
                val spec =
                    ParameterSpec
                        .builder(
                            name = property.simpleName.getShortName(),
                            type = property.type.toTypeName(),
                            modifiers = property.modifiers.mapNotNull { it.toKModifier() }.toTypedArray(),
                        )
                if (property.type.toTypeName().isNullable) {
                    spec.defaultValue("null")
                }
                if (INTERNALLY_MANAGED_PARAMETERS.contains(property.simpleName.getShortName())) {
                    internalParams.add(spec.build())
                } else {
                    if (property.type.toTypeName().isNullable) {
                        optionalParams.add(spec.build())
                    } else {
                        requiredParams.add(spec.build())
                    }
                }
            }
        return listOf(requiredParams, optionalParams, internalParams)
    }

    private fun generateSecondaryConstructors(
        requiredParams: List<ParameterSpec>,
        optionalParams: List<ParameterSpec>,
    ): List<FunSpec> {
        val secondaryConstructors = mutableListOf<FunSpec>()
        if (optionalParams.isNotEmpty()) {
            for (i in optionalParams.size - 1 downTo 0) {
                val thisIteration = optionalParams.slice(0..<i)
                val nullCount = optionalParams.size - thisIteration.size
                val nulls = List(nullCount) { "null" }.joinToString(", ")
                val givenParams = (requiredParams + thisIteration)
                val constructorCall =
                    buildString {
                        append(givenParams.joinToString(", ") { it.name })
                        if (givenParams.isNotEmpty()) {
                            append(", ")
                        }
                        append(nulls)
                    }
                val spec =
                    FunSpec
                        .constructorBuilder()
                        .addParameters(requiredParams)
                        .addParameters(thisIteration)
                        .callThisConstructor(constructorCall)
                        .build()
                secondaryConstructors.add(spec)
            }
        }
        return secondaryConstructors
    }

    fun buildDtoToNetworkModelMapper(
        packageName: String,
        interfaceName: String,
        networkModelName: String,
        publicParams: List<ParameterSpec>,
        internalParams: List<ParameterSpec>,
    ): FunSpec {
        val spec =
            FunSpec
                .builder("toNetworkModel")
                .addParameters(internalParams)
                .receiver(ClassName(packageName, interfaceName))
                .returns(ClassName(packageName, networkModelName))
                .addStatement(
                    "return $networkModelName(\n\t${(publicParams + internalParams).joinToString(
                        ",\n\t" ,
                    ) { "${it.name} = ${it.name}" }}\n)",
                )
        return spec.build()
    }
}

class StytchDtoProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        StytchDtoProcessor(environment.codeGenerator, environment.logger, environment.options)
}
