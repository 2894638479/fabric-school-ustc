package org.schoolustc.items

import kotlinx.serialization.json.Json
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.BlockHitResult
import org.schoolustc.gui.TeachingTableMenu
import org.schoolustc.questionbank.questionBankClientList

class TeachingTableBlock(prop:Properties):HorizontalDirectionalBlock(prop),ExtendedScreenHandlerFactory {
    override fun use(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        interactionHand: InteractionHand,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        if (level.isClientSide) return InteractionResult.SUCCESS
        player.openMenu(this)
        return InteractionResult.CONSUME
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(FACING, context.horizontalDirection.opposite)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(FACING)
    }

    override fun createMenu(i: Int, inventory: Inventory, player: Player): AbstractContainerMenu {
        return TeachingTableMenu(i,inventory, listOf())
    }

    override fun getDisplayName(): Component = Component.translatable("block.school-ustc.teaching_table")

    override fun writeScreenOpeningData(p0: ServerPlayer, p1: FriendlyByteBuf) {
        p1.writeByteArray(Json.encodeToString(questionBankClientList).encodeToByteArray())
    }
}