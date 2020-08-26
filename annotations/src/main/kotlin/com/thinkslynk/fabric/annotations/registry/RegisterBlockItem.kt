package com.thinkslynk.fabric.annotations.registry

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class RegisterBlockItem(
        val namespace: String,
        val name: String,
        val itemGroup: String
)