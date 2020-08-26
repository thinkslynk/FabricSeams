package com.thinkslynk.fabric.annotations.generate.registry

import com.squareup.kotlinpoet.*
import com.thinkslynk.fabric.annotations.FabricProcessor
import com.thinkslynk.fabric.annotations.extensions.addDefaultProperties
import com.thinkslynk.fabric.annotations.extensions.addImports
import com.thinkslynk.fabric.annotations.extensions.addTypes
import com.thinkslynk.fabric.annotations.extensions.camelToSnakeCase
import com.thinkslynk.fabric.annotations.generate.IGenerator
import com.thinkslynk.fabric.annotations.registry.RegisterBlock
import java.io.File
import javax.lang.model.element.Element
import javax.lang.model.element.QualifiedNameable
import kotlin.reflect.KClass

class RegisterBlockGenerator: IGenerator {
    companion object {
        const val CLASS_NAME = "BlockRegistryGenerated"
        const val FUNC_NAME = "register"

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
                elements,
                generateRegisterFunction(
                    elements
                )
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
            .addFunctions(functions.asList())
            .build()

    private fun generateRegisterFunction(elements: Collection<Element>): FunSpec {
        var funcBuilder = FunSpec.builder(FUNC_NAME)
            .addModifiers(KModifier.PUBLIC)

        elements.forEach {
            val annotation = it.getAnnotation(RegisterBlock::class.java)
            val propName = formatPropertyName(it.simpleName.toString())

            funcBuilder = funcBuilder.addStatement(
                "Registry.register(Registry.BLOCK, " +
                        "Identifier(\"${annotation.namespace}\", \"${annotation.name}\"), " +
                        "$propName)"
            )
        }

        return funcBuilder.build()
    }

    override fun getSupportedAnnotationClass(): KClass<out Annotation> {
        return RegisterBlock::class
    }
}