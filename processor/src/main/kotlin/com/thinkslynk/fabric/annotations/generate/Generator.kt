package com.thinkslynk.fabric.annotations.generate

import com.thinkslynk.fabric.annotations.processor.Processor
import java.nio.file.Path
import javax.annotation.processing.ProcessingEnvironment

interface Generator {
    fun generate(folder: Path, processingEnv: ProcessingEnvironment)
    val finders: Iterable<Processor>
}