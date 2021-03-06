package com.thinkslynk.fabric.annotations.generate.registry

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ClassName.Companion.bestGuess
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.thinkslynk.fabric.annotations.FabricProcessor
import com.thinkslynk.fabric.annotations.extensions.addImports
import com.thinkslynk.fabric.annotations.extensions.addLateInitProperties
import com.thinkslynk.fabric.annotations.extensions.addTypes
import com.thinkslynk.fabric.annotations.extensions.camelToSnakeCase
import com.thinkslynk.fabric.annotations.find.registry.BlockEntityFinder
import com.thinkslynk.fabric.annotations.generate.Generator
import com.thinkslynk.fabric.annotations.registry.RegisterBlockEntity
import java.nio.file.Path
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element

class RegisterBlockEntityGenerator: Generator {
    companion object {
        const val CLASS_NAME = "BlockEntityRegistryGenerated"
        const val FUNC_NAME = "register"

        fun formatPropertyName(name: String): String {
            return name.camelToSnakeCase().toUpperCase()
        }
    }
    
    override fun generate(folder: Path, processingEnv: ProcessingEnvironment) {
        val elements = BlockEntityFinder.blockEntities
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
            .addImport("net.minecraft.block.entity", "BlockEntityType")
            .addImport("java.util.function", "Supplier")
            .addImport(FabricProcessor.GENERATED_PACKAGE, RegisterBlockGenerator.CLASS_NAME)
            .addImports(elements)
            .addTypes(classes.asList())
            .build()

    private fun generateRegistryClass(elements: Collection<Element>, vararg functions: FunSpec): TypeSpec =
        TypeSpec.objectBuilder(CLASS_NAME)
            .addLateInitProperties(
                elements,
                ::formatPropertyName,
                typeGenerator = {
                    val t = it.asType().asTypeName()
                    bestGuess("net.minecraft.block.entity.BlockEntityType")
                        .plusParameter(t)
                }
            )
            .addFunctions(functions.asList())
            .build()

    private fun generateRegisterFunction(elements: Collection<Element>): FunSpec {
        var funcBuilder = FunSpec.builder(FUNC_NAME)
            .addModifiers(KModifier.PUBLIC)

        elements.forEach {
            val annotation = it.getAnnotation(RegisterBlockEntity::class.java)
            val name = it.simpleName.toString()
            val propName = formatPropertyName(name)

            val blocks = annotation.blocks.joinToString { block -> "BlockRegistryGenerated.$block" }

            funcBuilder = funcBuilder.addStatement(
                """
                    $propName = Registry.register(
                        Registry.BLOCK_ENTITY_TYPE,
                        Identifier("${annotation.namespace}", "${annotation.name}"),
                        BlockEntityType.Builder
                            .create(Supplier { $name() }, $blocks)
                            .build(null)
                    )
                """.trimIndent()
            )
        }

        return funcBuilder.build()
    }

    override val finders get() = listOf(BlockEntityFinder)
}