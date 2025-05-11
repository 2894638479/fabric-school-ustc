package org.schoolustc

import net.fabricmc.api.ModInitializer
import net.minecraft.resources.ResourceLocation
import org.schoolustc.gui.CardMachineMenu
import org.schoolustc.items.CARD_MACHINE_BLOCK
import org.schoolustc.items.CARD_MACHINE_ITEM
import org.schoolustc.items.MONEY_CARD
import org.schoolustc.items.STUDENT_CARD
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
		structs.forEach { it.register() }
		STUDENT_CARD
		MONEY_CARD
		CARD_MACHINE_ITEM
		CARD_MACHINE_BLOCK
	}
}