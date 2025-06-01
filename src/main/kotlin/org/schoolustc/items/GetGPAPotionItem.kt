package org.schoolustc.items

import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.ThrownPotion
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.PotionItem
import net.minecraft.world.level.Level
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult
import org.schoolustc.items.MoneyCardItem.Companion.ownerName
import org.schoolustc.items.MoneyCardItem.Companion.ownerUUID
import org.schoolustc.items.StudentCardItem.Companion.subjectInfo
import org.schoolustc.trigger

class GetGPAPotionItem(prop:Properties) : PotionItem(prop) {
    override fun use(
        level: Level,
        player: Player,
        interactionHand: InteractionHand
    ): InteractionResultHolder<ItemStack>? {
        val itemStack = player.getItemInHand(interactionHand)
        if (!level.isClientSide) {
            val thrownPotion = object: ThrownPotion(level, player){
                override fun onHitEntity(entityHitResult: EntityHitResult) {
                    if(!level.isClientSide) {
                        val playerHit = (entityHitResult.entity as? ServerPlayer) ?: return
                        playerHit.inventory.items.firstOrNull {
                            it.`is`(STUDENT_CARD) && it.ownerUUID == playerHit.stringUUID
                        }?.let {
                            val item = Items.PAPER.defaultInstance.apply {
                                setHoverName(Component.literal("GPA of ${it.ownerName}: ${String.format("%.3f",it.subjectInfo.gpa)}"))
                            }
                            playerHit.drop(item,true,false)
                            (player as? ServerPlayer)?.trigger("school/get_gpa")
                        }
                        this.discard()
                    }
                }

                override fun onHit(hitResult: HitResult) {
                    super.onHit(hitResult)
                }
            }
            thrownPotion.item = itemStack
            thrownPotion.shootFromRotation(player, player.xRot, player.yRot, -20.0f, 0.5f, 1.0f)
            level.addFreshEntity(thrownPotion)
        }
        player.awardStat(Stats.ITEM_USED[this])
        if (!player.abilities.instabuild) {
            itemStack.shrink(1)
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide())
    }
}