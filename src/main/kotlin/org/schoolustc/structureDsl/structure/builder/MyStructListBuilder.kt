package org.schoolustc.structureDsl.structure.builder

import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.structure.StructureBuildScope

abstract class MyStructListBuilder<T:MyStruct> {
    abstract fun StructureBuildScope.build():List<T>
    @JvmName("build1")
    fun build(scope:StructureBuildScope) = scope.build()
}