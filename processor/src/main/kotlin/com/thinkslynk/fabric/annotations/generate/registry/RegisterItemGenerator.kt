package com.thinkslynk.fabric.annotations.generate.registry

import com.thinkslynk.fabric.annotations.processor.definition.ItemDefinitionProcessor

class RegisterItemGenerator: AbstractRegistryGenerator(
    CLASS_NAME,
    FUNC_NAME,
    "ITEM",
    ItemDefinitionProcessor
) {
    companion object {
        const val CLASS_NAME = "ItemRegistryGenerated"
        const val FUNC_NAME = "register"
    }
}