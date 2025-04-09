package org.schoolustc.structs.blockbuilder

import net.minecraft.data.worldgen.features.TreeFeatures
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import org.schoolustc.structs.Tree
import org.schoolustc.structs.builder.TreeBuilder
import org.schoolustc.structureDsl.Point
import org.schoolustc.structureDsl.from
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.structure.StructureBuildScope

class TreeBlock(
    para:BlockBuilderPara,
    val type:() -> Pair<ResourceKey<ConfiguredFeature<*, *>>,Tree.TreeType>
):BlockBuilder(para) {
    val maxCount = area.size / 20
    override fun StructureBuildScope.build() = mutableListOf<MyStruct>().also { list ->
        val area = para.area.padding(2)
        val treeArea = area
        val tries = List(5){
            mutableListOf<TreeBuilder>().also { trees ->
                for (i in 1..maxCount){
                    val (key,type) = type()
                    val isFancy = type == Tree.TreeType.BIG || type == Tree.TreeType.CHERRY
                    val maxDistance = if (isFancy) 7.0 else 4.0
                    val x = rand from treeArea.x
                    val z = rand from treeArea.z
                    val pos = Point(x,y(x,z) + 1,z)
                    if(trees.firstOrNull { it.pos.distanceTo(pos) < maxDistance } == null){
                        trees += TreeBuilder(pos,key,type)
                    }
                }
            }
        }
        val chosen = tries.maxBy { it.size }
        list += chosen.map { it.build() }
        list += getLights()
        list += getLeafWalls()
    }
}