@file:Suppress("unused")
package com.thinkslynk.fabric.annotations.extensions

import com.thinkslynk.fabric.annotations.extensions.LogLevel.*
import javax.annotation.processing.Messager
import javax.lang.model.element.Element
import javax.tools.Diagnostic

val LOG_LEVEL = INFO

fun Messager.debug(s: String) = printMessage(kind(DEBUG), s)
fun Messager.debug(s: String, element: Element) = printMessage(kind(DEBUG), s, element)

fun Messager.info(s: String) = printMessage(kind(INFO), s)
fun Messager.info(s: String, element: Element) = printMessage(kind(INFO), s, element)

fun Messager.log(s: String) = printMessage(kind(LOG),s)
fun Messager.log(s: String, element: Element) = printMessage(kind(LOG),s, element)

private fun kind(level : LogLevel) = if(level >= LOG_LEVEL) Diagnostic.Kind.WARNING else Diagnostic.Kind.NOTE

fun Messager.log(level: LogLevel, s: String) = printMessage(kind(level), s)
fun Messager.log(level: LogLevel, s: String, element: Element) = printMessage(kind(level), s, element)

fun Messager.warn(s: String) = printMessage(Diagnostic.Kind.WARNING, s)
fun Messager.warn(s: String, element: Element) = printMessage(Diagnostic.Kind.WARNING, s, element)

fun Messager.error(s: String) = printMessage(Diagnostic.Kind.ERROR, s)
fun Messager.error(s: String, element: Element) = printMessage(Diagnostic.Kind.ERROR, s, element)


enum class LogLevel : Comparable<LogLevel>{
    DEBUG, INFO, LOG, NORMAL
}