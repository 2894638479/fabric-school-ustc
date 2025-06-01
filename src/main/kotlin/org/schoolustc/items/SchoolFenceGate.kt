package org.schoolustc.items

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.FenceGateBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.WoodType
import net.minecraft.world.phys.BlockHitResult
import org.schoolustc.trigger

class SchoolFenceGate(prop:Properties):FenceGateBlock(prop, WoodType.OAK) {
    override fun use(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        interactionHand: InteractionHand,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        if(!player.getItemInHand(interactionHand).`is`(MONEY_CARD) && !blockState.getValue(OPEN)) {
            (player as? ServerPlayer)?.trigger("school/fence_protect")
            return InteractionResult.FAIL
        }
        return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult)
    }
}