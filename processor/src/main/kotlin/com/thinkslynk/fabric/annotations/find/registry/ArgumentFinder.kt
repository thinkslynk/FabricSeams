package com.thinkslynk.fabric.annotations.find.registry

import com.thinkslynk.fabric.annotations.find.Finder
import com.thinkslynk.fabric.annotations.registry.RegisterArgument
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import kotlin.reflect.KClass

object ArgumentFinder : Finder {
    override val annotation: KClass<out Annotation> = RegisterArgument::class

    override fun accept(element: Element, processingEnv: ProcessingEnvironment): Boolean {
        TODO("Implement accept")
    }
}