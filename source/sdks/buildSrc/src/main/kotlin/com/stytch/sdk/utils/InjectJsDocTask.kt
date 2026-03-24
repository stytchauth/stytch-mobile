package com.stytch.sdk.utils

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Reads a KSP-generated JsDoc map (JSON) and injects JSDoc comments into a KMP-generated
 * TypeScript declarations file (.d.mts).
 *
 * KMP silently drops KDocs from JS output. This task restores them by:
 *   1. Reading the flat JSON map produced by StytchJsDocExtractor (keys like "TypeName",
 *      "TypeName.member", "OuterType.InnerType")
 *   2. Walking the .d.mts line by line with a brace-depth context stack
 *   3. Injecting JSDoc comments before matching declarations
 */
abstract class InjectJsDocTask : DefaultTask() {
    @get:InputFile
    abstract val jsDocMapFile: RegularFileProperty

    // In-place modification — not declared as @OutputFile to avoid Gradle self-dependency.
    @get:InputFile
    abstract val dtsFile: RegularFileProperty

    @TaskAction
    fun inject() {
        val mapFile = jsDocMapFile.get().asFile
        val dts = dtsFile.get().asFile

        if (!mapFile.exists()) {
            logger.warn("InjectJsDocTask: jsdoc-map.json not found at ${mapFile.absolutePath}, skipping")
            return
        }
        if (!dts.exists()) {
            logger.warn("InjectJsDocTask: .d.mts not found at ${dts.absolutePath}, skipping")
            return
        }

        val docs = parseDocMap(mapFile.readText())
        val original = dts.readText()
        val injected = injectDocs(original, docs)
        dts.writeText(injected)

        val added = injected.lines().count { it.trimStart().startsWith("/**") } -
            original.lines().count { it.trimStart().startsWith("/**") }
        logger.lifecycle("InjectJsDocTask: injected $added JSDoc comments into ${dts.name}")
    }
}

// ── Doc map parsing ───────────────────────────────────────────────────────────

// Format: one "Key\tValue" per line, with newlines in the value escaped as \n.
private fun parseDocMap(text: String): Map<String, String> =
    text.lines()
        .filter { it.contains('\t') }
        .associate { line ->
            val tab = line.indexOf('\t')
            line.substring(0, tab) to line.substring(tab + 1).replace("\\n", "\n")
        }

// ── TypeScript injection ──────────────────────────────────────────────────────

private data class Frame(val kind: Kind, val name: String, val depth: Int)

private enum class Kind { DECLARE, NAMESPACE, INNER_CLASS, SKIP }

private val TOP_DECL_RE = Regex("""^export declare (?:interface|(?:abstract )?class) (\w+)""")
private val TOP_NS_RE = Regex("""^export declare namespace (\w+)""")
private val TOP_FN_RE = Regex("""^export declare (?:const |function )(\w+)""")
private val INNER_CLASS_RE = Regex("""^(?:abstract )?class (\w+)""")
private val INNER_NS_RE = Regex("""^namespace (\w+)""")

// Matches: "readonly foo:", "get foo():", "foo(" — captures the member name
private val MEMBER_RE = Regex("""^(?:readonly (\w+)|get (\w+)\(\)|(\w+)\s*\()""")

private val SKIP_MEMBERS = setOf("__doNotUseOrImplementIt", "constructor")

private fun injectDocs(content: String, docs: Map<String, String>): String {
    val lines = content.lines()
    val result = mutableListOf<String>()
    val stack = ArrayDeque<Frame>()
    var depth = 0

    for ((lineIdx, line) in lines.withIndex()) {
        val trimmed = line.trimStart()
        val indent = line.length - trimmed.length
        val indentStr = " ".repeat(indent)

        val opens = trimmed.count { it == '{' }
        val closes = trimmed.count { it == '}' }
        val newDepth = depth + opens - closes

        // Pop frames that have been closed by this line's `}` characters
        while (stack.isNotEmpty() && stack.last().depth > newDepth) {
            stack.removeLast()
        }

        val currentFrame = stack.lastOrNull()
        val currentKind = currentFrame?.kind
        val currentName = currentFrame?.name ?: ""

        // Determine what JSDoc to inject (if any) before this line
        val docKey: String? = when {
            currentKind == Kind.SKIP -> null

            // Inside `export declare interface/class Foo { ... }`
            currentKind == Kind.DECLARE ->
                MEMBER_RE.find(trimmed)
                    ?.groupValues?.drop(1)?.firstOrNull { it.isNotEmpty() }
                    ?.takeIf { it !in SKIP_MEMBERS }
                    ?.let { "$currentName.$it" }

            // Inside `export declare namespace Foo { ... }`: nested type declarations
            currentKind == Kind.NAMESPACE ->
                INNER_CLASS_RE.find(trimmed)?.groupValues?.get(1)?.let { inner ->
                    "$currentName.$inner".takeIf { it in docs } ?: inner.takeIf { it in docs }
                }

            // Inside a nested `class Bar { ... }` within a namespace
            currentKind == Kind.INNER_CLASS ->
                MEMBER_RE.find(trimmed)
                    ?.groupValues?.drop(1)?.firstOrNull { it.isNotEmpty() }
                    ?.takeIf { it !in SKIP_MEMBERS }
                    ?.let { "$currentName.$it" }

            // Top level: `export declare interface/class/function`
            currentKind == null -> {
                TOP_DECL_RE.find(trimmed)?.groupValues?.get(1)
                    ?: TOP_FN_RE.find(trimmed)?.groupValues?.get(1)
            }

            else -> null
        }

        val docText = docKey?.let { docs[it] }
        if (docText != null && !lineAlreadyHasDoc(lines, lineIdx)) {
            result.add(formatJsDoc(docText, indentStr))
        }

        result.add(line)

        // Push a new frame only when this line leaves a block open (net depth increase).
        // Balanced `{ ... }` on one line (opens == closes) must NOT push a frame —
        // the entry depth would equal the current depth and the frame would never be popped.
        if (newDepth > depth) {
            val frame: Frame? = when (currentKind) {
                null -> {
                    // Top level
                    TOP_DECL_RE.find(trimmed)?.groupValues?.get(1)?.let {
                        Frame(Kind.DECLARE, it, newDepth)
                    } ?: TOP_NS_RE.find(trimmed)?.groupValues?.get(1)?.let {
                        Frame(Kind.NAMESPACE, it, newDepth)
                    }
                    // top-level `export declare const/function` with a body — treat as skip
                    ?: Frame(Kind.SKIP, "", newDepth)
                }
                Kind.NAMESPACE -> when {
                    trimmed.startsWith("namespace \$metadata\$") ->
                        Frame(Kind.SKIP, "", newDepth)
                    // `namespace Foo {` inside a `namespace Bar {` is always the $metadata$
                    // wrapper for inner class Foo — skip its content
                    INNER_NS_RE.matches(trimmed.substringBefore("{").trim()) ->
                        Frame(Kind.SKIP, "", newDepth)
                    else ->
                        INNER_CLASS_RE.find(trimmed)?.groupValues?.get(1)?.let {
                            Frame(Kind.INNER_CLASS, it, newDepth)
                        } ?: Frame(Kind.SKIP, "", newDepth)
                }
                // A `{` inside a DECLARE or INNER_CLASS is an inline type literal
                // (e.g. `readonly __doNotUseOrImplementIt: { ... }`) — skip its content
                Kind.DECLARE, Kind.INNER_CLASS -> Frame(Kind.SKIP, "", newDepth)
                Kind.SKIP -> Frame(Kind.SKIP, "", newDepth)
            }
            frame?.let { stack.addLast(it) }
        }

        depth = newDepth
    }

    return result.joinToString("\n")
}

/** Returns true if the line immediately above (ignoring blanks) is already a JSDoc comment. */
private fun lineAlreadyHasDoc(lines: List<String>, idx: Int): Boolean {
    var i = idx - 1
    while (i >= 0 && lines[i].isBlank()) i--
    return i >= 0 && (lines[i].trimStart().startsWith("/**") || lines[i].trimStart().startsWith("* ") || lines[i].trimStart() == "*/")
}

private fun formatJsDoc(text: String, indent: String): String {
    // Convert KDoc [Reference] links to backtick code spans (FQ Kotlin names won't
    // resolve as TypeScript {@link} targets, so plain code is cleaner)
    val converted = text
        .replace(Regex("""\[([^\]]+)]""")) { "`${it.groupValues[1]}`" }
        .replace("@return ", "@returns ")

    val lines = converted.lines().filter { it.isNotBlank() }
    return if (lines.size == 1) {
        "$indent/** ${lines[0]} */"
    } else {
        buildString {
            append("$indent/**\n")
            lines.forEach { append("$indent * $it\n") }
            append("$indent */")
        }
    }
}
