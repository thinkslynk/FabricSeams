package com.thinkslynk.fabric.annotations.generate

import com.thinkslynk.fabric.annotations.find.Finder
import java.nio.file.Path
import javax.annotation.processing.ProcessingEnvironment

interface Generator {
    fun generate(folder: Path, processingEnv: ProcessingEnvironment)
    val finders: Iterable<Finder>
}