package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.*
import org.schoolustc.structureDsl.struct.scope.StructBuildScopeWithConfig
import org.schoolustc.structureDsl.struct.scope.StructGenConfig
import kotlin.math.min

class Street(
    config: StructGenConfig,
    val length : Int
): MyRoadStruct(Companion,config,Point(7,1,length)) {
    companion object : MyRoadStructInfo<Street>("street"){
        override val defaultDirection = Direction2D.ZPlus
        override fun loadTag(tag: CompoundTag) = Street(
            tag.read("C"),
            tag.read("l")
        )
        override fun Street.saveTag(tag: CompoundTag) {
            tag.write("C",config)
            tag.write("l",length)
        }
        override val width get() = 7
        override val period get() = 10
        override val constructor get() = ::Street
    }
    override fun StructBuildScopeWithConfig.build() = inSurfView {
        AIR fill Area(0..6,1..2,0..<length)
        GRAY_CONCRETE fill Area(0..6,0..0,0..<length)
        val first = length - length%5
        for(i in 0..<first step 5){
            WHITE_CONCRETE fill Area(3..3,0..0,i..i+2)
        }
        val last = length - first
        WHITE_CONCRETE fill Area(3..3,0..0,first..first + min(last,2))
    }
}