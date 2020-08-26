package com.thinkslynk.fabric_annotations.example.block

import com.thinkslynk.fabric_annotations.example.ExampleMod
import com.thinkslynk.fabric.annotations.registry.RegisterBlock
import com.thinkslynk.fabric.annotations.registry.RegisterItem
import com.thinkslynk.fabric.helpers.AnnotationHelpers
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.Material

@RegisterBlock(ExampleMod.identifier, ExampleBlock.NAME)
@RegisterItem(ExampleMod.identifier, ExampleBlock.NAME, AnnotationHelpers.ItemGroup.MISC)
class ExampleBlock: Block(
    FabricBlockSettings
        .of(Material.METAL)
        .hardness(4.0f)
){
    companion object{
        const val NAME = "example_block"
    }
}