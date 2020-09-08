package com.thinkslynk.fabric.annotations.extensions

import javax.lang.model.element.*
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
    return this.kind.isClass // CLASS or ENUM
}

fun Element.isEnum():Boolean {
    contract {
        returns(true) implies (this@isEnum is TypeElement)
    }
    return this.kind == ElementKind.ENUM // CLASS or ENUM
}

fun Element.isEnumConstant():Boolean {
    contract {
        returns(true) implies (this@isEnumConstant is Element) // Which type is this?
    }
    return this.kind == ElementKind.ENUM_CONSTANT // CLASS or ENUM
}

fun Element.isMethod():Boolean {
    contract {
        returns(true) implies (this@isMethod is ExecutableElement)
    }
    return this.kind == ElementKind.METHOD
}

fun Element.isPackage():Boolean {
    contract {
        returns(true) implies (this@isPackage is PackageElement)
    }
    return this.kind == ElementKind.PACKAGE
}