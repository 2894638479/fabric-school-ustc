package org.schoolustc.structureDsl.structure

//较为均匀地分割区间
fun IntRange.split(maxLength:Int):List<Int>{
    val full = last - first
    val count = full / (maxLength + 1) + 1
    val each = full / count
    val rest = full % count
    val length = List(count){ if(it < rest) each + 1 else each }
    val result = mutableListOf<Int>()
    length.forEachIndexed { i, it ->
        result += if(i == 0) first + it
        else result[i-1] + it
    }
    return listOf(first) + result
}