package org.schoolustc.items

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.Potion
import net.minecraft.world.item.alchemy.PotionUtils
import net.minecraft.world.item.alchemy.Potions
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import org.schoolustc.items.MoneyCardItem.Companion.money
import org.schoolustc.structureDsl.from
import org.schoolustc.structureDsl.withChance
import org.schoolustc.trigger


class DrinkMachineBlock(prop:Properties):HorizontalDirectionalBlock(prop) {
    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(FACING, context.horizontalDirection.opposite)
    }
    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(FACING)
    }

    companion object {
        private val potionMap = mapOf(
            Potions.LUCK to 1,
            Potions.WATER to 1,
            Potions.HEALING to 1,
            Potions.LEAPING to 1,
            Potions.SLOW_FALLING to 1,
            Potions.NIGHT_VISION to 1,
            Potions.STRONG_STRENGTH to 2,
            Potions.STRENGTH to 1,
            Potions.LONG_FIRE_RESISTANCE to 2,
            Potions.STRONG_HEALING to 2,
            Potions.LONG_SWIFTNESS to 2,
            Potions.LONG_REGENERATION to 2,
        )
        private val Potion.stack get() = PotionUtils.setPotion(ItemStack(Items.POTION), this)
        private val Potion.splashStack get() = PotionUtils.setPotion(ItemStack(Items.SPLASH_POTION), this)
        private val Potion.lingeringStack get() = PotionUtils.setPotion(ItemStack(Items.LINGERING_POTION), this)
        private fun RandomSource.randDrinkMachineItem():ItemStack?{
            return withChance(0.9){ from(potionMap) }?.run { from(
                {stack} to 3,
                {splashStack} to 1,
                {lingeringStack} to 1
            )() } ?: from(
                GET_GPA_POTION_ITEM.defaultInstance to 1,
                null to 1,
            )
        }
    }
    override fun use(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        interactionHand: InteractionHand,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        val stack = player.getItemInHand(interactionHand)
        if(!stack.`is`(MONEY_CARD)) return InteractionResult.PASS
        val cost = 20
        if(stack.money < cost) return InteractionResult.FAIL
        stack.money -= 20
        level.random.randDrinkMachineItem()?.let {
            val direction = blockState.getValue(FACING).normal.run {
                Vec3(x.toDouble(),y.toDouble(),z.toDouble())
            }
            val pos = blockPos.center.add(direction.scale(0.51))
            val entity = ItemEntity(level,pos.x,pos.y,pos.z,it).apply {
                setDeltaMovement(direction.scale(0.5))
            }
            level.addFreshEntity(entity)
            (player as? ServerPlayer)?.trigger("school/buy_drink")
        } ?: (player as? ServerPlayer)?.trigger("school/buy_drink_failed")
        return InteractionResult.SUCCESS
    }
}