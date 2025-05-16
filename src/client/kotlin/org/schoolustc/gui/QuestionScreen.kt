package org.schoolustc.gui

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.BookViewScreen
import net.minecraft.client.gui.screens.inventory.BookViewScreen.BOOK_LOCATION
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FormattedText
import net.minecraft.util.FormattedCharSequence
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.ItemStack
import org.schoolustc.fullId
import org.schoolustc.items.QUESTION_ITEM
import org.schoolustc.items.QuestionItem
import org.schoolustc.items.QuestionItem.Companion.choices
import org.schoolustc.items.QuestionItem.Companion.chosen
import org.schoolustc.items.QuestionItem.Companion.question
import org.schoolustc.items.QuestionItem.Companion.status
import org.schoolustc.packet.OPEN_QUESTION_GUI
import org.schoolustc.packet.QUESTION_CHOOSE
import org.schoolustc.packet.packetBuf

class QuestionScreen(val hand:InteractionHand,val item:ItemStack):Screen(Component.translatable("text.question")) {
    companion object {
        val backPng = fullId("textures/gui/question.png")
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
    val backWidth = 256
    val backHeight = 192
    val topPadding = 32
    val sidePadding = 32
    val innerPadding = 10
    val buttonSize = 20
    val fontHeight get() = font.lineHeight
    val questionWidth = backWidth/2 - sidePadding - innerPadding
    val choicesWidth = backWidth/2 - sidePadding - innerPadding - buttonSize
    var questionText = listOf<FormattedCharSequence>()
    var choicesTexts = listOf(listOf<FormattedCharSequence>())
    fun updateItemText(){
        questionText = font.split(FormattedText.of(item.question),questionWidth)
        choicesTexts = item.choices.map {
            font.split(FormattedText.of(it),choicesWidth)
        }
    }
    override fun init() {
        val button = Button.builder(CommonComponents.GUI_DONE) { _: Button? -> this.onClose() }
            .bounds(this.width / 2 - 100, 196, 200, 20).build()
        this.addRenderableWidget(button)
        updateItemText()
    }
    private fun GuiGraphics.renderText(text: FormattedCharSequence,x:Int,y:Int) = drawString(font,text,x,y,0,false)
    private fun GuiGraphics.renderText(text: List<FormattedCharSequence>,x:Int,y:Int) = text.forEachIndexed { i, text ->
        drawString(font, text, x, y + i * fontHeight, 0, false)
    }
    private fun GuiGraphics.renderText(text:String,x:Int,y:Int) = drawString(font,text,x,y,0,false)
    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val leftPos = (width - backWidth) / 2
        val topPos = 2
        renderBackground(guiGraphics)
        guiGraphics.blit(backPng,leftPos,topPos, 0, 0, backWidth,backHeight)

        guiGraphics.renderText(questionText,leftPos + sidePadding,topPos + topPadding)
        var offset = 0
        choicesTexts.forEach {
            guiGraphics.renderText(it,leftPos + backWidth/2 + sidePadding + buttonSize,topPos + topPadding + offset)
            offset += it.size * fontHeight
        }
        super.render(guiGraphics, mouseX,mouseY,partialTick)
    }
    private fun choose(i:Int){
        if(i !in item.choices.indices) return
        item.chosen = i
        item.status = QuestionItem.Status.NOT_CHOSEN
        ClientPlayNetworking.send(QUESTION_CHOOSE, packetBuf().writeVarInt(i).writeEnum(hand))
    }
}