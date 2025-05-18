package org.schoolustc.gui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class GradingMachineMenuScreen(
    menu:GradingMachineMenu,
    inventory: Inventory,
    title: Component
): AbstractContainerScreen<GradingMachineMenu>(menu,inventory,title){
    override fun renderBg(guiGraphics: GuiGraphics, f: Float, i: Int, j: Int) {
    }
    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX,mouseY, partialTick)
        renderTooltip(guiGraphics, mouseX, mouseY)
    }
}