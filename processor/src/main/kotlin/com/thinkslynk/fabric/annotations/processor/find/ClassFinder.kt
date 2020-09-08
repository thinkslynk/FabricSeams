package com.thinkslynk.fabric.annotations.processor.find

import com.thinkslynk.fabric.annotations.extensions.isClass
import com.thinkslynk.fabric.annotations.processor.SimpleElementProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import kotlin.reflect.KClass


class ClassFinder<T : Annotation>(override val annotation: KClass<T>) : AnnotationFinder,
    SimpleElementProcessor<TypeElement> {

    private val items: MutableList<TypeElement> = mutableListOf()
    override val elements: List<TypeElement> get() = items

    override fun accept(element: Element, processingEnv: ProcessingEnvironment): Boolean {
        if (!element.isClass()) {
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Not a class", element)
            return false
        }
        items.add(element)
        return true
    }

    companion object{
        inline operator fun <reified T:Annotation>invoke() = ClassFinder(T::class)
    }
}