package com.thinkslynk.fabric.annotations

import com.google.auto.service.AutoService
import com.thinkslynk.fabric.annotations.extensions.error
import com.thinkslynk.fabric.annotations.extensions.info
import com.thinkslynk.fabric.annotations.extensions.log
import com.thinkslynk.fabric.annotations.extensions.warn
import com.thinkslynk.fabric.annotations.generate.Generator
import com.thinkslynk.fabric.annotations.generate.registry.*
import com.thinkslynk.fabric.annotations.processor.Processor
import com.thinkslynk.fabric.annotations.processor.find.AnnotationFinder
import java.nio.file.Files
import java.nio.file.Path
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import javax.annotation.processing.Processor as JavaxAnnotationProcessingProcessor

@AutoService(JavaxAnnotationProcessingProcessor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(
    FabricProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME
)
open class FabricProcessor : AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        const val GENERATED_PACKAGE = "com.thinkslynk.fabric.generated"

        val generators: List<Generator> = listOf(
            RegisterBlockGenerator(),
            RegisterBlockItemGenerator(),
            RegisterBlockEntityGenerator(),
            RegisterItemGenerator(),
            RegisterItemGroupGenerator()
        )

        val processors = setupAllFinders()
        private fun setupAllFinders(): List<Processor> {
            val processing: MutableSet<Processor> = mutableSetOf()
            val found: MutableSet<Processor> = LinkedHashSet()

            // * marks a finder as processing
            // * processes required processing
            //      * if any are already marked they will error
            fun process(processor: Processor) {
                if (processor in found)
                    return
                // if finder already removed, we got a cycle
                if (!processing.add(processor)) {
                    throw RuntimeException("Finders are cyclic. Cycle contains:$processor")
                }

                // true if a child errors
                for (nextFinder in processor.requires) {
                    process(nextFinder)
                }
                found.add(processor)
            }
            // start all finders
            generators.forEach { it.finders.forEach(::process) }

            // revert the list
            return found.toList()//.reversed()
        }
    }

    private var hasRun = false

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        try {
            if (hasRun) {
                processingEnv.messager.warn("Trying to process multiple times")
                return false
            }
            hasRun = true
            val generatedSourcesRoot: String = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: run {
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "Can't find the target directory for generated Kotlin files."
                )
                return false
            }

            // Prep folder
            val path = Path.of(generatedSourcesRoot)
            Files.createDirectories(path)

            // TODO Find root package of mod

            // TODO Create our full registry that points to all other generated registries

            // process finders
            processingEnv.messager.log("processors activated: ${processors.joinToString()}")

            for (processor in processors) {
                processingEnv.messager.info("running: $processor")
                processor.run(roundEnv, processingEnv)
            }

            // process generators
            for (generator in generators) {
                processingEnv.messager.info("running: $generator")
                generator.generate(path, processingEnv)
            }
            return true
        } catch(ex:Exception) {
            processingEnv.messager.error("Exception while processing")
            processingEnv.messager.error("msg: ${ex.message}")
            processingEnv.messager.error("trace: ${ex.stackTrace.joinToString("\n")}")
            throw ex
        }
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return processors
            .filterIsInstance<AnnotationFinder>()
            .map { it.annotation.java.canonicalName }
            .toMutableSet()
    }
}