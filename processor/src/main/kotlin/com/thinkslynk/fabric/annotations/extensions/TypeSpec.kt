package com.thinkslynk.fabric.annotations.extensions

import com.squareup.kotlinpoet.*
import javax.lang.model.element.Element

fun TypeSpec.Builder.addDefaultProperties(
    elements: Collection<Element>,
    formatName: (name: String) -> String
): TypeSpec.Builder {
    elements.forEach {element->

        val targetClassName = element.simpleName.toString()
        val propName = formatName(targetClassName)
        if (element.isMethod()) {
            val returnType = element.returnType

            this.addProperty(
                    PropertySpec.builder(propName, returnType.asTypeName(), KModifier.PUBLIC)
                            .initializer("$targetClassName()")
                            .build()
            )
        } else {
            this.addProperty(
                    PropertySpec.builder(propName, element.asType().asTypeName(), KModifier.PUBLIC)
                            .initializer("$targetClassName()")
                            .build()
            )
        }
    }

    return this
}

fun TypeSpec.Builder.addInitializedProperties(
    elements: Collection<Element>,
    formatName: (name: String) -> String,
    initializer: (element: Element) -> String,
    type: TypeName? = null
): TypeSpec.Builder {
    var b = this
    elements.forEach {
        val targetClassName = it.simpleName.toString()
        val propName = formatName(targetClassName)
        val t: TypeName = type ?: it.asType().asTypeName()

        b = b.addProperty(
            PropertySpec.builder(propName, t, KModifier.PUBLIC)
                .initializer(initializer(it))
                .build()
        )
    }

    return b
}

fun TypeSpec.Builder.addLateInitProperties(
    elements: Collection<Element>,
    formatName: (name: String) -> String,
    type: TypeName? = null,
    typeGenerator: ((element: Element) -> TypeName)? = null
): TypeSpec.Builder {
    var b = this
    elements.forEach {
        val targetClassName = it.simpleName.toString()
        val propName = formatName(targetClassName)
        val t: TypeName = type ?:
        typeGenerator?.invoke(it) ?:
        it.asType().asTypeName()

        b = b.addProperty(
            PropertySpec.builder(propName, t, KModifier.PUBLIC, KModifier.LATEINIT)
                .mutable()
                .build()
        )
    }

    return b
}