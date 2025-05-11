package org.schoolustc.items

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.schoolustc.gui.CardMachineMenu

class CardMachineBlock(prop:Properties) : Block(prop) {
    override fun use(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        interactionHand: InteractionHand,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        if (level.isClientSide) return InteractionResult.SUCCESS
        player.openMenu(blockState.getMenuProvider(level, blockPos))
        return InteractionResult.CONSUME
    }

    override fun getMenuProvider(blockState: BlockState, level: Level, blockPos: BlockPos): MenuProvider {
        return SimpleMenuProvider({i,inv,p -> CardMachineMenu(i,inv, ContainerLevelAccess.create(level, blockPos)) }, Component.translatable("container.crafting"))
    }
}