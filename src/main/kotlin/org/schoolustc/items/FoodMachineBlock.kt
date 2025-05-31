package org.schoolustc.items

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import org.schoolustc.items.MoneyCardItem.Companion.money

class FoodMachineBlock(prop:Properties):EntityBlock,Block(prop) {
    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState) =
        FoodMachineBlockEntity(blockPos, blockState)
    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(FACING, context.horizontalDirection.opposite)
    }
    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(FACING)
    }

    override fun use(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        interactionHand: InteractionHand,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        val entity = level.getBlockEntity(blockPos)
        if(entity !is FoodMachineBlockEntity) return InteractionResult.PASS
        val stack = player.getItemInHand(interactionHand)
        if(!stack.`is`(MONEY_CARD)) {
            if(player.isCreative){
                if(stack.`is`(entity.item)) entity.cost++
                else {
                    entity.item = stack.item
                    entity.cost = 0
                }
                return InteractionResult.SUCCESS
            } else return InteractionResult.PASS
        }
        if(stack.money < entity.cost) return InteractionResult.FAIL
        stack.money -= entity.cost
        val itemStack = entity.item.defaultInstance
        val direction = blockState.getValue(FACING).normal.run {
            Vec3(x.toDouble(),y.toDouble(),z.toDouble())
        }
        val itemPos = blockPos.center.add(direction.scale(0.51))
        val itemEntity = ItemEntity(level,itemPos.x,itemPos.y,itemPos.z,itemStack).apply {
            setDeltaMovement(direction.scale(0.5))
        }
        level.addFreshEntity(itemEntity)
        return InteractionResult.SUCCESS
    }
}