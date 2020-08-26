package com.thinkslynk.fabric.annotations

import com.google.auto.service.AutoService
import com.thinkslynk.fabric.annotations.generate.IGenerator
import com.thinkslynk.fabric.annotations.generate.registry.RegisterBlockEntityGenerator
import com.thinkslynk.fabric.annotations.generate.registry.RegisterBlockGenerator
import com.thinkslynk.fabric.annotations.generate.registry.RegisterBlockItemGenerator
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(
    FabricProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME
)
open class FabricProcessor: AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        const val GENERATED_PACKAGE = "com.thinkslynk.fabric.generated"

        val generators: List<IGenerator> = listOf(
            RegisterBlockGenerator(),
            RegisterBlockItemGenerator(),
            RegisterBlockEntityGenerator()
        )
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
        val file = File(generatedSourcesRoot)
        file.mkdir()

        // TODO Find root package of mod

        // TODO Create our full registry that points to all other generated registries

        // Run our standard generators
        generators.forEach{
            val elements = roundEnv.getElementsAnnotatedWith(it.getSupportedAnnotationClass().java)
            it.generate(elements, file)
        }

        return true
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return generators
            .map { it.getSupportedAnnotationClass().java.canonicalName }
            .toMutableSet()
    }
}