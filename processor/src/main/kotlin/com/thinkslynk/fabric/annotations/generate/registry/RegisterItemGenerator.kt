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
import javax.lang.model.element.VariableElement
import javax.tools.Diagnostic

class RegisterItemGenerator: Generator {
    companion object {
        const val CLASS_NAME = "ItemRegistryGenerated"
        const val FUNC_NAME = "register"
    }

    override fun generate(folder: Path, processingEnv: ProcessingEnvironment) {
        val elements = ItemFinder.items
        if (elements.isEmpty()) return

        val builder = RegistryBuilder(FabricProcessor.GENERATED_PACKAGE, CLASS_NAME, FUNC_NAME)

        for (element in elements) {
            var found = false
            for(constructor in element.findConstructors()){
                val args:List<VariableElement> = constructor.parameters
                if(args.isNotEmpty()) {
                    if (args.all { ArgumentFinder.knows(it) }) {
                        builder.addItem(element, args)
                        found = true
                    }
                }else{
                    builder.addItem(element)
                    found = true
                }
            }
            if(!found){
                processingEnv.messager.printMessage(Diagnostic.Kind.ERROR,"No constructor available",element)
            }
        }

        // Output file
        builder.writeTo(folder)
    }

    override val finders get() = listOf(ItemFinder, ArgumentFinder)
}