package com.thinkslynk.fabric.annotations.builder

import com.squareup.kotlinpoet.*
import com.thinkslynk.fabric.annotations.processor.definition.Definition
import java.nio.file.Path

class RegistryBuilder(packageName: String, className: String, funcName: String, val registry: MemberName) {
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
        objectBuilder
            .addProperty(
                PropertySpec.builder(element.name, element.type)
                    .initializer(element.initializer)
                    .build()
            )
        registerBuilder
            .addStatement(
                """%T.register(%M, %T(%S, %P), %L)""",
                registryClass,
                registry,
                identifierClass,
                element.registryNamespace,
                element.registryName,
                element.name
            )
    }


    companion object {
        val identifierClass = ClassName("net.minecraft.util", "Identifier")
        val registryClass = ClassName("net.minecraft.util.registry", "Registry")

        private val registryNameInjector = Regex("""\$(\d+)""")

        fun generateRegistryName(name: String, genArgs: List<Pair<ClassName, String>>): String =
            registryNameInjector.replace(name) {
                val index = it.groups[1]!!.value.toInt()
                val (enumClass, enumConst) = genArgs[index]
                val qualified = enumClass.nestedClass(enumConst).canonicalName

                "${'$'}{$qualified}"
            }

    }
}