package com.thinkslynk.fabric.annotations.generate

import java.io.File
import javax.lang.model.element.Element
import kotlin.reflect.KClass

interface IGenerator {
    fun generate(elements: Collection<Element>, folder: File)
    fun getSupportedAnnotationClass(): KClass<out Annotation>
}