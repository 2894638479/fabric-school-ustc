package org.schoolustc.structurePieces

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.IRON_BARS
import net.minecraft.world.level.block.Blocks.STONE_BRICKS
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.struct.MyStructInfo
import org.schoolustc.structureDsl.struct.StructBuilder
import org.schoolustc.structureDsl.struct.StructGenConfig

class OuterWallPiece(
    config: StructGenConfig,
    val length:Int
): MyStruct(Companion,config,Area(0..<length,0..3,0..0)) {
    init {
        if(length <= 0)
            error("wall length <= 0")
    }
    companion object : MyStructInfo<OuterWallPiece>("wall"){
        override fun loadTag(tag: CompoundTag) = OuterWallPiece(
            tag.getConfig(),
            tag.getInt("l")
        )
        override fun OuterWallPiece.saveTag(tag: CompoundTag) {
            tag.putConfig(config)
            tag.putInt("l",length)
        }
    }
    override fun StructBuilder.build() {
        STONE_BRICKS fillS Area(0..<length,0..1,0..0)
        IRON_BARS fillXS Area(0..<length,2..3,0..0)

        //填补高度差带来的漏洞
        val h = (0..<length).map { Point(it,0,0).finalSurfacePos }
        for(i in 1..<length){
            if(h[i].y < h[i-1].y) IRON_BARS fillXS Point(i,4,0)
        }
        for(i in 0..<length-1){
            if(h[i].y < h[i+1].y) IRON_BARS fillXS Point(i,4,0)
        }
    }
}