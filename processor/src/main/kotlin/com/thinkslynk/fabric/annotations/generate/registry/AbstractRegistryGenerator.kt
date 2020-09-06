package com.thinkslynk.fabric.annotations.generate.registry

import com.thinkslynk.fabric.annotations.FabricProcessor
import com.thinkslynk.fabric.annotations.builder.RegistryBuilder
import com.thinkslynk.fabric.annotations.extensions.findConstructors
import com.thinkslynk.fabric.annotations.extensions.warn
import com.thinkslynk.fabric.annotations.processor.find.registry.ArgumentFinder
import com.thinkslynk.fabric.annotations.generate.Generator
import com.thinkslynk.fabric.annotations.processor.SimpleElementProcessor
import com.thinkslynk.fabric.annotations.processor.definition.Definition
import com.thinkslynk.fabric.annotations.processor.definition.DefinitionProcessor
import java.nio.file.Path
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.tools.Diagnostic

abstract class AbstractRegistryGenerator(val className:String, val funcName:String, val registry: String, val definitionProcessor: SimpleElementProcessor<Definition>):Generator{
    override fun generate(folder: Path, processingEnv: ProcessingEnvironment) {
        val elements = definitionProcessor.elements
        if (elements.isEmpty()) return

        val builder = RegistryBuilder(FabricProcessor.GENERATED_PACKAGE, className, funcName, registry)
        for (element in elements) {
            builder.addDefinition(element)
        }

        // Output file
        builder.writeTo(folder)
    }
    override val finders get() = listOf(definitionProcessor)
}