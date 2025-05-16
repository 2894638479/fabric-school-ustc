package org.schoolustc

import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.gui.screens.MenuScreens
import org.schoolustc.gui.CardMachineMenu
import org.schoolustc.gui.CardMachineMenuScreen
import org.schoolustc.gui.QuestionScreen

object SchoolUSTCClient : ClientModInitializer {
	override fun onInitializeClient() {
		MenuScreens.register(CardMachineMenu.type,::CardMachineMenuScreen)
		QuestionScreen.registerPacket()
	}
}