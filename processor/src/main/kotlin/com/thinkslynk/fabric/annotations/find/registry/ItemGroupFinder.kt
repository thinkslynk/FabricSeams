package com.thinkslynk.fabric.annotations.find.registry

import com.thinkslynk.fabric.annotations.find.Finder
import com.thinkslynk.fabric.annotations.registry.RegisterItemGroup
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.tools.Diagnostic
import kotlin.reflect.KClass

object ItemGroupFinder : Finder {
    override val annotation: KClass<out Annotation> = RegisterItemGroup::class

    private val elements : MutableList<Element> = mutableListOf()
    val itemGroups: List<Element> get() = elements

    override fun accept(element: Element, processingEnv: ProcessingEnvironment):Boolean{
        if(element.kind != ElementKind.METHOD){
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Not a method", element)
            return false
        }
        elements.add(element)
        return true
    }
}