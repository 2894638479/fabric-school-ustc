package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.POLISHED_ANDESITE
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStructFixedSize
import org.schoolustc.structureDsl.struct.MyStructFixedSizeInfo
import org.schoolustc.structureDsl.struct.scope.StructBuildScopeWithConfig
import org.schoolustc.structureDsl.struct.scope.StructGenConfig

class WhitePavilion(config: StructGenConfig):MyStructFixedSize(Companion,config) {
    companion object : MyStructFixedSizeInfo<WhitePavilion>("white_pavilion", Point(12,7,11)){
        override val defaultDirection = Direction2D.XPlus
        override fun WhitePavilion.saveTag(tag: CompoundTag) = tag.write("C",config)
        override fun loadTag(tag: CompoundTag) = WhitePavilion(tag.read("C"))
    }

    override fun StructBuildScopeWithConfig.build() {
        inRelativeView {
            "white_pavilion" putA Point(0,0,0)
            POLISHED_ANDESITE fillUnder Area(0..<xSize,0..0,0..<zSize)
        }
    }
}