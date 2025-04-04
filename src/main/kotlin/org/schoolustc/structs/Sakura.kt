package org.schoolustc.structs

import net.minecraft.data.worldgen.features.TreeFeatures
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.PINK_PETALS
import net.minecraft.world.level.block.state.properties.BlockStateProperties.FLOWER_AMOUNT
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStructFixedSize
import org.schoolustc.structureDsl.struct.StructBuildScope
import org.schoolustc.structureDsl.struct.StructGenConfig
import org.schoolustc.structureDsl.struct.MyStructFixedSizeInfo


class Sakura(config:StructGenConfig):MyStructFixedSize(Companion,config) {
    companion object : MyStructFixedSizeInfo<Sakura>("sakura", Point(11,20,11)) {
        override fun loadTag(tag: CompoundTag) = Sakura(tag.read("C"))
        override fun Sakura.saveTag(tag: CompoundTag) = tag.write("C",config)
        override val defaultDirection = Direction2D.XPlus
    }
    override fun StructBuildScope.build() {
        val treePoint = Point(5,0,5)
        TreeFeatures.CHERRY_BEES_005 place treePoint
        for(i in 1..10){
            val pos = Point(rand from 1..9,1,rand from 1..9)
            if(world.getBlockState(pos.getFinalSurfacePos().blockPos).isAir) {
                PINK_PETALS.state.setValue(FLOWER_AMOUNT,rand from mapOf(
                    1 to 5f,
                    2 to 3f,
                    3 to 1f,
                    4 to 1f
                )) fillS pos
            }
        }
    }
}