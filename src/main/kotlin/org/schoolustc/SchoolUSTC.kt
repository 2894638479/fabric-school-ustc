package org.schoolustc

import net.fabricmc.api.ModInitializer
import org.schoolustc.structure.Classroom
import org.schoolustc.structurePieces.ClassroomPiece
import org.schoolustc.structures.SchoolStructure
import org.slf4j.LoggerFactory


object SchoolUSTC : ModInitializer {
	val id = "school-ustc"
    val logger = LoggerFactory.getLogger(id)

	override fun onInitialize() {
		SchoolStructure.register()
		Classroom.register()
	}
}