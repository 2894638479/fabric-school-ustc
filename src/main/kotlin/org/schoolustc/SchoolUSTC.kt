package org.schoolustc

import net.fabricmc.api.ModInitializer
import net.minecraft.resources.ResourceLocation
import org.schoolustc.structurePieces.*
import org.schoolustc.structures.School
import org.slf4j.LoggerFactory


const val id = "school-ustc"
fun fullId(str: String) = ResourceLocation(id,str)
val logger = LoggerFactory.getLogger(id)

object SchoolUSTC : ModInitializer {
	override fun onInitialize() {
		School.register()
		ClassroomPiece.register()
		BlocksPiece.register()
		StreetPiece.register()
		OuterWallPiece.register()
		OuterWallCornerPiece.register()
	}
}