package com.thinkslynk.fabric.annotations.generate.registry

import com.squareup.kotlinpoet.*
import com.thinkslynk.fabric.annotations.FabricProcessor
import com.thinkslynk.fabric.annotations.builder.RegistryBuilder
import com.thinkslynk.fabric.annotations.builder.RegistryBuilder.Companion.formatPropertyName
import com.thinkslynk.fabric.annotations.extensions.*
import com.thinkslynk.fabric.annotations.find.registry.ArgumentFinder
import com.thinkslynk.fabric.annotations.find.registry.ItemFinder
import com.thinkslynk.fabric.annotations.generate.Generator
import com.thinkslynk.fabric.annotations.registry.RegisterItem
import java.nio.file.Path
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.tools.Diagnostic

class RegisterItemGenerator: AbstractRegistryGenerator() {
    companion object {
        const val CLASS_NAME = "ItemRegistryGenerated"
        const val FUNC_NAME = "register"
    }

    override val typeElements: List<TypeElement>
        get() = ItemFinder.items

    override val finders get() = listOf(ItemFinder, ArgumentFinder)
}