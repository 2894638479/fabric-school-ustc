package org.schoolustc.items

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import org.schoolustc.packet.OPEN_QUESTION_GUI
import org.schoolustc.packet.QUESTION_CHOOSE
import org.schoolustc.packet.packetBuf
import org.schoolustc.structureDsl.itemMember

class QuestionItem(properties: Properties): Item(properties) {
    enum class Status{NOT_CHOSEN,CORRECT,WRONG,NOT_CHECKED}
    companion object {
        var ItemStack.subject by itemMember<String>("subject")
        var ItemStack.question by itemMember<String>("question")
        private var ItemStack.choicesStr by itemMember<String>("choices")
        var ItemStack.choices : List<String>
            get() = choicesStr.split('\n')
            set(value) { choicesStr = value.joinToString("\n") }
        var ItemStack.chosen by itemMember<Int>("chosen")
        var ItemStack.status by itemMember<Status>("status")
        val ItemStack.predicate get() = when(status){
            Status.NOT_CHOSEN -> 0f
            Status.CORRECT -> 0.5f
            Status.WRONG -> 1f
            Status.NOT_CHECKED -> 0f
        }
        val ItemStack.subjectTranslated get() = subject.let {
            Language.getInstance().getOrDefault("question_bank.subject.$it",it)
        }
        fun registerPacket() {
            ServerPlayNetworking.registerGlobalReceiver(QUESTION_CHOOSE){ minecraftServer, serverPlayer, serverGamePacketListenerImpl, friendlyByteBuf, packetSender ->
                val chosen = friendlyByteBuf.readVarInt()
                val hand = friendlyByteBuf.readEnum(InteractionHand::class.java)
                minecraftServer.execute {
                    val item = serverPlayer.getItemInHand(hand)
                    if(item.`is`(QUESTION_ITEM)){
                        item.chosen = chosen
                    }
                }
            }
        }
    }
    override fun appendHoverText(stack: ItemStack, level: Level?, list: MutableList<Component>, flag: TooltipFlag) {
        list += Component.literal("科目：${stack.subjectTranslated}")
        list += Component.literal("选项数：${stack.choices.size}")
        list += Component.literal(when(stack.status){
            Status.NOT_CHOSEN -> "未作答"
            Status.CORRECT -> "作答正确"
            Status.WRONG -> "作答错误"
            Status.NOT_CHECKED -> "未批改"
        })
    }

    override fun getDefaultInstance(): ItemStack {
        return super.getDefaultInstance().apply {
            question = "question"
            choices = listOf(
                "aaa","bbb","ccc","ddd"
            )
            chosen = -1
            subject = "physics"
            status = Status.NOT_CHOSEN
        }
    }

    override fun use(
        level: Level,
        player: Player,
        interactionHand: InteractionHand
    ): InteractionResultHolder<ItemStack> {
        val itemStack = player.getItemInHand(interactionHand)
        if(!level.isClientSide) {
            ServerPlayNetworking.send(
                player as ServerPlayer,
                OPEN_QUESTION_GUI,
                packetBuf().writeEnum(interactionHand).writeNbt(itemStack.orCreateTag)
            )
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide())
    }
}