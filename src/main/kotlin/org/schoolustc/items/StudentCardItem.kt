package org.schoolustc.items

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import org.schoolustc.items.MoneyCardItem.Companion.ownerName
import org.schoolustc.structureDsl.member
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

var Int.a by object :ReadWriteProperty<Any?,Int>{
    override fun getValue(thisRef: Any?, property: KProperty<*>) = 0
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {}
}
class StudentCardItem(properties: Properties):Item(properties) {
    companion object {
        var CompoundTag.gpa by member<Double>("GPA")
        var CompoundTag.score by member<Int>("score")
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