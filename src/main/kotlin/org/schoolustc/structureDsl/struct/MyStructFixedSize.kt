package org.schoolustc.structureDsl.struct

import org.schoolustc.structureDsl.structure.MyStructFixedSizeInfo

abstract class MyStructFixedSize(
    info: MyStructFixedSizeInfo<*>,
    config: StructGenConfig,
):MyStruct(info,config,info.size)