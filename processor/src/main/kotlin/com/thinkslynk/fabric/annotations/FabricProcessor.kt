package com.thinkslynk.fabric.annotations

import com.google.auto.service.AutoService
import com.thinkslynk.fabric.annotations.find.Finder
import com.thinkslynk.fabric.annotations.generate.Generator
import com.thinkslynk.fabric.annotations.generate.registry.*
import java.lang.RuntimeException
import java.nio.file.Files
import java.nio.file.Path
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
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

        val finders = setupAllFinders()
        fun setupAllFinders() : List<Finder>{
            val processing: MutableSet<Finder> = mutableSetOf()
            val found: MutableSet<Finder> = LinkedHashSet<Finder>()

            // * marks a finder as processing
            // * processes required processing
            //      * if any are already marked they will error
            fun process(finder: Finder) {
                if (finder in found)
                    return
                // if finder already removed, we got a cycle
                if (!processing.add(finder)) {
                    throw RuntimeException("Finders are cyclic. Cycle contains:$finder")
                }

                // true if a child errors
                for (nextFinder in finder.requires) {
                    process(nextFinder)
                }
                found.add(finder)
            }
            // start all finders
            generators.forEach{ it.finders.forEach(::process) }

            // revert the list
            return found.reversed()
        }
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {

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
        for (finder in finders) {
            val elements = roundEnv.getElementsAnnotatedWith(finder.annotation.java)
            processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "$finder: ${elements.joinToString()}")
            for(element in elements){
                finder.accept(element, processingEnv)
            }
        }

        // process generators
        for(generator in generators){
            processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "starting generator: $generator\n")
            generator.generate(path)
        }

        return true
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return finders.map { it.annotation.java.canonicalName }.toMutableSet()
    }


}