package com.stytch.sdk.utils

import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.FileSpec
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class StytchPoetTask : DefaultTask() {
    @get:InputDirectory
    abstract val inputDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Input
    abstract val packageName: Property<String>

    @TaskAction
    fun generate() {
        inputDir.asFileTree.forEach {
            if (it.nameWithoutExtension.endsWith("Request")) {
                generateRequestDto(it)
            }
            if (it.nameWithoutExtension.endsWith("Response")) {
                generateResponseDto(it)
            }
        }
    }

    private fun generateRequestDto(file: File) {
        val file =
            FileSpec
                .builder(packageName.get(), file.nameWithoutExtension.replace("Request", "Parameters"))
                .build()
        file.writeTo(outputDir.get().asFile)
    }

    private fun generateResponseDto(file: File) {
        // TODO
    }
}

class StytchDtoProcessor(
    val codeGenerator: CodeGenerator,
    val logger: KSPLogger,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("com.stytch.sdk.networking.NetworkModel")
        symbols.forEach { symbol ->
            val x =
                symbol.containingFile
                    ?.declarations
                    ?.filterIsInstance<KSClassDeclaration>()
                    ?.filter { it.simpleName.getShortName().endsWith("Request") }
                    ?.firstOrNull()
            println("JORDAN >>>> ${x?.getAllProperties()}")
        }
        return emptyList()
    }
}

class StytchDtoProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        StytchDtoProcessor(environment.codeGenerator, environment.logger)
}
