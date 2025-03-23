package org.schoolustc.structureDsl.structure.builder

import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.structure.StructureBuildScope

interface MyStructListBuilder<T:MyStruct> {
    fun StructureBuildScope.build():List<T>
}