package com.thinkslynk.fabric.annotations.processor.definition

import com.thinkslynk.fabric.annotations.processor.Processor
import com.thinkslynk.fabric.annotations.processor.SimpleElementProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment

abstract class DefinitionProcessor<T>(private val parent: SimpleElementProcessor<T>) :
    SimpleElementProcessor<Definition> {
    override val requires: List<Processor> = listOf(parent)
    private val definitions: MutableList<Definition> = mutableListOf()
    override val elements: List<Definition> = definitions

    override fun run(roundEnv: RoundEnvironment, processingEnv: ProcessingEnvironment) {
        for (element in parent.elements) {
            definitions.addAll(process(element, processingEnv))
        }
    }

    protected abstract fun process(item: T, processingEnv: ProcessingEnvironment): Iterable<Definition>
}

