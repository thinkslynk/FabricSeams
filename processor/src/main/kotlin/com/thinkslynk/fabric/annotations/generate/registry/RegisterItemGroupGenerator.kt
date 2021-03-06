package com.thinkslynk.fabric.annotations.generate.registry

import com.squareup.kotlinpoet.*
import com.thinkslynk.fabric.annotations.FabricProcessor
import com.thinkslynk.fabric.annotations.extensions.*
import com.thinkslynk.fabric.annotations.find.registry.ItemGroupFinder
import com.thinkslynk.fabric.annotations.generate.Generator
import java.nio.file.Path
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element

class RegisterItemGroupGenerator: Generator {
    companion object {
        const val CLASS_NAME = "MyItemGroups"

        fun formatPropertyName(name: String): String {
            return name.camelToSnakeCase().toUpperCase()
        }
    }

    override fun generate(folder: Path, processingEnv: ProcessingEnvironment) {
        val elements = ItemGroupFinder.itemGroups
        if (elements.isEmpty()) return

        // Output file
        generateRegistryFile(
            elements,
            generateRegistryClass(
                elements
            )
        ).writeTo(folder)
    }

    private fun generateRegistryFile(elements: Collection<Element>, vararg classes: TypeSpec): FileSpec =
        FileSpec.builder(FabricProcessor.GENERATED_PACKAGE, CLASS_NAME)
            .addImport("net.minecraft.util", "Identifier")
            .addImport("net.minecraft.util.registry", "Registry")
            .addImports(elements)
            .addTypes(classes.asList())
            .build()

    private fun generateRegistryClass(elements: Collection<Element>, vararg functions: FunSpec): TypeSpec=
        TypeSpec.objectBuilder(CLASS_NAME)
            .addDefaultProperties(elements, ::formatPropertyName)
            .build()


    override val finders get() = listOf(ItemGroupFinder)
}