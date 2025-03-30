package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStructFixedSize
import org.schoolustc.structureDsl.struct.StructBuildScope
import org.schoolustc.structureDsl.struct.StructGenConfig
import org.schoolustc.structureDsl.structure.MyStructFixedSizeInfo

class Building(
    config: StructGenConfig,
    val height:Int,
    val flatTop:Boolean
):MyStructFixedSize(Companion,config) {
    companion object : MyStructFixedSizeInfo<Building>("building", Point(11,100,11)){
        override fun loadTag(tag: CompoundTag) = Building(
            tag.getConfig(),
            tag.getInt("h"),
            tag.getBoolean("f")
        )
        override fun Building.saveTag(tag: CompoundTag) {
            tag.putConfig(config)
            tag.putInt("h",height)
            tag.putBoolean("f",flatTop)
        }
        override val defaultDirection = Direction2D.ZPlus
    }

    override fun StructBuildScope.build() {
        infix fun String.put(y:Int) = put(Point(0,y,0))
        "building_base" put 0
        for(i in 1..height){
            "building_middle" put i*4
        }
        "building_top" put height*4 + 4
        val topest = if(flatTop) "building_topest_flat" else "building_topest"
        topest put height*4 + 8
    }
}