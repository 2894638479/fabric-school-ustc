package org.schoolustc.items

import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import org.schoolustc.items.MoneyCardItem.Companion.ownerName
import org.schoolustc.structureDsl.itemMember


class StudentCardItem(properties: Properties):Item(properties) {
    companion object {
        var ItemStack.gpa by itemMember<Double>("GPA")
        var ItemStack.score by itemMember<Int>("score")
    }
    override fun appendHoverText(stack: ItemStack, level: Level?, list: MutableList<Component>, flag: TooltipFlag) {
        list += Component.literal("所有者：${stack.ownerName}")
        list += Component.literal("GPA：${stack.gpa}")
        list += Component.literal("学分：${stack.score}")
    }

    override fun getDefaultInstance() = super.getDefaultInstance().apply {
        gpa = 0.0
        score = 0
    }
}