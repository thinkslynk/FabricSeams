package com.thinkslynk.fabric.annotations.processor.find

import com.thinkslynk.fabric.annotations.processor.Processor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import kotlin.reflect.KClass

interface AnnotationFinder : Processor {
    val annotation: KClass<out Annotation>
    fun accept(element: Element, processingEnv: ProcessingEnvironment): Boolean
    override fun run(roundEnv:RoundEnvironment, processingEnv: ProcessingEnvironment) {
        val elements = roundEnv.getElementsAnnotatedWith(annotation.java)
        for(element in elements){
            accept(element, processingEnv)
        }
    }

    override val requires: List<Processor> get() = emptyList()
}