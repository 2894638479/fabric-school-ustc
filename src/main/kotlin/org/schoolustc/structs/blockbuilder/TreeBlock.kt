package org.schoolustc.structs.blockbuilder

import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import org.schoolustc.structs.builder.TreeBuilder
import org.schoolustc.structureDsl.Point
import org.schoolustc.structureDsl.from
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.structure.StructureBuildScope

class TreeBlock(
    para:BlockBuilderPara,
    val type: ResourceKey<ConfiguredFeature<*, *>>
):BlockBuilder(para) {
    override fun StructureBuildScope.build() = mutableListOf<MyStruct>().also { list ->
        val area = para.area.padding(2)
        val treeArea = area.padding(3)
        val tries = List(5){
            mutableListOf<TreeBuilder>().also { trees ->
                for (i in 1..10){
                    val x = rand from treeArea.x
                    val z = rand from treeArea.z
                    val pos = Point(x,y(x,z) + 1,z)
                    if(trees.firstOrNull { it.pos.distanceTo(pos) < 6.0 } == null){
                        trees += TreeBuilder(pos,type)
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