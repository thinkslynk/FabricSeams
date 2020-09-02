package com.thinkslynk.fabric.annotations.builder

import com.squareup.kotlinpoet.*
import com.thinkslynk.fabric.annotations.extensions.camelToSnakeCase
import com.thinkslynk.fabric.annotations.extensions.crossFlatMap
import com.thinkslynk.fabric.annotations.find.registry.ArgumentFinder
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

    fun addItem(type: TypeElement, args: List<VariableElement>, annotationNamespace:String, annotationName:String) {
        val constName = formatPropertyName(type.simpleName.toString())
        val className = type.asClassName()
        if (PRE) {
            // ONE_K_STORAGE_PART_ITEM
            args.crossFlatMap({
                val (argType, items) = ArgumentFinder.values(it)
                items.map { argType to it }
            }) { genArgs ->
                val name = genArgs.joinToString(separator = "_", postfix = "_$constName") { it.second }

                addPropertyTo(
                    objectBuilder,
                    registerBuilder,
                    name,
                    className,
                    annotationNamespace,
                    generateRegistryName(annotationName, genArgs),
                    genArgs
                )

            }
        } else {
            // STORAGE_PART_ITEM.ONE_K
            val obj = TypeSpec.objectBuilder(constName)
            val objRegister = FunSpec.builder(funcName).addModifiers(KModifier.INTERNAL)
            args.crossFlatMap({
                val (argType, items) = ArgumentFinder.values(it)
                items.map { argType to it }
            }) { genArgs ->
                val name = genArgs.joinToString(separator = "_") { it.second }
                addPropertyTo(
                    obj,
                    objRegister,
                    name,
                    className,
                    annotationNamespace,
                    generateRegistryName(annotationName, genArgs),
                    genArgs
                )

            }
            objectBuilder
                .addType(
                    obj
                        .addFunction(objRegister.build())
                        .build()
                )
            registerBuilder.addStatement("$constName.$funcName()")
        }
    }

    fun addItem(type: TypeElement, annotationNamespace:String, annotationName:String) {
        val constName = formatPropertyName(type.simpleName.toString())
        val className = type.asClassName()
        objectBuilder
            .addProperty(
                PropertySpec.builder(constName, className)
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
            .addStatement("""$constName = %T()""", className)
            .addStatement(
                """%T.register(%M, %T(%S, %P), $constName)""",
                registryClass,
                registry,
                identifierClass,
                annotationNamespace,
                annotationName
            )
    }

    fun writeTo(folder: Path) {
        fileBuilder.addType(
            objectBuilder
                .addFunction(registerBuilder.build())
                .build()
        )
            .build()
            .writeTo(folder)

    }


    companion object {
        fun formatPropertyName(name: String): String {
            return name.camelToSnakeCase().toUpperCase()
        }

        // true : ONE_K_STORAGE_PART_ITEM
        // false: STORAGE_PART_ITEM.ONE_K
        const val PRE = true

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

        fun addPropertyTo(
            obj: TypeSpec.Builder,
            objRegister: FunSpec.Builder,
            name: String,
            className: ClassName,
            namespace: String,
            registryName: String,
            genArgs: List<Pair<ClassName, String>>
        ) {
            obj.addProperty(
                PropertySpec.builder(
                    name,
                    className
                ).addModifiers(KModifier.LATEINIT)
                    .mutable(true)
                    .setter(
                        FunSpec.setterBuilder()
                            .addModifiers(KModifier.PRIVATE)
                            .build()
                    )
                    .build()
            )


            objRegister.addStatement(
                """$name=%T(${genArgs.joinToString { it.first.nestedClass(it.second).canonicalName }})""",
                className
            ).addStatement(
                """%T.register(Registry.ITEM, %T(%S, %P), $name)""",
                registryClass,
                identifierClass,
                namespace,
                registryName
            )
        }
    }
}