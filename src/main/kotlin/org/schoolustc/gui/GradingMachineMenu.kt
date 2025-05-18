package org.schoolustc.gui

import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import org.schoolustc.items.GRADING_MACHINE_BLOCK
import org.schoolustc.items.QUESTION_ITEM
import org.schoolustc.items.QuestionItem
import org.schoolustc.items.QuestionItem.Companion.choices
import org.schoolustc.items.QuestionItem.Companion.chosen
import org.schoolustc.items.QuestionItem.Companion.question
import org.schoolustc.items.QuestionItem.Companion.status
import org.schoolustc.items.QuestionItem.Companion.subject
import org.schoolustc.items.STUDENT_CARD
import org.schoolustc.items.StudentCardItem.Companion.subjectInfo
import org.schoolustc.packet.GRADING_MACHINE_GRADING
import org.schoolustc.questionbank.questionBankMap

class GradingMachineMenu(
    val containerId:Int,
    val inventory: Inventory,
    val access: ContainerLevelAccess
): AbstractContainerMenu(type,containerId) {
    companion object {
        val type = Registry.register(
            BuiltInRegistries.MENU,
            "grading_machine",
            MenuType(
                { id, inv -> GradingMachineMenu(id, inv, ContainerLevelAccess.NULL) },
                FeatureFlags.VANILLA_SET
            )
        )
        fun register() { type }
        fun registerPacket(){
            ServerPlayNetworking.registerGlobalReceiver(GRADING_MACHINE_GRADING){ minecraftServer: MinecraftServer, serverPlayer: ServerPlayer, serverGamePacketListenerImpl: ServerGamePacketListenerImpl, friendlyByteBuf: FriendlyByteBuf, packetSender: PacketSender ->
                val id = friendlyByteBuf.readVarInt()
                minecraftServer.execute {
                    val menu = serverPlayer.containerMenu ?: return@execute
                    if(id != menu.containerId) return@execute
                    val gradingMenu = (menu as? GradingMachineMenu) ?: return@execute
                    gradingMenu.grading()
                }
            }
        }
    }
    val studentCardContainer = SimpleContainer(1)
    val questionContainer = SimpleContainer(9)
    init {
        addSlot(Slot(studentCardContainer,0,25,35))
        for (j in 0..2){
            for(k in 0..2){
                addSlot(Slot(questionContainer,k + j * 3,98 + k * 18,17 + j * 18))
            }
        }
        for (j in 0..2) {
            for (k in 0..8) {
                addSlot(Slot(inventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18))
            }
        }
        for (j in 0..8) {
            addSlot(Slot(inventory, j, 8 + j * 18, 142))
        }
    }

    override fun quickMoveStack(player: Player, i: Int): ItemStack {
        val slot = slots[i]
        val stack = slot.item
        val copy = stack.copy()
        if(i == 0) {
            if (!moveItemStackTo(stack, 10,46, true)) return ItemStack.EMPTY
        }
        if(i in 1..9) {
            if(!moveItemStackTo(stack,10,46,true)) return ItemStack.EMPTY
        }
        if(i in 10..<46) {
            if(stack.`is`(STUDENT_CARD)){
                if(!moveItemStackTo(stack,0,1,false)) return ItemStack.EMPTY
            } else if(stack.`is`(QUESTION_ITEM) && stack.status == QuestionItem.Status.NOT_CHECKED) {
                if(!moveItemStackTo(stack,1,10,false)) return ItemStack.EMPTY
            } else return ItemStack.EMPTY
        }
        slot.setChanged()
        slotsChanged(slot.container)
        return copy
    }

    override fun stillValid(player: Player): Boolean {
        return stillValid(this.access, player, GRADING_MACHINE_BLOCK)
    }

    override fun removed(player: Player) {
        super.removed(player)
        clearContainer(player,studentCardContainer)
        clearContainer(player,questionContainer)
    }
    fun canGrading():Boolean{
        var canGrading = questionContainer.items.sumOf {
            if(it.isEmpty) 0.toInt()
            else if(it.`is`(QUESTION_ITEM) && it.status == QuestionItem.Status.NOT_CHECKED) 1
            else return false
        } > 0
        if(!studentCardContainer.getItem(0).`is`(STUDENT_CARD)) canGrading = false
        return canGrading
    }
    fun grading(){
        if(!canGrading()) return
        val studentCard = studentCardContainer.items[0]
        var info = studentCard.subjectInfo
        if(!studentCard.`is`(STUDENT_CARD)) return
        questionContainer.items.forEach {
            if(it.`is`(QUESTION_ITEM)){
                val subject = it.subject
                val questionBank = questionBankMap[subject] ?: return@forEach
                val questionItem = questionBank.questionList.firstOrNull { it1 ->
                    it1.question == it.question && it1.choices == it.choices
                } ?: return@forEach
                val correct = it.chosen == questionItem.answer
                it.status = if(correct) QuestionItem.Status.CORRECT else QuestionItem.Status.WRONG
                info = info.grading(subject,questionItem.difficulty,correct)
            }
        }
        studentCard.subjectInfo = info
    }
}