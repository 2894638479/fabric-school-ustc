package org.schoolustc.gui

import net.minecraft.client.gui.GuiGraphics
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
    val splitPos get() = leftPos + pngWidth1

    override fun init() {
        super.init()
        leftPos = (width - fullPngWidth) / 2
        topPos = (height - pngHeight) / 2
        titleLabelX = pngWidth1 + (pngWidth2 - font.width(title)) / 2
    }
    private fun GuiGraphics.renderText(text:String,x:Int,y:Int) = drawString(font,text,x,y,0,false)
    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        renderBackground(guiGraphics)
        guiGraphics.blit(backPng1,leftPos,topPos, 0, 0, pngWidth1, pngHeight)
        guiGraphics.blit(backPng2,splitPos,topPos, 0, 0, pngWidth2, pngHeight)
        guiGraphics.renderText(Language.getInstance().getOrDefault("text.subject"),leftPos + 8,topPos + 6)
    }

    var banks = listOf<QuestionBank.QuestionBankClient>()

    fun updateBanks(){
        if(cachedQuestionBank != banks){
            banks = cachedQuestionBank
        }
    }
    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        updateBanks()
        super.render(guiGraphics, mouseX,mouseY, partialTick)
        renderTooltip(guiGraphics, mouseX, mouseY)
    }
}