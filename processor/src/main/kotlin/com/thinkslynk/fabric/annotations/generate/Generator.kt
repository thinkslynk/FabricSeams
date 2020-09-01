package com.thinkslynk.fabric.annotations.generate

import com.thinkslynk.fabric.annotations.find.Finder
import java.io.File
import java.nio.file.Path
import javax.lang.model.element.Element
import kotlin.reflect.KClass

interface Generator {
    fun generate(folder: Path)
    val finders: Iterable<Finder>
}