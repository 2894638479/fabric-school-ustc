package org.schoolustc

import net.fabricmc.api.ModInitializer
import net.minecraft.resources.ResourceLocation
import org.schoolustc.structs.*
import org.schoolustc.structureDsl.struct.MyStructInfo
import org.schoolustc.structures.School
import org.slf4j.LoggerFactory


const val id = "school-ustc"
fun fullId(str: String) = ResourceLocation(id,str)
val logger = LoggerFactory.getLogger(id)
val structs = listOf<MyStructInfo<*>>(
	Classroom,Gate,LeafWall,OuterWall,OuterWallCorner,Road,Splitter,Street,StreetLight
)
object SchoolUSTC : ModInitializer {
	override fun onInitialize() {
		School.register()
		structs.forEach { it.register() }
	}
}