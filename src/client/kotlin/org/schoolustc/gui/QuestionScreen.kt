package org.schoolustc.gui

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.BookViewScreen.BOOK_LOCATION
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.ItemStack
import org.schoolustc.items.QUESTION_ITEM
import org.schoolustc.items.QuestionItem.Companion.choices
import org.schoolustc.items.QuestionItem.Companion.chosen
import org.schoolustc.items.QuestionItem.Companion.question
import org.schoolustc.packet.OPEN_QUESTION_GUI
import org.schoolustc.packet.QUESTION_CHOOSE
import org.schoolustc.packet.packetBuf

class QuestionScreen(val hand:InteractionHand,val item:ItemStack):Screen(Component.translatable("text.question")) {
    companion object {
        val backPng = BOOK_LOCATION
        fun registerPacket(){
            ClientPlayNetworking.registerGlobalReceiver(OPEN_QUESTION_GUI) { minecraft, clientPacketListener, friendlyByteBuf, packetSender ->
                val hand = friendlyByteBuf.readEnum(InteractionHand::class.java)
                val nbt = friendlyByteBuf.readNbt()
                val stack = ItemStack(QUESTION_ITEM).apply { tag = nbt }
                minecraft.execute {
                    minecraft.setScreen(QuestionScreen(hand, stack))
                }
            }
        }
    }
    val backWidth = 192
    val backHeight = 192

    override fun init() {
        val button = Button.builder(CommonComponents.GUI_DONE) { _: Button? -> this.onClose() }
            .bounds(this.width / 2 - 100, 196, 200, 20).build()
        this.addRenderableWidget(button)
    }
    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val leftPos = (width - backWidth) / 2
        val topPos = 2
        renderBackground(guiGraphics)
        guiGraphics.blit(backPng,leftPos,topPos, 0, 0, backWidth,backHeight)
        guiGraphics.drawString(font,item.question,leftPos + 36,topPos + 32,0,false)
        super.render(guiGraphics, mouseX,mouseY,partialTick)
    }
    private fun choose(i:Int){
        if(i !in item.choices.indices) return
        item.chosen = i
        ClientPlayNetworking.send(QUESTION_CHOOSE, packetBuf().writeVarInt(i).writeEnum(hand))
    }
}