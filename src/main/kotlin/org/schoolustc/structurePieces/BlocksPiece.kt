package org.schoolustc.structurePieces

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Block
import org.schoolustc.structureDsl.*

class BlocksPiece(
    config: StructGenConfig,
    val block: Block,
    val area: AreaProg
):MyStruct(Companion,config,area.boundingArea){
    companion object : MyStructInfo<BlocksPiece>("blocks") {
        override fun loadTag(tag: CompoundTag) = BlocksPiece(
            tag.getConfig(),
            tag.getBlock(),
            tag.getAreaProg()
        )
        override fun BlocksPiece.saveTag(tag: CompoundTag) {
            tag.putConfig(config)
            tag.putBlock(block)
            tag.putAreaProg(area)
        }
        override fun StructBuilder.build(struct:BlocksPiece) {
            struct.block fill struct.area
        }
    }
}