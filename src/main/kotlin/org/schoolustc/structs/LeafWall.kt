package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStructInfo
import org.schoolustc.structureDsl.struct.MyStructWithConfig
import org.schoolustc.structureDsl.struct.scope.StructBuildScopeWithConfig
import org.schoolustc.structureDsl.struct.scope.StructGenConfig

class LeafWall(
    config: StructGenConfig,
    val length:Int
):MyStructWithConfig(Companion, Point(length,1,1),config) {
    companion object : MyStructInfo<LeafWall>("leafwall"){
        override val defaultDirection = Direction2D.XPlus
        override fun loadTag(tag: CompoundTag) = LeafWall(
            tag.read("C"),
            tag.read("l")
        )
        override fun LeafWall.saveTag(tag: CompoundTag) {
            tag.write("C",config)
            tag.write("l",length)
        }
    }

    override fun StructBuildScopeWithConfig.build() {
        val leaves = {
            rand from mapOf(
                OAK_LEAVES to 3f,
                SPRUCE_LEAVES to 1f,
                AZALEA_LEAVES to 2f,
                FLOWERING_AZALEA_LEAVES to 2f
            )
        }

        inSurfView {
            { leaves().leafState(true) } fill Area(0..<length,1..1,0..0)
        }
    }
}