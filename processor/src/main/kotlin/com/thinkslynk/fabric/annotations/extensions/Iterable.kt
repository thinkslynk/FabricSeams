package com.thinkslynk.fabric.annotations.extensions

fun <T,S,R>Iterable<T>.crossFlatMap(extract:(T)->Iterable<S>, transform:(List<S>)->R):List<R>{
    val result = mutableListOf<R>()
    val each = map{extract(it).toList()}
    if(each.any{it.isEmpty()})
        return emptyList()
    val idx = IntArray(each.size)
    val items = MutableList(each.size){each[it].first()}
    var i = 0
    while(true){
        result.add(transform(items))

        while(idx[i] + 1 == each[i].size){
            if(++i == idx.size){
                return result
            }
        }
        items[i] = each[i][++idx[i]]
        while(i > 0){
            idx[--i] = 0
            items[i] = each[i][0]
        }
    }
}