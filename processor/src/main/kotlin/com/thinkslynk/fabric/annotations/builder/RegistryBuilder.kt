package com.thinkslynk.fabric.annotations.builder

import com.squareup.kotlinpoet.*
import com.thinkslynk.fabric.annotations.extensions.camelToUpperSnakeCase
import com.thinkslynk.fabric.annotations.extensions.crossFlatMap
import com.thinkslynk.fabric.annotations.processor.definition.Definition
import com.thinkslynk.fabric.annotations.processor.find.registry.ArgumentFinder
import java.nio.file.Path
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

class RegistryBuilder(packageName: String, className: String, val funcName: String, val registry: MemberName) {
    constructor(packageName: String, className: String, funcName: String, registry: String) : this(
        packageName,className,funcName, MemberName(registryClass,registry)
    )

    private val fileBuilder = FileSpec.builder(packageName, className)

    private val objectBuilder = TypeSpec.objectBuilder(className)

    private val registerBuilder = FunSpec.builder(funcName)
        .addModifiers(KModifier.PUBLIC)

    fun writeTo(folder: Path) {
        fileBuilder.addType(
            objectBuilder
                .addFunction(registerBuilder.build())
                .build()
        )
            .build()
            .writeTo(folder)

    }

    fun addDefinition(element: Definition) {
        val name = element.name
        val className = element.type
        objectBuilder
            .addProperty(
                PropertySpec.builder(name, className)
                    .addModifiers(KModifier.LATEINIT)
                    .mutable(true)
                    .setter(
                        FunSpec.setterBuilder()
                            .addModifiers(KModifier.PRIVATE)
                            .build()
                    )
                    .build()
            )
        registerBuilder
            .addStatement("""$name = %L""", element.initializer)
            .addStatement(
                """%T.register(%M, %T(%S, %P), $name)""",
                registryClass,
                registry,
                identifierClass,
                element.registryNamespace,
                element.registryName
            )
    }


    companion object {
        val identifierClass = ClassName("net.minecraft.util", "Identifier")
        val registryClass = ClassName("net.minecraft.util.registry", "Registry")

        val registryNameInjector = Regex("""\$(\d+)""")

        fun generateRegistryName(name: String, genArgs: List<Pair<ClassName, String>>): String =
            registryNameInjector.replace(name) {
                val index = it.groups[1]!!.value.toInt()
                val (enumClass, enumConst) = genArgs[index]
                val qualified = enumClass.nestedClass(enumConst).canonicalName

                "${'$'}{$qualified}"
            }

    }
}