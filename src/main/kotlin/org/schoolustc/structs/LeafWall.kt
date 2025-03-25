package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.struct.MyStructInfo
import org.schoolustc.structureDsl.struct.StructBuildScope
import org.schoolustc.structureDsl.struct.StructGenConfig

class LeafWall(
    config:StructGenConfig,
    val length:Int
):MyStruct(Companion,config, Point(length,2,1)) {
    companion object : MyStructInfo<LeafWall>("leafwall"){
        override val defaultDirection = Direction2D.X1
        override fun loadTag(tag: CompoundTag) = LeafWall(
            tag.getConfig(),
            tag.getInt("l")
        )
        override fun LeafWall.saveTag(tag: CompoundTag) {
            tag.putConfig(config)
            tag.putInt("l",length)
        }
    }

    override fun StructBuildScope.build() {
        val leaves = selector(mapOf(
            OAK_LEAVES.leafState(true) to 3f,
            SPRUCE_LEAVES.leafState(true) to 1f,
            AZALEA_LEAVES.leafState(true) to 2f,
            FLOWERING_AZALEA_LEAVES.leafState(true) to 2f
        ))
        leaves fillS Area(0..<length,1..1,0..0)
        STONE_BRICK_SLAB fillS Area(0..<length,2..2,0..0)
    }
}