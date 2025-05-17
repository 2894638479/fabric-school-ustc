package org.schoolustc.gui

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack
import org.schoolustc.items.TEACHING_TABLE_BLOCK

class TeachingTableMenu(
    val containerId:Int,
    val inventory: Inventory,
    val access: ContainerLevelAccess
): AbstractContainerMenu(type,containerId) {
    companion object {
        val type = Registry.register(
            BuiltInRegistries.MENU,
            "teaching_table",
            MenuType(
                {id,inv -> TeachingTableMenu(id,inv,ContainerLevelAccess.NULL)},
                FeatureFlags.VANILLA_SET
            )
        )
        fun register(){ type }
    }

    private val studentCardContainer = SimpleContainer(1)
    override fun stillValid(player: Player): Boolean {
        return stillValid(this.access, player, TEACHING_TABLE_BLOCK)
    }

    override fun quickMoveStack(player: Player, i: Int): ItemStack {
        return ItemStack.EMPTY
    }

    override fun removed(player: Player) {
        clearContainer(player,studentCardContainer)
    }
}