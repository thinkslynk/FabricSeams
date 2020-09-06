package com.thinkslynk.fabric.annotations.processor.find.registry

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import com.thinkslynk.fabric.annotations.extensions.error
import com.thinkslynk.fabric.annotations.processor.find.AnnotationFinder
import com.thinkslynk.fabric.annotations.registry.Category
import com.thinkslynk.fabric.annotations.registry.RegisterArgument
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind
import javax.tools.Diagnostic.Kind
import kotlin.reflect.KClass

object ArgumentFinder : AnnotationFinder {
    override val annotation: KClass<out Annotation> = RegisterArgument::class

    private val elements: MutableMap<ClassName, List<Element>> = mutableMapOf()

    override fun accept(element: Element, processingEnv: ProcessingEnvironment): Boolean {
        if (element.kind != ElementKind.ENUM) {
            processingEnv.messager.printMessage(Kind.ERROR, "Not an enum", element)
            return false
        }
        element as TypeElement
        val constants = element.enclosedElements.filter { it.kind == ElementKind.ENUM_CONSTANT }
        elements[element.asClassName()] = constants.map { it }
        return true
    }

    fun knows(element: VariableElement): Boolean {
        val name = getClassName(element) ?: return false
        return name in elements
    }

    private fun getClassName(element: VariableElement): ClassName? {
        val type = element.asType()
        if (type.kind != TypeKind.DECLARED)
            return null
        type as DeclaredType
        val typeElement = type.asElement()
        if (typeElement !is TypeElement)
            return null
        return typeElement.asClassName()
    }

    fun values(arg: VariableElement, processingEnv: ProcessingEnvironment): Pair<ClassName, List<String>> {
        val className = getClassName(arg) ?: error("oh no")
        val cat = arg.getAnnotation(Category::class.java)
        val enumValues = elements.getValue(className)
        if (cat == null) {
            return className to enumValues.map { it.simpleName.toString() }
        } else {
            // all the values that match all given categories
            val items = enumValues.filter { value ->
                val valueAnnotation = value.getAnnotation(Category::class.java)
                if (valueAnnotation == null)
                    false
                else
                    cat.name.all { it in valueAnnotation.name }
            }
            if (items.isEmpty()) {
                // oof, probably a typo or something
                processingEnv.messager.error(
                    "Could not find arguments for this parameter with categories: " +
                            "[${cat.name.joinToString()}]. Following categories were found for this class: " +
                            "[${enumValues
                                .mapNotNull{it.getAnnotation(Category::class.java)}
                                .flatMap{it.name.toList()}
                                .sorted()
                                .joinToString()}]", arg
                )
                return className to emptyList()
            }
            return className to items.map{it.simpleName.toString()}
        }
    }
}