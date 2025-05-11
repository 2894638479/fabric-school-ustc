package org.schoolustc.items

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level


class MoneyCardItem(properties: Properties):Item(properties) {
    companion object {
        const val MONEY = "money"
        const val OWNER_NAME = "owner_name"
        const val OWNER_UUID = "owner_uuid"
        var CompoundTag.money
            get() = getInt(MONEY)
            set(value) = putInt(MONEY,value)
        var CompoundTag.ownerName
            get() = getString(OWNER_NAME)
            set(value) = putString(OWNER_NAME,value)
        var CompoundTag.ownerUUID
            get() = getString(OWNER_UUID)
            set(value) = putString(OWNER_UUID,value)
    }
    override fun appendHoverText(stack: ItemStack, level: Level?, list: MutableList<Component>, flag: TooltipFlag) {
        list += Component.literal("所有者：${stack.orCreateTag.ownerName}")
        list += Component.literal("余额：${stack.orCreateTag.money}")
    }

    override fun getDefaultInstance(): ItemStack {
        return super.getDefaultInstance().apply { orCreateTag.money = 0 }
    }
}