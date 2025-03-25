package org.schoolustc

import net.fabricmc.api.ModInitializer
import net.minecraft.resources.ResourceLocation
import org.schoolustc.structs.*
import org.schoolustc.structures.School
import org.slf4j.LoggerFactory


const val id = "school-ustc"
fun fullId(str: String) = ResourceLocation(id,str)
val logger = LoggerFactory.getLogger(id)

object SchoolUSTC : ModInitializer {
	override fun onInitialize() {
		School.register()
		Classroom.register()
		Street.register()
		OuterWall.register()
		OuterWallCorner.register()
		Road.register()
		Splitter.register()
		Gate.register()
		LeafWall.register()
	}
}