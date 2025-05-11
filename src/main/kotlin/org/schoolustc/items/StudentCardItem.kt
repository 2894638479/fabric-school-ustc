package org.schoolustc.items

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import org.schoolustc.items.MoneyCardItem.Companion.money
import org.schoolustc.items.MoneyCardItem.Companion.ownerName

class StudentCardItem(properties: Properties):Item(properties) {
    companion object {
        const val GPA = "gpa"
        const val SCORE = "score"
        var CompoundTag.gpa
            get() = getDouble(GPA)
            set(value) = putDouble(GPA,value)
        var CompoundTag.score
            get() = getInt(SCORE)
            set(value) = putInt(SCORE,value)
    }
    override fun appendHoverText(stack: ItemStack, level: Level?, list: MutableList<Component>, flag: TooltipFlag) {
        list += Component.literal("所有者：${stack.orCreateTag.ownerName}")
        list += Component.literal("GPA：${stack.orCreateTag.gpa}")
        list += Component.literal("学分：${stack.orCreateTag.score}")
    }

    override fun getDefaultInstance(): ItemStack {
        return super.getDefaultInstance().apply {
            orCreateTag.gpa = 0.0
            orCreateTag.score = 0
        }
    }
}