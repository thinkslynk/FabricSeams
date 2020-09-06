package com.thinkslynk.fabric.annotations.extensions

import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import kotlin.contracts.contract

fun TypeMirror.isDeclared():Boolean {
    contract {
        returns(true) implies (this@isDeclared is DeclaredType)
    }
    return this.kind == TypeKind.DECLARED
}

fun Element.isClass():Boolean {
    contract {
        returns(true) implies (this@isClass is TypeElement)
    }
    return this.kind == ElementKind.CLASS
}

