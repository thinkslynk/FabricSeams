package com.thinkslynk.fabric.annotations.processor.definition

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.asClassName
import com.thinkslynk.fabric.annotations.FabricProcessor
import com.thinkslynk.fabric.annotations.extensions.*
import com.thinkslynk.fabric.annotations.generate.registry.RegisterBlockGenerator
import com.thinkslynk.fabric.annotations.generate.registry.RegisterItemGroupGenerator
import com.thinkslynk.fabric.annotations.processor.AutomaticElementProcessor
import com.thinkslynk.fabric.annotations.processor.find.blockItemForFinder
import com.thinkslynk.fabric.annotations.registry.RegisterBlockItem
import com.thinkslynk.fabric.helpers.AnnotationHelpers
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract


private fun check(processingEnv: ProcessingEnvironment){
    processingEnv.messager.debug(Thread.currentThread().stackTrace.joinToString())
}

object BlockItemDefinitionProcessor : AutomaticElementProcessor<Definition>() {

    private val blocks by BlockDefinitionProcessor
    private val offeredBlockItems by blockItemForFinder

    private val definitions: MutableList<Definition> = mutableListOf()
    override val elements: List<Definition> get() = definitions

    override fun run(roundEnv: RoundEnvironment, processingEnv: ProcessingEnvironment) {
        // leaving debug here as this is not considered fixed
        @Suppress("LocalVariableName")
        val HACK_ENABLED = true
        check(processingEnv)

        processingEnv.messager.debug("extracting items")
        val items = offeredBlockItems.associateByTo(mutableMapOf()) {
            processingEnv.messager.debug("extracting item: $it",it)
            val itemAnnotation = it.getAnnotation(blockItemForFinder.annotation.java)
            if(HACK_ENABLED){
                processingEnv.messager.debug("using hack to extract class name: $itemAnnotation", it)
                val annotationString = itemAnnotation.toString()
                val match = HACKY_REGEX.find(annotationString)
                if(match == null){
                    processingEnv.messager.error("RegisterBlockItemFor can only be placed on named classes", it)
                    error("RegisterBlockItemFor can only be placed on named classes")
                }
                match.groups[1]!!.value
            }
            else {
                processingEnv.messager.debug("got annotation: $itemAnnotation", it)
                processingEnv.messager.debug("got annotation: $itemAnnotation: ${itemAnnotation.kClass}", it)
                processingEnv.messager.debug(
                    "got annotation: $itemAnnotation: ${itemAnnotation.kClass.qualifiedName}",
                    it
                )
                itemAnnotation!!.kClass.qualifiedName ?: run {
                    processingEnv.messager.error("RegisterBlockItemFor can only be placed on named classes", it)
                    error("RegisterBlockItemFor can only be placed on named classes")
                }
            }
        }

        processingEnv.messager.debug("checking blocks")
        NextBlock@
        for (block in blocks) {
            processingEnv.messager.debug("checking block: $block",block.element)
            block.element as TypeElement // TODO this is ugly and might break
            // verify if this needs an item
            val annotation = block.element.getAnnotation(RegisterBlockItem::class.java)
                ?: continue@NextBlock // TODO warn about blocks without items?
            processingEnv.messager.debug("block has item",block.element)

            var currClass = block.element.asType()
            var className: ClassName? = null
            processingEnv.messager.debug("searching supperclasses",block.element)
            while (currClass.isDeclared()) {
                val element = currClass.asElement()
                processingEnv.messager.debug("found a class:",element)
                if (!element.isClass()) {
                    processingEnv.messager.error("expected super to be a class, but got type:$currClass element:$element")
                    continue@NextBlock
                }
                val item = items[element.qualifiedName.toString()]
                if (item == null) {
                    currClass = element.superclass
                    continue
                }
                // we found an item registered for the block
                processingEnv.messager.debug("found item for class",item)
                className = item.asClassName()
                break
            }
            if (className == null) {
                processingEnv.messager.debug("no item found, using BlockItem",block.element)
                className = BLOCK_ITEM_CLASS
            }

            // FIXME we require the Blocks to be registered first, as I'm only settign the values
            // in the register function
            val group = if (AnnotationHelpers.ItemGroup.contains(annotation.itemGroup)) {
                MemberName(ITEM_GROUP_CLASS, annotation.itemGroup)
            } else {
                MemberName(ITEM_GROUP_REGISTER_CLASS, annotation.itemGroup)
            }
            definitions += Definition(
                element = block.element, //TODO: Figure out which one of the two i want
                name = block.name,
                type = className,

                // TODO: This requires a public constructor in the form of (Block,Settings)
                initializer = CodeBlock.of("""%T(%T.%L,%T().group(%M))""", className, BLOCK_REGISTER_CLASS,block.name,
                    ITEM_SETTINGS_CLASS,group),
                registryName = annotation.name or block.registryName,
                registryNamespace = annotation.namespace or block.registryNamespace
            )
            processingEnv.messager.debug("added definition",block.element)
        }
    }


    private val BLOCK_ITEM_CLASS = ClassName("net.minecraft.item", "BlockItem")
    private val BLOCK_REGISTER_CLASS = ClassName(FabricProcessor.GENERATED_PACKAGE, RegisterBlockGenerator.CLASS_NAME)
    private val ITEM_CLASS = ClassName("net.minecraft.item","Item")
    private val ITEM_SETTINGS_CLASS = ITEM_CLASS.nestedClass("Settings")
    private val ITEM_GROUP_CLASS = ClassName("net.minecraft.item","ItemGroup")
    private val ITEM_GROUP_REGISTER_CLASS = ClassName(FabricProcessor.GENERATED_PACKAGE, RegisterItemGroupGenerator.CLASS_NAME)
    private val HACKY_REGEX = Regex("""\(kClass=([^)]+?).class\)""")
}

infix fun String.or(other: String) = if (this.isEmpty()) other else this