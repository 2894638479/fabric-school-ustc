package org.schoolustc.items

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import org.schoolustc.structureDsl.member


class MoneyCardItem(properties: Properties):Item(properties) {
    companion object {
        var CompoundTag.money by member<Int>("money")
        var CompoundTag.ownerName by member<String>("owner_name")
        var CompoundTag.ownerUUID by member<String>("owner_uuid")
    }
    override fun appendHoverText(stack: ItemStack, level: Level?, list: MutableList<Component>, flag: TooltipFlag) {
        list += Component.literal("所有者：${stack.orCreateTag.ownerName}")
        list += Component.literal("余额：${stack.orCreateTag.money}")
    }

    override fun getDefaultInstance(): ItemStack {
        return super.getDefaultInstance().apply { orCreateTag.money = 0 }
    }
}