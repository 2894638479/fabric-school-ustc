package org.schoolustc.structureDsl.struct

import org.schoolustc.structureDsl.Point
import org.schoolustc.structureDsl.struct.scope.StructGenConfig

abstract class MyRoadStruct(
    info: MyRoadStructInfo<*>,
    config: StructGenConfig,
    size:Point
):MyStructWithConfig(info,size,config)