package org.schoolustc.items

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.BlockHitResult
import org.schoolustc.gui.CardMachineMenu


class CardMachineBlock(prop:Properties) :HorizontalDirectionalBlock(prop) {
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
        return SimpleMenuProvider({i,inv,p -> CardMachineMenu(i,inv, ContainerLevelAccess.create(level, blockPos)) }, Component.translatable("block.school-ustc.card_machine"))
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(FACING, context.horizontalDirection.opposite)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(FACING)
    }
}