package com.thinkslynk.fabric.annotations.find.registry

import com.thinkslynk.fabric.annotations.find.Finder
import com.thinkslynk.fabric.annotations.registry.RegisterItem
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.tools.Diagnostic
import kotlin.reflect.KClass

object ItemFinder : Finder {
    override val annotation: KClass<out Annotation> = RegisterItem::class

    private val classElements : MutableList<TypeElement> = mutableListOf()
    //private val constructorElements : MutableList<TypeElement> = mutableListOf()
    val items: List<TypeElement> get() = classElements

    override fun accept(element: Element, processingEnv: ProcessingEnvironment):Boolean{
        if(element.kind != ElementKind.CLASS){
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Not a class", element)
            return false
        }
        classElements.add(element as TypeElement)
        return true
    }
}