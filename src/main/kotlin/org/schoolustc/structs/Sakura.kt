package org.schoolustc.structs

import net.minecraft.data.worldgen.features.TreeFeatures
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Blocks.BLACKSTONE
import net.minecraft.world.level.block.Blocks.PINK_PETALS
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.*
import org.schoolustc.structureDsl.structure.MyStructFixedSizeInfo


class Sakura(config:StructGenConfig):MyStructFixedSize(Companion,config) {
    companion object : MyStructFixedSizeInfo<Sakura>("sakura", Point(11,20,11)) {
        override fun loadTag(tag: CompoundTag) = Sakura(tag.getConfig())
        override fun Sakura.saveTag(tag: CompoundTag) = tag.putConfig(config)
        override val defaultDirection = Direction2D.XPlus
    }
    override fun StructBuildScope.build() {
        val treePoint = Point(5,0,5)
        placeTree(treePoint,rand from mapOf(
            TreeFeatures.CHERRY to 5f,
            TreeFeatures.CHERRY_BEES_005 to 1f
        ))
        for(i in 1..10){
            val pos = Point(rand from 2..8,1,rand from 2..8)
//            if(pos != treePoint && world.getBlockState(pos.getFinalSurfacePos().blockPos).isAir) {
                PINK_PETALS.state fillS pos
//            }
        }
    }
}