package org.schoolustc

import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.renderer.item.ItemProperties
import org.schoolustc.gui.*
import org.schoolustc.items.QUESTION_ITEM
import org.schoolustc.items.QuestionItem.Companion.predicate
import org.schoolustc.packet.registerQuestionBankPacket


object SchoolUSTCClient : ClientModInitializer {
	override fun onInitializeClient() {
		MenuScreens.register(CardMachineMenu.type,::CardMachineMenuScreen)
		MenuScreens.register(TeachingTableMenu.type,::TeachingTableMenuScreen)
		MenuScreens.register(GradingMachineMenu.type,::GradingMachineMenuScreen)
		QuestionScreen.registerPacket()
		registerQuestionBankPacket()
		ItemProperties.register(QUESTION_ITEM, fullId("status")) { stack, _, _, _ -> stack.predicate }
	}
}