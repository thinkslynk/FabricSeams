package com.thinkslynk.fabric.annotations.generate.registry

import com.thinkslynk.fabric.annotations.processor.definition.BlockDefinitionProcessor

class RegisterBlockGenerator : AbstractRegistryGenerator(
    CLASS_NAME,
    FUNC_NAME,
    "BLOCK",
    BlockDefinitionProcessor
) {
    companion object {
        const val CLASS_NAME = "BlockRegistryGenerated"
        const val FUNC_NAME = "register"
    }
}