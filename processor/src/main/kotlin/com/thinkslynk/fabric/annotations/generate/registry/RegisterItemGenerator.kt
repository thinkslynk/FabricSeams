package com.thinkslynk.fabric.annotations.generate.registry

import com.thinkslynk.fabric.annotations.find.registry.ArgumentFinder
import com.thinkslynk.fabric.annotations.find.registry.ItemFinder
import com.thinkslynk.fabric.annotations.registry.RegisterItem
import javax.lang.model.element.TypeElement

class RegisterItemGenerator: AbstractRegistryGenerator(CLASS_NAME, FUNC_NAME, "ITEM") {
    companion object {
        const val CLASS_NAME = "ItemRegistryGenerated"
        const val FUNC_NAME = "register"
    }

    override val typeElements: List<TypeElement>
        get() = ItemFinder.items

    override fun getAnnotationNamespace(typeElement: TypeElement): String =
        typeElement.getAnnotation(RegisterItem::class.java).namespace


    override fun getAnnotationName(typeElement: TypeElement): String =
        typeElement.getAnnotation(RegisterItem::class.java).name


    override val finders get() = listOf(ItemFinder, ArgumentFinder)
}