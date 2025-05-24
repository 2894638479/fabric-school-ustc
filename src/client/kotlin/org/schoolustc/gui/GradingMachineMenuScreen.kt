package org.schoolustc.gui

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.schoolustc.fullId
import org.schoolustc.packet.GRADING_MACHINE_GRADING
import org.schoolustc.packet.packetBuf

class GradingMachineMenuScreen(
    menu:GradingMachineMenu,
    inventory: Inventory,
    title: Component
): AbstractContainerScreen<GradingMachineMenu>(menu,inventory,title){
    companion object {
        val backPng = fullId("textures/gui/container/grading_machine.png")
    }
    override fun init() {
        super.init()
        titleLabelX = (imageWidth - font.width(title)) / 2
        menu.questionContainer.addListener { onSlotChanged() }
        menu.studentCardContainer.addListener { onSlotChanged() }
    }
    private var gradingButton: Button? = null
        set(value) {
            field?.let { removeWidget(it) }
            field = value
            field?.let { addRenderableWidget(it) }
        }
    private fun onSlotChanged(){
        val showButton = menu.canGrading()
        gradingButton = if (showButton) Button.builder(Component.literal("批改")){
            grading()
        }.bounds(leftPos + 53,topPos + 30,40,26).build() else null
    }
    override fun renderBg(guiGraphics: GuiGraphics, f: Float, i: Int, j: Int) {
        renderBackground(guiGraphics)
        guiGraphics.blit(backPng,leftPos,topPos, 0, 0, imageWidth, imageHeight)
    }
    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX,mouseY, partialTick)
        renderTooltip(guiGraphics, mouseX, mouseY)
    }
    private fun grading(){
        ClientPlayNetworking.send(GRADING_MACHINE_GRADING, packetBuf().writeVarInt(menu.containerId))
    }
}