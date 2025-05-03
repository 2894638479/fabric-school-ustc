package org.schoolustc.structureDsl.struct

import org.schoolustc.structureDsl.struct.scope.StructGenConfig

abstract class MyRoadStructInfo<T:MyRoadStruct>(id:String): MyStructInfo<T>(id){
    abstract val width: Int
    abstract val constructor:(config: StructGenConfig, length:Int)->T
}