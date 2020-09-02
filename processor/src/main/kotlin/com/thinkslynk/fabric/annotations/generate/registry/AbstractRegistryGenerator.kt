package com.thinkslynk.fabric.annotations.generate.registry

import com.thinkslynk.fabric.annotations.FabricProcessor
import com.thinkslynk.fabric.annotations.builder.RegistryBuilder
import com.thinkslynk.fabric.annotations.extensions.findConstructors
import com.thinkslynk.fabric.annotations.find.registry.ArgumentFinder
import com.thinkslynk.fabric.annotations.find.registry.ItemFinder
import com.thinkslynk.fabric.annotations.generate.Generator
import java.nio.file.Path
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.tools.Diagnostic

abstract class AbstractRegistryGenerator :Generator{
    abstract val typeElements:List<TypeElement>

    override fun generate(folder: Path, processingEnv: ProcessingEnvironment) {
        val elements = typeElements
        if (elements.isEmpty()) return

        val builder = RegistryBuilder(FabricProcessor.GENERATED_PACKAGE, RegisterItemGenerator.CLASS_NAME, RegisterItemGenerator.FUNC_NAME)

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
}