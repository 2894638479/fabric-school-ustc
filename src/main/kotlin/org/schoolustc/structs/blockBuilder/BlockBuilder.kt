package org.schoolustc.structs.blockBuilder

import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.structure.builder.MyStructListBuilder

abstract class BlockBuilder(val para: BlockBuilderPara): MyStructListBuilder<MyStruct>() {
    val area get() = para.area
    val nextToWalls get() = para.nextToWalls
    val nextToSplitter get() = para.nextToSplitter
}