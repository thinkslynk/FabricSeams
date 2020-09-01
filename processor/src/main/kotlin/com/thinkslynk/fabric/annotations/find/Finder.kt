package com.thinkslynk.fabric.annotations.find

import kotlin.reflect.KClass
import javax.annotation.processing.*
import javax.lang.model.element.Element

interface Finder {
    val annotation: KClass<out Annotation>
    val requires: List<Finder> get() = emptyList()
    fun accept(element: Element, processingEnv:ProcessingEnvironment): Boolean
}