package com.thinkslynk.fabric.annotations.extensions

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import javax.lang.model.element.*


fun FileSpec.Builder.addImports(elements: Collection<Element>): FileSpec.Builder {
    var b = this
    elements.forEach {

        var enclosing = it
        while (enclosing.kind != ElementKind.PACKAGE) {
            enclosing = enclosing.enclosingElement
        }
        val packageElement = enclosing as PackageElement

        b = b.addImport(packageElement.toString(), it.simpleName.toString())
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