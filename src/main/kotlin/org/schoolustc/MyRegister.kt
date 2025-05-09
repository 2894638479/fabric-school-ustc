package org.schoolustc

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block

object MyRegister {
    fun registerBlockItem(id:String,block: Block,itemTab:ResourceKey<CreativeModeTab>?): Pair<Block, Item> {
        val blockRegistered = Registry.register(BuiltInRegistries.BLOCK, fullId(id),block)
        val item = BlockItem(blockRegistered,Item.Properties())
        val itemRegistered = registerItem(id,item,itemTab)
        return blockRegistered to itemRegistered
    }
    fun registerItem(id:String,item:Item,itemTab:ResourceKey<CreativeModeTab>?): Item {
        val itemRegistered = Registry.register(BuiltInRegistries.ITEM, fullId(id),item)
        if (itemTab != null) {
            ItemGroupEvents.modifyEntriesEvent(itemTab).register(ItemGroupEvents.ModifyEntries {
                it.addAfter(itemRegistered, itemRegistered)
            })
        }
        return itemRegistered
    }
}