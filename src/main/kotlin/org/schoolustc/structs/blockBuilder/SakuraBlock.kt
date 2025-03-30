package org.schoolustc.structs.blockBuilder

import org.schoolustc.structs.Sakura
import org.schoolustc.structs.builder.SakuraBuilder
import org.schoolustc.structureDsl.Area2D
import org.schoolustc.structureDsl.Direction2D
import org.schoolustc.structureDsl.Point
import org.schoolustc.structureDsl.from
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.structure.StructureBuildScope
import org.schoolustc.structureDsl.structure.builder.MyStructListBuilder

class SakuraBlock(
    val area: Area2D,
    val nextToWalls:List<Direction2D>,
    val nextToSplitter:List<Direction2D>
):MyStructListBuilder<MyStruct>() {
    override fun StructureBuildScope.build() = mutableListOf<MyStruct>().also { list ->
        list += NormalBlock(area, nextToWalls, nextToSplitter).build(this)
        val area = area.padding(2)
        val treeArea = area.padding(3)
        val tries = List(5){
            val tree = mutableListOf<SakuraBuilder>()
            for (i in 1..10){
                val x = rand from treeArea.x
                val z = rand from treeArea.z
                val pos = Point(x,y(x,z) + 1,z)
                if(tree.firstOrNull { it.pos.distanceTo(pos) < 8.0 } == null){
                    tree += SakuraBuilder(pos)
                }
            }
            tree
        }
        val chosen = tries.maxBy { it.size }
        list += chosen.map { it.build() }
    }
}