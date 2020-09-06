package com.thinkslynk.fabric.annotations.processor

interface SimpleElementProcessor<T>: Processor {
    val elements:List<T>
}

