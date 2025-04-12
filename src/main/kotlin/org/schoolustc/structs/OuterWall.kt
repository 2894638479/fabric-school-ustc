package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.IRON_BARS
import net.minecraft.world.level.block.Blocks.STONE_BRICKS
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.struct.MyStructInfo
import org.schoolustc.structureDsl.struct.StructBuildScope
import org.schoolustc.structureDsl.struct.StructGenConfig

class OuterWall(
    config: StructGenConfig,
    val length:Int
): MyStruct(Companion,config,Point(length,4,1)) {
    companion object : MyStructInfo<OuterWall>("wall"){
        override val defaultDirection = Direction2D.XPlus
        override fun loadTag(tag: CompoundTag) = OuterWall(
            tag.read("C"),
            tag.read("l")
        )
        override fun OuterWall.saveTag(tag: CompoundTag) {
            tag.write("C",config)
            tag.write("l",length)
        }
    }
    override fun StructBuildScope.build() {
        val state = IRON_BARS.connectedX

        inSurfView {
            STONE_BRICKS fill Area(0..<length,0..1,0..0)
            state fill Area(0..<length,2..3,0..0)
        }

        //填补高度差带来的漏洞
//        val h = (0..<length).map { Point(it,0,0).getFinalSurfacePos() }
//        for(i in 1..<length){
//            if(h[i].y < h[i-1].y) state fillSurf Point(i,4,0)
//        }
//        for(i in 0..<length-1){
//            if(h[i].y < h[i+1].y) state fillSurf Point(i,4,0)
//        }
    }
}