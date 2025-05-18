package org.schoolustc

import net.fabricmc.api.ModInitializer
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import org.schoolustc.gui.CardMachineMenu
import org.schoolustc.gui.GradingMachineMenu
import org.schoolustc.gui.TeachingTableMenu
import org.schoolustc.items.GradingMachineBlock
import org.schoolustc.items.QuestionItem
import org.schoolustc.items.TeachingTableBlock
import org.schoolustc.items.registerItemAndBlock
import org.schoolustc.questionbank.registerReloadListener
import org.schoolustc.structs.*
import org.schoolustc.structs.feature.BrownMushroomGrassFeature
import org.schoolustc.structureDsl.struct.MyStructInfo
import org.slf4j.LoggerFactory


const val id = "school-ustc"
fun fullId(str: String) = ResourceLocation(id,str)
val logger = LoggerFactory.getLogger(id)
val structs = listOf<MyStructInfo<*>>(
	Classroom,Gate,LeafWall,OuterWall,OuterWallCorner,Road,Splitter,Street,StreetLight,Building,Tree,Path,Park,CherrySide,TreeSide,WhitePavilion,
	ClassroomTop,Hallway,StairwellTop,Stairwell,HallwayTop,HallwayCrossing,HallwayCrossingTop,Balcony,BalconyTop,Base,HugeGoldOre
)
object SchoolUSTC : ModInitializer {
	override fun onInitialize() {
		logger.info("$id loading")
		School.register()
		BrownMushroomGrassFeature.register()
		CardMachineMenu.register()
		TeachingTableMenu.register()
		TeachingTableMenu.registerPacket()
		GradingMachineMenu.register()
		structs.forEach { it.register() }
		registerItemAndBlock()
		QuestionItem.registerPacket()
		registerReloadListener()
	}
}