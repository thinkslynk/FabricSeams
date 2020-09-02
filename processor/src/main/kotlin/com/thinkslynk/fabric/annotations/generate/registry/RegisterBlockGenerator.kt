package com.thinkslynk.fabric.annotations.generate.registry

import com.thinkslynk.fabric.annotations.extensions.camelToSnakeCase
import com.thinkslynk.fabric.annotations.find.registry.ArgumentFinder
import com.thinkslynk.fabric.annotations.find.registry.BlockFinder
import javax.lang.model.element.TypeElement

class RegisterBlockGenerator: AbstractRegistryGenerator() {
    companion object {
        const val CLASS_NAME = "BlockRegistryGenerated"
        const val FUNC_NAME = "register"

        fun formatPropertyName(name: String): String {
            return name.camelToSnakeCase().toUpperCase()
        }
    }

    override val typeElements: List<TypeElement>
        get() = BlockFinder.blocks


    override val finders get() = listOf(BlockFinder, ArgumentFinder)
}