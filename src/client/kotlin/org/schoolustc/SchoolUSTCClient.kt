package org.schoolustc

import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.gui.screens.MenuScreens
import org.schoolustc.gui.CardMachineMenu
import org.schoolustc.gui.CardMachineMenuScreen

object SchoolUSTCClient : ClientModInitializer {
	override fun onInitializeClient() {
		MenuScreens.register(CardMachineMenu.type,::CardMachineMenuScreen)
	}
}