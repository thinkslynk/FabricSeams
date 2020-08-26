package com.thinkslynk.fabric.annotations.registry

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class RegisterBlockEntity(
    val namespace: String,
    val name: String,
    val blocks: Array<String>
)