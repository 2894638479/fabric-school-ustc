package org.schoolustc.structureDsl.struct.builder

import org.schoolustc.structureDsl.struct.MyStruct

interface MyStructBuilder<T:MyStruct> {
    fun build():T
}