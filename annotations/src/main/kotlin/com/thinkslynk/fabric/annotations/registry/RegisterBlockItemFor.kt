package com.thinkslynk.fabric.annotations.registry

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class RegisterBlockItemFor(
    val kClass:KClass<*> // KClass<out net.minecraft.block.Block would be nice
)