package com.thinkslynk.fabric.annotations.processor.definition

import com.thinkslynk.fabric.annotations.processor.find.blockFinder
import com.thinkslynk.fabric.annotations.processor.find.itemFinder
import javax.lang.model.element.TypeElement

object BlockDefinitionProcessor:ParameterisedElementProcessor(blockFinder) {
    override fun getAnnotationNamespace(typeElement: TypeElement): String =
        typeElement.getAnnotation(blockFinder.annotation.java).namespace

    override fun getAnnotationName(typeElement: TypeElement): String =
        typeElement.getAnnotation(blockFinder.annotation.java).name
}


object ItemDefinitionProcessor:ParameterisedElementProcessor(itemFinder) {
    override fun getAnnotationNamespace(typeElement: TypeElement): String =
        typeElement.getAnnotation(itemFinder.annotation.java).namespace

    override fun getAnnotationName(typeElement: TypeElement): String =
        typeElement.getAnnotation(itemFinder.annotation.java).name
}