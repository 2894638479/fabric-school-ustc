package org.schoolustc.gui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.gui.screens.inventory.StonecutterScreen
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.schoolustc.fullId
import org.schoolustc.packet.cachedQuestionBank
import org.schoolustc.questionbank.QuestionBank

class TeachingTableMenuScreen(
    menu:TeachingTableMenu,
    inventory: Inventory,
    title: Component
): AbstractContainerScreen<TeachingTableMenu>(menu,inventory,title){
    companion object {
        val backPng1 = fullId("textures/gui/container/teaching_table_1.png")
        val backPng2 = fullId("textures/gui/container/teaching_table_2.png")
        val pngWidth1 = 116
        val pngWidth2 = 176
        val pngHeight = 166
        val fullPngWidth = pngWidth1 + pngWidth2
    }
    private val minX get() = leftPos - pngWidth1
    private var leftButton = Button.builder(Component.literal("")){}.build()
        set(value) { removeWidget(field);field = value;addRenderableWidget(field) }
    private var rightButton = Button.builder(Component.literal("")){}.build()
        set(value) { removeWidget(field);field = value;addRenderableWidget(field) }
    private var pages : List<List<QuestionBank.QuestionBankClient>> = listOf()
    private var subjectButtons:List<Button> = listOf()
        set(value) { field.forEach { removeWidget(it) };field = value;field.forEach { addRenderableWidget(it) } }
    private fun subjectString(name:String) = Language.getInstance().getOrDefault("question_bank.subject.$name",name)
    private var curBank:QuestionBank.QuestionBankClient? = null
        set(value) {
            field = value

        }
    private var curPage = 0
        set(value) {
            leftButton.active = false
            rightButton.active = false
            pages.ifEmpty {
                field = 0
                return
            }
            if(value < 0) field = 0
            if(value > pages.size - 1) field = pages.size - 1
            field = value
            subjectButtons = pages[field].mapIndexed { i, bank ->
                Button.builder(Component.literal(subjectString(bank.subject))){
                    subjectButtons.forEach { it.active = true }
                    it.active = false
                    curBank = bank
                }.bounds(minX + 8,topPos + 17+i*21,100,21).build().apply {
                    if(bank == curBank) active = false
                }
            }
            if(field != 0) leftButton.active = true
            if(field != pages.size - 1) rightButton.active = true
        }
    override fun init() {
        super.init()
        leftPos = (width - fullPngWidth) / 2 + pngWidth1
        topPos = (height - pngHeight) / 2
        titleLabelX = pngWidth1 + (pngWidth2 - font.width(title)) / 2
        leftButton = Button.builder(Component.literal("<")){curPage--}.bounds(minX + 8,topPos + 144,30,16).build()
        rightButton = Button.builder(Component.literal(">")){curPage++}.bounds(minX + 78,topPos + 144,30,16).build()
        curPage = curPage
    }
    private fun GuiGraphics.renderText(text:String,x:Int,y:Int) = drawString(font,text,x,y,0,false)
    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        renderBackground(guiGraphics)
        guiGraphics.blit(backPng1,minX,topPos, 0, 0, pngWidth1, pngHeight)
        guiGraphics.blit(backPng2,leftPos,topPos, 0, 0, pngWidth2, pngHeight)
        guiGraphics.renderText(Language.getInstance().getOrDefault("text.subject"),minX + 8,topPos + 6)
    }

    var banks = listOf<QuestionBank.QuestionBankClient>()

    fun updateBanks(){
        if(cachedQuestionBank != banks){
            banks = cachedQuestionBank
            pages = banks.chunked(6)
            curPage = curPage
        }
    }
    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        updateBanks()
        super.render(guiGraphics, mouseX,mouseY, partialTick)
        val text = "${curPage + 1}/${pages.size}"
        val width = font.width(text)
        guiGraphics.renderText(text,minX + 58 - width/2,topPos + 150)
        renderTooltip(guiGraphics, mouseX, mouseY)
    }
}