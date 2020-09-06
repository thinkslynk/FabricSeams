package com.thinkslynk.fabric.annotations.generate.registry

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ClassName.Companion.bestGuess
import com.thinkslynk.fabric.annotations.FabricProcessor
import com.thinkslynk.fabric.annotations.extensions.*
import com.thinkslynk.fabric.annotations.processor.definition.BlockItemDefinitionProcessor
import com.thinkslynk.fabric.annotations.processor.find.blockItemFinder
import com.thinkslynk.fabric.annotations.registry.RegisterBlockItem
import com.thinkslynk.fabric.helpers.AnnotationHelpers
import java.nio.file.Path
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element

class RegisterBlockItemGenerator: AbstractRegistryGenerator(
    CLASS_NAME,
    FUNC_NAME,
    "ITEM",
    BlockItemDefinitionProcessor
) {
    companion object {
        const val CLASS_NAME = "BlockItemRegistryGenerated"
        const val FUNC_NAME = "register"

        fun formatPropertyName(name: String): String {
            return name.camelToSnakeCase().toUpperCase()
        }
    }

    fun generate_old(folder: Path, processingEnv: ProcessingEnvironment) {
        val elements = blockItemFinder.elements
        if (elements.isEmpty()) return

        // Output file
        generateRegistryFile(
            generateRegistryClass(
                elements,
                generateRegisterFunction(
                    elements
                )
            )
        ).writeTo(folder)
    }

    private fun generateRegistryFile(vararg classes: TypeSpec): FileSpec =
        FileSpec.builder(FabricProcessor.GENERATED_PACKAGE, CLASS_NAME)
            .addImport("net.minecraft.util", "Identifier")
            .addImport("net.minecraft.util.registry", "Registry")
            .addImport("net.minecraft.item", "BlockItem")
            .addImport("net.minecraft.item", "Item")
            .addImport("net.minecraft.item", "ItemGroup")
            .addImport(FabricProcessor.GENERATED_PACKAGE, RegisterItemGroupGenerator.CLASS_NAME)
            .addTypes(classes.asList())
            .build()

    private fun generateRegistryClass(elements: Collection<Element>, vararg functions: FunSpec): TypeSpec =
        TypeSpec.objectBuilder(CLASS_NAME)
            .addInitializedProperties(elements, ::formatPropertyName, {
                element ->
                val annotation = element.getAnnotation(RegisterBlockItem::class.java)
                val prop = formatPropertyName(element.simpleName.toString())
                val group = if (AnnotationHelpers.ItemGroup.contains(annotation.itemGroup)) {
                    "ItemGroup.${annotation.itemGroup}"
                } else {
                    "${RegisterItemGroupGenerator.CLASS_NAME}.${annotation.itemGroup}"
                }

                "BlockItem(BlockRegistryGenerated.$prop, Item.Settings().group(${group}))"
            }, bestGuess("net.minecraft.item.BlockItem"))
            .addFunctions(functions.asList())
            .build()

    private fun generateRegisterFunction(elements: Collection<Element>): FunSpec {
        var funcBuilder = FunSpec.builder(FUNC_NAME)
            .addModifiers(KModifier.PUBLIC)

        elements.forEach {
            val annotation = it.getAnnotation(RegisterBlockItem::class.java)
            val propName = formatPropertyName(it.simpleName.toString())

            funcBuilder = funcBuilder.addStatement(
                "Registry.register(" +
                        "Registry.ITEM, " +
                        "Identifier(\"${annotation.namespace}\", \"${annotation.name}\"), " +
                        propName +
                        ")"
            )
        }

        return funcBuilder.build()
    }
}