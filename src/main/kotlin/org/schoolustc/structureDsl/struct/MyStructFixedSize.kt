package org.schoolustc.structureDsl.struct

import org.schoolustc.structureDsl.structure.MyStructFixedAreaInfo

abstract class MyStructFixedSize(
    info: MyStructFixedAreaInfo<*>,
    config: StructGenConfig,
):MyStruct(info,config,info.size)