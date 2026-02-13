package com.stytch.sdk.utils

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toKModifier
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import kotlin.collections.toTypedArray

val BLACKLISTED_REQUEST_PARAMETERS =
    listOf(
        "sessionToken",
        "sessionJwt",
        "intermediateSessionToken",
        "dfpTelemetryId",
        "captchaToken",
        "pkceCodeVerifier",
        "pkceCodeChallenge",
    )

class StytchDtoProcessor(
    val codeGenerator: CodeGenerator,
    val logger: KSPLogger,
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
            val fileName = requestClass.simpleName.getShortName().replace("Request", "Parameters")
            val classSpec = createClass(requestClass)
            val dtoMapperSpec = createDtoMapper(requestClass, classSpec)
            FileSpec
                .builder(packageName, fileName)
                .addType(classSpec)
                // .addFunction(dtoMapperSpec)
                .build()
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

    private fun createClass(requestClass: KSClassDeclaration): TypeSpec {
        val originalClassName = requestClass.simpleName.getShortName()
        val newClassName = originalClassName.replace("Request", "Parameters")
        val (requiredParams, optionalParams) = sortParameters(requestClass)
        return TypeSpec
            .classBuilder(newClassName)
            .addModifiers(KModifier.PUBLIC)
            .addAnnotation(kotlin.js.ExperimentalJsExport::class)
            .primaryConstructor(
                FunSpec
                    .constructorBuilder()
                    .addParameters(requiredParams)
                    .addParameters(optionalParams)
                    .build(),
            ).addFunctions(generateSecondaryConstructors(requiredParams, optionalParams))
            // .addFunction(generateNetworkModelMapper(requestClass))
            .build()
    }

    private fun sortParameters(requestClass: KSClassDeclaration): List<List<ParameterSpec>> {
        // Filter out blacklisted params and sort them into required/optional
        val requiredParams = mutableListOf<ParameterSpec>()
        val optionalParams = mutableListOf<ParameterSpec>()
        requestClass
            .getAllProperties()
            .filter { property -> !BLACKLISTED_REQUEST_PARAMETERS.contains(property.simpleName.getShortName()) }
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
                    optionalParams.add(spec.build())
                } else {
                    requiredParams.add(spec.build())
                }
            }
        return listOf(requiredParams, optionalParams)
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

    fun generateNetworkModelMapper(networkModelClass: KSClassDeclaration): FunSpec? = null

    fun createDtoMapper(
        networkModelClass: KSClassDeclaration,
        dtoClass: TypeSpec,
    ): FunSpec? = null
}

class StytchDtoProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        StytchDtoProcessor(environment.codeGenerator, environment.logger)
}
