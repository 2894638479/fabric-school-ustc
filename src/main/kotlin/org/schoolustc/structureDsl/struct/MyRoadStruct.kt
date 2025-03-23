package org.schoolustc.structureDsl.struct

import org.schoolustc.structureDsl.Point

abstract class MyRoadStruct(
    info: MyRoadStructInfo<*>,
    config: StructGenConfig,
    size:Point
):MyStruct(info,config,size)