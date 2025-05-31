package org.schoolustc.items

import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.schoolustc.MyRegister
import org.schoolustc.structureDsl.read
import org.schoolustc.structureDsl.write

class FoodMachineBlockEntity(
    blockPos: BlockPos,
    blockState: BlockState
):BlockEntity(type, blockPos, blockState) {
    companion object {
        val type = FabricBlockEntityTypeBuilder.create(::FoodMachineBlockEntity, FOOD_MACHINE_BLOCK).build()
        fun register() = MyRegister.registerBlockEntity("food_machine", type)
    }
    var item = Items.AIR
    var cost = 0
    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        tag.write("i",item)
        tag.write("c",cost)
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)
        item = tag.read("i")
        cost = tag.read("c")
    }
}