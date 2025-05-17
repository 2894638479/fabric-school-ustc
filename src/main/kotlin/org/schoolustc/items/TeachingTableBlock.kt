package org.schoolustc.items

import kotlinx.serialization.json.Json
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
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
import org.schoolustc.gui.TeachingTableMenu
import org.schoolustc.packet.SYNC_CONTAINER_QUESTION_BANK
import org.schoolustc.packet.packetBuf
import org.schoolustc.questionbank.questionBankClientList

class TeachingTableBlock(prop:Properties):HorizontalDirectionalBlock(prop) {
    override fun use(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        interactionHand: InteractionHand,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        if (level.isClientSide) return InteractionResult.SUCCESS
        val serverPlayer = player as? ServerPlayer ?: return InteractionResult.SUCCESS
        val id = player.openMenu(blockState.getMenuProvider(level, blockPos))
        if(id.isPresent){
            ServerPlayNetworking.send(
                serverPlayer,
                SYNC_CONTAINER_QUESTION_BANK,
                packetBuf().writeVarInt(id.asInt)
                    .writeByteArray(Json.encodeToString(questionBankClientList).encodeToByteArray())
            )
        }
        return InteractionResult.CONSUME
    }
    override fun getMenuProvider(blockState: BlockState, level: Level, blockPos: BlockPos): MenuProvider {
        return SimpleMenuProvider({i,inv,p -> TeachingTableMenu(i,inv, ContainerLevelAccess.create(level, blockPos)) }, Component.translatable("block.school-ustc.teaching_table"))
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(FACING, context.horizontalDirection.opposite)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(FACING)
    }
}