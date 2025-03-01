package org.schoolustc

import net.fabricmc.api.ModInitializer
import net.minecraft.resources.ResourceLocation
import org.intellij.lang.annotations.Identifier
import org.schoolustc.structure.Classroom
import org.schoolustc.structurePieces.ClassroomPiece
import org.schoolustc.structures.SchoolStructure
import org.slf4j.LoggerFactory


val id = "school-ustc"
fun fullId(str: String) = ResourceLocation(id,str)
val logger = LoggerFactory.getLogger(id)

object SchoolUSTC : ModInitializer {
	override fun onInitialize() {
		SchoolStructure.register()
		Classroom.register()
	}
}