package com.thinkslynk.fabric.annotations.find.registry

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import com.thinkslynk.fabric.annotations.find.Finder
import com.thinkslynk.fabric.annotations.registry.RegisterArgument
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.*
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind
import javax.tools.Diagnostic.Kind
import kotlin.reflect.KClass

object ArgumentFinder : Finder {
    override val annotation: KClass<out Annotation> = RegisterArgument::class

    private val elements : MutableMap<ClassName, List<String>> = mutableMapOf()
    val enums: Map<ClassName,List<String>> get() = elements

    override fun accept(element: Element, processingEnv: ProcessingEnvironment):Boolean{
        if(element.kind != ElementKind.ENUM){
            processingEnv.messager.printMessage(Kind.ERROR, "Not an enum", element)
            return false
        }
        element as TypeElement
        val constants = element.enclosedElements.filter{it.kind == ElementKind.ENUM_CONSTANT}
        elements[element.asClassName()] = constants.map { it.simpleName.toString() }
        processingEnv.messager.printMessage(Kind.WARNING, "enum: ${element.asClassName()} -> ${elements[element.asClassName()]}", element)
        return true
    }

    fun knows(element: VariableElement): Boolean {
        val name = getClassName(element) ?: return false
        return name in enums
    }

    private fun getClassName(element: VariableElement): ClassName? {
        val type = element.asType()
        if(type.kind != TypeKind.DECLARED)
            return null
        type as DeclaredType
        val typeElement = type.asElement()
        if(typeElement !is TypeElement)
            return null
        return typeElement.asClassName()
    }

    fun values(arg: VariableElement): Pair<ClassName, List<String>> {
        val className = getClassName(arg)?:error("oh no")
        return className to enums.getValue(className)
    }
}