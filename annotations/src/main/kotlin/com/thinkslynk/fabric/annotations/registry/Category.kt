package com.thinkslynk.fabric.annotations.registry

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD, AnnotationTarget.CLASS, AnnotationTarget.VALUE_PARAMETER)
annotation class Category(vararg val name:String)