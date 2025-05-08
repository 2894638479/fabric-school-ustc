package org.schoolustc

import net.fabricmc.api.ModInitializer
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.features.FeatureUtils
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration
import org.schoolustc.structs.*
import org.schoolustc.structs.feature.BrownMushroomGrassFeature
import org.schoolustc.structureDsl.struct.MyStructInfo
import org.slf4j.LoggerFactory


const val id = "school-ustc"
fun fullId(str: String) = ResourceLocation(id,str)
val logger = LoggerFactory.getLogger(id)
val structs = listOf<MyStructInfo<*>>(
	Classroom,Gate,LeafWall,OuterWall,OuterWallCorner,Road,Splitter,Street,StreetLight,Building,Tree,Path,Park,CherrySide,TreeSide,WhitePavilion,
	ClassroomTop,Hallway,StairwellTop,Stairwell,HallwayTop,HallwayCrossing,HallwayCrossingTop,Balcony,BalconyTop
)
object SchoolUSTC : ModInitializer {
	override fun onInitialize() {
		logger.info("$id loading")
		School.register()
		BrownMushroomGrassFeature.register()
		structs.forEach { it.register() }
	}
}