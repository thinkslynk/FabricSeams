package com.thinkslynk.fabric.annotations.registry

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@Repeatable
annotation class RegisterBlock(
        val namespace: String,
        val name: String
)