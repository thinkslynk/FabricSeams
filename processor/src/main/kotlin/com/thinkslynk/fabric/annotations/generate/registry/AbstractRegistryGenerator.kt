package com.thinkslynk.fabric.annotations.generate.registry

import com.thinkslynk.fabric.annotations.FabricProcessor
import com.thinkslynk.fabric.annotations.builder.RegistryBuilder
import com.thinkslynk.fabric.annotations.extensions.findConstructors
import com.thinkslynk.fabric.annotations.find.registry.ArgumentFinder
import com.thinkslynk.fabric.annotations.generate.Generator
import java.nio.file.Path
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.tools.Diagnostic

abstract class AbstractRegistryGenerator(val className:String, val funcName:String, val registry: String) :Generator{
    abstract val typeElements:List<TypeElement>

    override fun generate(folder: Path, processingEnv: ProcessingEnvironment) {
        val elements = typeElements
        if (elements.isEmpty()) return

        val builder = RegistryBuilder(FabricProcessor.GENERATED_PACKAGE, className, funcName, registry)

        for (element in elements) {
            var found = false
            val annotationNamespace = getAnnotationNamespace(element)
            val annotationName = getAnnotationName(element)
            for(constructor in element.findConstructors()){
                val args:List<VariableElement> = constructor.parameters
                if(args.isNotEmpty()) {
                    if (args.all { ArgumentFinder.knows(it) }) {
                        builder.addItem(element, args, annotationNamespace, annotationName)
                        found = true
                    }
                }else{
                    builder.addItem(element, annotationNamespace, annotationName)
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

    abstract fun getAnnotationNamespace(typeElement:TypeElement):String
    abstract fun getAnnotationName(typeElement:TypeElement):String
}