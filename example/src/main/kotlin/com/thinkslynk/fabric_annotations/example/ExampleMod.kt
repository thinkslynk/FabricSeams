package com.thinkslynk.fabric_annotations.example

import com.thinkslynk.fabric.generated.BlockRegistryGenerated
import com.thinkslynk.fabric.generated.BlockItemRegistryGenerated
import net.fabricmc.api.ModInitializer

object ExampleMod: ModInitializer {
    const val identifier = "example_mod"

    override fun onInitialize() {
        println("Initializing example mod...")
        BlockRegistryGenerated.register()
        BlockItemRegistryGenerated.register()
    }
}