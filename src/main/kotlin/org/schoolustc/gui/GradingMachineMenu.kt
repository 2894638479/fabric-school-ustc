package org.schoolustc.gui

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack
import org.schoolustc.items.CARD_MACHINE_BLOCK
import org.schoolustc.items.GRADING_MACHINE_BLOCK

class GradingMachineMenu(
    val containerId:Int,
    val inventory: Inventory,
    val access: ContainerLevelAccess
): AbstractContainerMenu(type,containerId) {
    companion object {
        val type = Registry.register(
            BuiltInRegistries.MENU,
            "grading_machine",
            MenuType(
                { id, inv -> GradingMachineMenu(id, inv, ContainerLevelAccess.NULL) },
                FeatureFlags.VANILLA_SET
            )
        )
        fun register() { type }
    }

    override fun quickMoveStack(player: Player, i: Int): ItemStack {
        return ItemStack.EMPTY
    }

    override fun stillValid(player: Player): Boolean {
        return stillValid(this.access, player, GRADING_MACHINE_BLOCK)
    }

    override fun removed(player: Player) {
        super.removed(player)
    }
}