package com.thinkslynk.fabric.annotations.generate.registry

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ClassName.Companion.bestGuess
import com.thinkslynk.fabric.annotations.FabricProcessor
import com.thinkslynk.fabric.annotations.extensions.*
import com.thinkslynk.fabric.annotations.generate.IGenerator
import com.thinkslynk.fabric.annotations.registry.RegisterBlockItem
import com.thinkslynk.fabric.annotations.registry.RegisterItem
import java.io.File
import java.nio.file.Path
import javax.lang.model.element.Element
import kotlin.reflect.KClass

class RegisterItemGenerator: IGenerator {
    companion object {
        const val CLASS_NAME = "ItemRegistryGenerated"
        const val FUNC_NAME = "register"

        fun formatPropertyName(name: String): String {
            return name.camelToSnakeCase().toUpperCase()
        }
    }

    override fun generate(elements: Collection<Element>, folder: Path) {
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
            val annotation = it.getAnnotation(RegisterItem::class.java)
            val propName = formatPropertyName(it.simpleName.toString())

            funcBuilder = funcBuilder.addStatement(
                "Registry.register(Registry.ITEM, " +
                        "Identifier(\"${annotation.namespace}\", \"${annotation.name}\"), " +
                        "$propName)"
            )
        }

        return funcBuilder.build()
    }

    override fun getSupportedAnnotationClass(): KClass<out Annotation> {
        return RegisterItem::class
    }
}