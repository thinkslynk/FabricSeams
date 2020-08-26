package com.thinkslynk.fabric_annotations.example.entity

import com.thinkslynk.fabric.generated.BlockEntityRegistryGenerated
import com.thinkslynk.fabric.annotations.registry.RegisterBlockEntity
import com.thinkslynk.fabric_annotations.example.ExampleMod
import com.thinkslynk.fabric_annotations.example.block.ExampleBlock
import net.minecraft.block.entity.BlockEntity

@RegisterBlockEntity(ExampleMod.identifier, ExampleBlock.NAME, ["EXAMPLE_BLOCK"])
class ExampleBlockEntity: BlockEntity(BlockEntityRegistryGenerated.EXAMPLE_BLOCK_ENTITY)