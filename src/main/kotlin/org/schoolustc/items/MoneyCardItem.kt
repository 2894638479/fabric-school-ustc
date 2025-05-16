package org.schoolustc.items

import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import org.schoolustc.structureDsl.itemMember
import java.time.ZonedDateTime


class MoneyCardItem(properties: Properties):Item(properties) {
    companion object {
        var ItemStack.money by itemMember<Int>("money")
        var ItemStack.ownerName by itemMember<String>("owner_name")
        var ItemStack.ownerUUID by itemMember<String>("owner_uuid")
        var ItemStack.createDate by itemMember<ZonedDateTime>("date")
    }
    override fun appendHoverText(stack: ItemStack, level: Level?, list: MutableList<Component>, flag: TooltipFlag) {
        list += Component.literal("所有者：${stack.ownerName}")
        list += Component.literal("余额：${stack.money}")
    }

    override fun getDefaultInstance(): ItemStack {
        return super.getDefaultInstance().apply {
            money = 0
            createDate = ZonedDateTime.now()
        }
    }
}