package com.thinkslynk.fabric.annotations.processor.find.registry

import com.thinkslynk.fabric.annotations.processor.find.AnnotationFinder
import com.thinkslynk.fabric.annotations.processor.SimpleElementProcessor
import com.thinkslynk.fabric.annotations.registry.RegisterItemGroup
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.tools.Diagnostic
import kotlin.reflect.KClass

object ItemGroupFinder : AnnotationFinder,
    SimpleElementProcessor<Element> {
    override val annotation: KClass<out Annotation> = RegisterItemGroup::class

    private val itemGroups : MutableList<Element> = mutableListOf()
    override val elements: List<Element> get() = itemGroups

    override fun accept(element: Element, processingEnv: ProcessingEnvironment):Boolean{
        if(element.kind != ElementKind.METHOD){
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Not a method", element)
            return false
        }
        itemGroups.add(element)
        return true
    }
}