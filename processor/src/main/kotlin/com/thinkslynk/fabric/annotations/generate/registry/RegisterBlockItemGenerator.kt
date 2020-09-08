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

}