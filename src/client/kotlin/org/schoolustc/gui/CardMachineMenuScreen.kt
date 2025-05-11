package org.schoolustc.gui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.schoolustc.fullId

class CardMachineMenuScreen(
    menu:CardMachineMenu,
    inventory: Inventory,
    title: Component
): AbstractContainerScreen<CardMachineMenu>(menu,inventory,title) {
    companion object {
        val backPng = fullId("textures/gui/container/card_machine.png")
    }
    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        renderBackground(guiGraphics)
        guiGraphics.blit(backPng,leftPos,topPos, 0, 0, imageWidth, imageHeight)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX,mouseY, partialTick)
        renderTooltip(guiGraphics, mouseX, mouseY)
    }
}