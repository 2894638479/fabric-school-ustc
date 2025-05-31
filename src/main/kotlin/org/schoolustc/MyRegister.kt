package org.schoolustc

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType

object MyRegister {
    fun registerBlockItem(id:String,block: Block,itemTab:ResourceKey<CreativeModeTab>): Pair<Block, Item> {
        val blockRegistered = registerBlock(id, block)
        val item = BlockItem(blockRegistered,Item.Properties())
        val itemRegistered = registerItem(id,item,itemTab)
        return blockRegistered to itemRegistered
    }
    fun registerItem(id:String,item:Item,itemTab:ResourceKey<CreativeModeTab>) = registerItem(id, item).apply{
        ItemGroupEvents.modifyEntriesEvent(itemTab).register(ItemGroupEvents.ModifyEntries {
            it.addAfter(defaultInstance,defaultInstance)
        })
    }
    fun registerBlock(id:String,block: Block) = Registry.register(BuiltInRegistries.BLOCK, fullId(id),block)
    fun registerItem(id:String,item:Item) = Registry.register(BuiltInRegistries.ITEM, fullId(id),item)
    fun <T:BlockEntity> registerBlockEntity(id:String, blockEntityType: BlockEntityType<T>) = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, fullId(id),blockEntityType)
}