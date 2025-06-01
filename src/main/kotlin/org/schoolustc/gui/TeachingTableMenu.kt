package org.schoolustc.gui

import kotlinx.serialization.json.Json
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import org.schoolustc.items.QUESTION_ITEM
import org.schoolustc.items.QuestionItem
import org.schoolustc.items.QuestionItem.Companion.choices
import org.schoolustc.items.QuestionItem.Companion.chosen
import org.schoolustc.items.QuestionItem.Companion.difficulty
import org.schoolustc.items.QuestionItem.Companion.question
import org.schoolustc.items.QuestionItem.Companion.status
import org.schoolustc.items.QuestionItem.Companion.subject
import org.schoolustc.items.STUDENT_CARD
import org.schoolustc.items.StudentCardItem.Companion.subjectInfo
import org.schoolustc.items.TEACHING_TABLE_BLOCK
import org.schoolustc.packet.TEACHING_TABLE_FINISH_LEARN
import org.schoolustc.packet.TEACHING_TABLE_START_LEARN
import org.schoolustc.questionbank.QuestionBank
import org.schoolustc.questionbank.questionBankMap
import org.schoolustc.trigger


class TeachingTableMenu(
    val containerId:Int,
    val inventory: Inventory,
    val clientQuestionBank:List<QuestionBank.QuestionBankClient>
): AbstractContainerMenu(type,containerId) {
    companion object {
        val type = Registry.register(
            BuiltInRegistries.MENU,
            "teaching_table",
            ExtendedScreenHandlerType{ i: Int, inventory: Inventory, friendlyByteBuf: FriendlyByteBuf ->
                TeachingTableMenu(i,inventory,Json.decodeFromString(String(friendlyByteBuf.readByteArray())))
            }
        )
        fun register(){ type }
        fun registerPacket(){
            ServerPlayNetworking.registerGlobalReceiver(TEACHING_TABLE_START_LEARN){ minecraftServer, serverPlayer, serverGamePacketListenerImpl, friendlyByteBuf, packetSender ->
                val id = friendlyByteBuf.readVarInt()
                val subject = String(friendlyByteBuf.readByteArray())
                minecraftServer.execute {
                    if(id == serverPlayer.containerMenu?.containerId){
                        val menu = (serverPlayer.containerMenu as? TeachingTableMenu) ?: return@execute
                        val item = menu.slots[0].item
                        if(item.`is`(STUDENT_CARD)){
                            item.subjectInfo = item.subjectInfo.startLearn(subject)
                            val questionItems = questionBankMap[subject] ?: return@execute
                            questionItems.questionList.forEach {
                                val questionItem = ItemStack(QUESTION_ITEM).apply {
                                    question = it.question
                                    difficulty = it.difficulty
                                    choices = it.choices
                                    this.subject = subject
                                    chosen = -1
                                    status = QuestionItem.Status.NOT_CHOSEN
                                }
                                if(!serverPlayer.inventory.add(questionItem)){
                                    serverPlayer.drop(questionItem,false)
                                }
                            }
                        }
                    }
                }
            }
            ServerPlayNetworking.registerGlobalReceiver(TEACHING_TABLE_FINISH_LEARN){ minecraftServer: MinecraftServer, serverPlayer: ServerPlayer, serverGamePacketListenerImpl: ServerGamePacketListenerImpl, friendlyByteBuf: FriendlyByteBuf, packetSender: PacketSender ->
                val id = friendlyByteBuf.readVarInt()
                val subject = String(friendlyByteBuf.readByteArray())
                minecraftServer.execute {
                    if(id == serverPlayer.containerMenu?.containerId) {
                        val menu = (serverPlayer.containerMenu as? TeachingTableMenu) ?: return@execute
                        val item = menu.slots[0].item
                        if(item.`is`(STUDENT_CARD)){
                            item.subjectInfo = item.subjectInfo.finishLearn(subject){
                                serverPlayer.trigger("school/finish_lesson")
                            }
                        }
                    }
                }
            }
        }
    }

    val studentCardContainer = SimpleContainer(1)
    init {
        addSlot(Slot(studentCardContainer,0,25,35))
        for (j in 0..2) {
            for (k in 0..8) {
                this.addSlot(Slot(inventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18))
            }
        }
        for (j in 0..8) {
            this.addSlot(Slot(inventory, j, 8 + j * 18, 142))
        }
    }
    override fun stillValid(player: Player): Boolean {
        return player.inventory.stillValid(player)
    }

    override fun quickMoveStack(player: Player, i: Int): ItemStack {
        val slot = slots[i]
        val stack = slot.item
        val copy = stack.copy()
        if(i == 0) {
            if (!moveItemStackTo(stack, 1, 37, true)) return ItemStack.EMPTY
        }
        if(i in 1..<37) {
            if (!stack.`is`(STUDENT_CARD)) return ItemStack.EMPTY
            if (!moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY
        }
        slot.setChanged()
        slotsChanged(slot.container)
        return copy
    }

    override fun removed(player: Player) {
        super.removed(player)
        clearContainer(player,studentCardContainer)
    }

    override fun slotsChanged(container: Container) {
        super.slotsChanged(container)
        sendAllDataToRemote()
    }
}