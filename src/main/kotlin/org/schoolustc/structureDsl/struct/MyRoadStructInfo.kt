package org.schoolustc.structureDsl.struct

abstract class MyRoadStructInfo<T:MyRoadStruct>(id:String): MyStructInfo<T>(id){
    abstract val width: Int
    abstract val period: Int
    abstract val constructor:(config:StructGenConfig, length:Int)->T
}