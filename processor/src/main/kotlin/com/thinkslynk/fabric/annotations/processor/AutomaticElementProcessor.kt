package com.thinkslynk.fabric.annotations.processor

import kotlin.reflect.KProperty

abstract class AutomaticElementProcessor<T>: SimpleElementProcessor<T> {
    private val required = mutableListOf<Processor>()
    override val requires: List<Processor> get() = required

    protected operator fun <S> SimpleElementProcessor<S>.provideDelegate(thisRef: SimpleElementProcessor<T>, property: KProperty<*>) : Delegate<S>{
        required.add(this)
        return Delegate(this)
    }

    class Delegate<S>(private val sep: SimpleElementProcessor<S>){
        operator fun getValue(thisRef:Any, property: KProperty<*>) = sep.elements
    }
}