package org.schoolustc.gui

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.locale.Language
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

class QuestionScreen(
    val hand:InteractionHand,
    val item:ItemStack,
    val subjectStr:String
):Screen(Component.translatable("text.question")) {
    companion object {
        val backPng = fullId("textures/gui/question.png")
        fun registerPacket(){
            ClientPlayNetworking.registerGlobalReceiver(OPEN_QUESTION_GUI) { minecraft, clientPacketListener, friendlyByteBuf, packetSender ->
                val hand = friendlyByteBuf.readEnum(InteractionHand::class.java)
                val nbt = friendlyByteBuf.readNbt()
                val subject = String(friendlyByteBuf.readByteArray())
                val stack = ItemStack(QUESTION_ITEM).apply { tag = nbt }
                minecraft.execute {
                    minecraft.setScreen(QuestionScreen(hand, stack,subject))
                }
            }
        }
        const val backWidth = 256
        const val backHeight = 192
        const val topPadding = 32
        const val sidePadding = 32
        const val innerPadding = 10
        const val buttonSize = 20
        const val choicePadding = 16
        const val questionWidth = backWidth / 2 - sidePadding - innerPadding
        const val choicesWidth = backWidth / 2 - sidePadding - innerPadding - buttonSize
        const val buttonToText = 10
    }
    val fontHeight get() = font.lineHeight
    val leftPos get() = (width - backWidth) / 2
    val topPos get() = 2
    var questionText = listOf<FormattedCharSequence>()
    var choicesTexts = listOf(listOf<FormattedCharSequence>())
    fun updateItemText(){
        questionText = font.split(FormattedText.of(item.question),questionWidth)
        choicesTexts = item.choices.map {
            font.split(FormattedText.of(it),choicesWidth)
        }
    }
    var choiceButtons = listOf<Button>()
        set(value) {
            field.forEach { removeWidget(it) }
            field = value
            field.forEach { addRenderableWidget(it) }
        }
    fun updateChoiceButtons(){
        var offset = 0
        choiceButtons = choicesTexts.mapIndexed { i,it ->
            Button.builder(Component.literal(('A'+i).toString())){ choose(i) }
                .bounds(width/2 + innerPadding,topPos + topPadding + (fontHeight - buttonSize) / 2 + offset, buttonSize, buttonSize)
                .build().apply { offset += it.size * fontHeight + choicePadding }
        }
        updateChoice()
    }
    fun updateChoice(){
        val chosen = item.chosen
        choiceButtons.forEachIndexed { index, button ->
            button.active = index != chosen
        }
    }
    var closeButton = Button.builder(Component.literal("")){}.build()
        set(value) {
            removeWidget(field)
            field = value
            addRenderableWidget(field)
        }
    override fun init() {
        updateItemText()
        updateChoiceButtons()
        closeButton = Button.builder(CommonComponents.GUI_DONE) { _: Button? -> this.onClose() }
            .bounds(width / 2 - 100, 196, 200, 20).build()
    }
    private fun GuiGraphics.renderText(text: FormattedCharSequence,x:Int,y:Int) = drawString(font,text,x,y,0,false)
    private fun GuiGraphics.renderText(text: List<FormattedCharSequence>,x:Int,y:Int) = text.forEachIndexed { i, text ->
        drawString(font, text, x, y + i * fontHeight, 0, false)
    }
    private fun GuiGraphics.renderText(text:String,x:Int,y:Int) = drawString(font,text,x,y,0,false)
    val subjectString = "${Language.getInstance().getOrDefault("text.subject")}:" + subjectStr
    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        renderBackground(guiGraphics)
        guiGraphics.blit(backPng,leftPos,topPos, 0, 0, backWidth,backHeight)
        val subjectWidth = font.width(subjectString)
        guiGraphics.renderText(subjectString,(width - subjectWidth)/2,16)
        guiGraphics.renderText(questionText,leftPos + sidePadding,topPos + topPadding)
        var offset = 0
        choicesTexts.forEach {
            guiGraphics.renderText(it,width/2 + innerPadding + buttonSize + buttonToText,topPos + topPadding + offset)
            offset += it.size * fontHeight + choicePadding
        }
        super.render(guiGraphics, mouseX,mouseY,partialTick)
    }
    private fun choose(i:Int){
        if (!item.status.let { it == QuestionItem.Status.NOT_CHECKED || it == QuestionItem.Status.NOT_CHOSEN }) return
        if(i !in item.choices.indices) return
        item.chosen = i
        item.status = QuestionItem.Status.NOT_CHECKED
        updateChoice()
        ClientPlayNetworking.send(QUESTION_CHOOSE, packetBuf().writeVarInt(i).writeEnum(hand))
    }
}