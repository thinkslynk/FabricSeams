package com.thinkslynk.fabric.annotations.generate.registry

import com.thinkslynk.fabric.annotations.extensions.camelToSnakeCase
import com.thinkslynk.fabric.annotations.find.registry.ArgumentFinder
import com.thinkslynk.fabric.annotations.find.registry.BlockFinder
import com.thinkslynk.fabric.annotations.registry.RegisterBlock
import javax.lang.model.element.TypeElement

class RegisterBlockGenerator : AbstractRegistryGenerator(CLASS_NAME, FUNC_NAME, "BLOCK") {
    companion object {
        const val CLASS_NAME = "BlockRegistryGenerated"
        const val FUNC_NAME = "register"

        fun formatPropertyName(name: String): String {
            return name.camelToSnakeCase().toUpperCase()
        }
    }

    override val typeElements: List<TypeElement>
        get() = BlockFinder.blocks

    override fun getAnnotationNamespace(typeElement: TypeElement): String =
        typeElement.getAnnotation(RegisterBlock::class.java).namespace


    override fun getAnnotationName(typeElement: TypeElement): String =
        typeElement.getAnnotation(RegisterBlock::class.java).name


    override val finders get() = listOf(BlockFinder, ArgumentFinder)
}