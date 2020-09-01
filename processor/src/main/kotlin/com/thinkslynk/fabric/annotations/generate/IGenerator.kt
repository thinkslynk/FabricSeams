package com.thinkslynk.fabric.annotations.generate

import java.io.File
import java.nio.file.Path
import javax.lang.model.element.Element
import kotlin.reflect.KClass

interface IGenerator {
    fun generate(elements: Collection<Element>, folder: Path)
    fun getSupportedAnnotationClass(): KClass<out Annotation>
}