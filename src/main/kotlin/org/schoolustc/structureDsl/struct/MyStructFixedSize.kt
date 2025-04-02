package org.schoolustc.structureDsl.struct

abstract class MyStructFixedSize(
    info: MyStructFixedSizeInfo<*>,
    config: StructGenConfig,
):MyStruct(info,config,info.size)