package com.thinkslynk.fabric.annotations.extensions

val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()
val snakeRegex = "_[a-zA-Z]".toRegex()

// String extensions
fun String.camelToSnakeCase(): String {
    return camelRegex.replace(this) {
        "_${it.value}"
    }.toLowerCase()
}

fun String.camelToUpperSnakeCase(): String {
    return camelRegex.replace(this) {
        "_${it.value}"
    }.toUpperCase()
}

fun String.snakeToLowerCamelCase(): String {
    return snakeRegex.replace(this) {
        it.value.replace("_","")
            .toUpperCase()
    }
}

fun String.snakeToUpperCamelCase(): String {
    return this.snakeToLowerCamelCase().capitalize()
}