package org.schoolustc.structs

import net.minecraft.data.worldgen.features.TreeFeatures
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyRoadStruct
import org.schoolustc.structureDsl.struct.MyRoadStructInfo
import org.schoolustc.structureDsl.struct.scope.StructBuildScopeWithConfig
import org.schoolustc.structureDsl.struct.scope.StructGenConfig
import kotlin.math.min

class CherryStreet(
    config: StructGenConfig,
    val length:Int
):MyRoadStruct(Companion,config, Point(width,1,length)) {
    companion object : MyRoadStructInfo<CherryStreet>("street_cherry"){
        override val defaultDirection = Direction2D.ZPlus
        override val constructor = ::CherryStreet
        override val width = 13
        override fun CherryStreet.saveTag(tag: CompoundTag) {
            tag.write("C",config)
            tag.write("l",length)
        }
        override fun loadTag(tag: CompoundTag) = CherryStreet(
            tag.read("C"),
            tag.read("l")
        )
    }

    override fun StructBuildScopeWithConfig.build() = inSurfView {
        fun plantCherry(x:Int,z:Int){
            DIRT fill Point(x,1,z)
            val pos = Point(x,2,z)
            val res = TreeFeatures.CHERRY_BEES_005 plant pos
            if(res == false) {
                CHERRY_SAPLING fill pos
            }
        }
        AIR fill Area(3..9,1..2,0..<length)
        GRAY_CONCRETE fill Area(3..9,0..0,0..<length)
        val first = length - length%5
        for(i in 0..<first step 5){
            WHITE_CONCRETE fill Area(6..6,0..0,i..i+2)
        }
        val last = length - first
        WHITE_CONCRETE fill Area(6..6,0..0,first..first + min(last,2))
        SMOOTH_STONE_SLAB fill Area(0..2,1..1,0..<length)
        SMOOTH_STONE_SLAB fill Area(10..12,1..1,0..<length)

        for(z in 2..length - 3 step 8){
            plantCherry(1,z)
            plantCherry(11,z)
        }
    }
}