package com.thinkslynk.fabric.annotations.processor

import javax.annotation.processing.*

interface Processor {
    val requires: List<Processor>
    fun run(roundEnv:RoundEnvironment, processingEnv: ProcessingEnvironment)
}