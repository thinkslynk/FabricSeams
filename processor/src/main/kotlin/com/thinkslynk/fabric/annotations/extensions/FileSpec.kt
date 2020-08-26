package com.thinkslynk.fabric.annotations.extensions

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import javax.lang.model.element.Element
import javax.lang.model.element.QualifiedNameable

fun FileSpec.Builder.addImports(elements: Collection<Element>): FileSpec.Builder {
    var b = this
    elements.forEach {
        // Check what the annotation was attached to
        val qualified = it as QualifiedNameable
        val target = qualified.qualifiedName.toString()
        val targetClassName = target.substringAfterLast(".")
        val targetPackage = target.substringBeforeLast(".")

        b = b.addImport(targetPackage, targetClassName)
    }
    return b
}

fun FileSpec.Builder.addTypes(types: Iterable<TypeSpec>): FileSpec.Builder {
    var b = this
    types.forEach {
        b = b.addType(it)
    }
    return b
}