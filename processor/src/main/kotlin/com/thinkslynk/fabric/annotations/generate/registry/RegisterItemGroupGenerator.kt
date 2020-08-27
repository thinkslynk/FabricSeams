package com.thinkslynk.fabric.annotations.generate.registry

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ClassName.Companion.bestGuess
import com.thinkslynk.fabric.annotations.FabricProcessor
import com.thinkslynk.fabric.annotations.extensions.*
import com.thinkslynk.fabric.annotations.generate.IGenerator
import com.thinkslynk.fabric.annotations.registry.RegisterBlockItem
import com.thinkslynk.fabric.annotations.registry.RegisterItem
import com.thinkslynk.fabric.annotations.registry.RegisterItemGroup
import java.io.File
import javax.lang.model.element.Element
import kotlin.reflect.KClass

class RegisterItemGroupGenerator: IGenerator {
    companion object {
        const val CLASS_NAME = "MyItemGroups"

        fun formatPropertyName(name: String): String {
            return name.camelToSnakeCase().toUpperCase()
        }
    }

    override fun generate(elements: Collection<Element>, folder: File) {
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


    override fun getSupportedAnnotationClass(): KClass<out Annotation> {
        return RegisterItemGroup::class
    }
}