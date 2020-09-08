package com.thinkslynk.fabric.annotations.extensions

import javax.lang.model.element.*

fun TypeElement.findConstructors() = enclosedElements
        // constructors
    .filter { it.kind == ElementKind.CONSTRUCTOR }
    .cast<List<ExecutableElement>>()
        // only public ones
    .filter { it.modifiers.contains(Modifier.PUBLIC) }

fun <T>Any?.cast() = this as T