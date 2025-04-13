package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.IRON_BARS
import net.minecraft.world.level.block.Blocks.STONE_BRICKS
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStructInfo
import org.schoolustc.structureDsl.struct.MyStructWithConfig
import org.schoolustc.structureDsl.struct.scope.StructBuildScopeWithConfig
import org.schoolustc.structureDsl.struct.scope.StructGenConfig

class OuterWall(
    config: StructGenConfig,
    val length:Int
): MyStructWithConfig(Companion,Point(length,4,1),config) {
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
    override fun StructBuildScopeWithConfig.build() {
        inSurfView {
            val state = IRON_BARS.state.connected(Direction2D.XPlus,Direction2D.XMin)
            STONE_BRICKS fill Area(0..<length,0..1,0..0)
            state fill Area(0..<length,2..3,0..0)

            //填补高度差带来的漏洞
            val h = List(length){ it to height(it,0) }
            h.zipWithNext { (x1,y1), (x2,y2) ->
                if(y1 > y2) IRON_BARS.state.connected(Direction2D.XMin) fill Point(x2,4,0)
                if(y1 < y2) IRON_BARS.state.connected(Direction2D.XPlus) fill Point(x1,4,0)
            }
        }
    }
}