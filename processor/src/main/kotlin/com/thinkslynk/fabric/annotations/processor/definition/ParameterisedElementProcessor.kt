package com.thinkslynk.fabric.annotations.processor.definition

import com.squareup.kotlinpoet.asClassName
import com.thinkslynk.fabric.annotations.builder.RegistryBuilder
import com.thinkslynk.fabric.annotations.extensions.crossFlatMap
import com.thinkslynk.fabric.annotations.extensions.findConstructors
import com.thinkslynk.fabric.annotations.processor.Processor
import com.thinkslynk.fabric.annotations.processor.SimpleElementProcessor
import com.thinkslynk.fabric.annotations.processor.find.registry.ArgumentFinder
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.tools.Diagnostic
import com.thinkslynk.fabric.annotations.extensions.camelToUpperSnakeCase

abstract class ParameterisedElementProcessor(parent: SimpleElementProcessor<TypeElement>) :
    DefinitionProcessor<TypeElement>(parent) {

    override fun process(item: TypeElement, processingEnv: ProcessingEnvironment): Iterable<Definition> = buildList {
        var found = false
        val annotationNamespace = getAnnotationNamespace(item)
        val annotationName = getAnnotationName(item)
        val className = item.asClassName()
        for (constructor in item.findConstructors()) {
            val args: List<VariableElement> = constructor.parameters
            if (args.isNotEmpty()) {
                if (args.all {
                        ArgumentFinder.knows(
                            it
                        )
                    }) {
                    addItems(item, args, annotationNamespace, annotationName, processingEnv)
                    found = true
                }
            } else {
                add(
                    Definition(
                        element = item,
                        registryNamespace = annotationNamespace,
                        name = className.simpleName.camelToUpperSnakeCase(),
                        type = className,
                        registryName = annotationName
                    )
                )
                found = true
            }
        }
        if (!found) {
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "No constructor available", item)
        }

    }

    private fun MutableList<Definition>.addItems(
        type: TypeElement,
        args: List<VariableElement>,
        annotationNamespace: String,
        annotationName: String,
        processingEnv: ProcessingEnvironment
    ) {
        val constName = type.simpleName.toString().camelToUpperSnakeCase()
        val className = type.asClassName()
        args.crossFlatMap({ arg ->
            val (argType, items) = ArgumentFinder.values(arg, processingEnv)

            items.map { argType to it }
        }) { genArgs ->
            val name = genArgs.joinToString(separator = "_", postfix = "_$constName") { it.second }
            val callArgs = genArgs.joinToString { it.first.nestedClass(it.second).canonicalName }
            add(
                Definition(
                    element = type,
                    registryNamespace = annotationNamespace,
                    name = name,
                    type = className,
                    registryName = RegistryBuilder.generateRegistryName(annotationName, genArgs),
                    callArgs = callArgs
                )
            )
        }
    }

    abstract fun getAnnotationNamespace(typeElement: TypeElement): String
    abstract fun getAnnotationName(typeElement: TypeElement): String

    override val requires: List<Processor> = listOf(ArgumentFinder, parent)
}

