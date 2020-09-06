package com.thinkslynk.fabric.annotations.processor.definition

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import javax.lang.model.element.Element

data class Definition(
    val element:Element, // mostly to issue errors
    val name: String,
    val type: ClassName,
    val initializer: CodeBlock,
    val registryNamespace: String,
    val registryName: String
    )
{
    @Deprecated("plz")
    constructor(
    element:Element, // mostly to issue errors
    name: String,
    type: ClassName,
    callArgs: String = "",
    registryNamespace: String,
    registryName: String
    ):this(element, name, type, CodeBlock.of("%T(%L)",type,callArgs), registryNamespace, registryName)
}