package org.schoolustc

import net.fabricmc.api.ModInitializer
import net.minecraft.resources.ResourceLocation
import org.schoolustc.structurePieces.ClassroomPiece
import org.schoolustc.structures.SchoolStructure
import org.slf4j.LoggerFactory


const val id = "school-ustc"
fun fullId(str: String) = ResourceLocation(id,str)
val logger = LoggerFactory.getLogger(id)

object SchoolUSTC : ModInitializer {
	override fun onInitialize() {
		SchoolStructure.register()
		ClassroomPiece.register()
	}
}