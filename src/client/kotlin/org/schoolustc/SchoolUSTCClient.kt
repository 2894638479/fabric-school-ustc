package org.schoolustc

import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.renderer.item.ItemProperties
import org.schoolustc.gui.CardMachineMenu
import org.schoolustc.gui.CardMachineMenuScreen
import org.schoolustc.gui.QuestionScreen
import org.schoolustc.items.QUESTION_ITEM
import org.schoolustc.items.QuestionItem.Companion.predicate


object SchoolUSTCClient : ClientModInitializer {
	override fun onInitializeClient() {
		MenuScreens.register(CardMachineMenu.type,::CardMachineMenuScreen)
		QuestionScreen.registerPacket()
		ItemProperties.register(QUESTION_ITEM, fullId("status")) { stack, _, _, _ -> stack.predicate }
	}
}