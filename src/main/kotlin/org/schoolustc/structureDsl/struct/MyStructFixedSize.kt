package org.schoolustc.structureDsl.struct

import org.schoolustc.structureDsl.struct.scope.StructGenConfig

abstract class MyStructFixedSize(
    info: MyStructFixedSizeInfo<*>,
    config: StructGenConfig
):MyStructWithConfig(info,info.size,config)