package org.schoolustc.structs.blockbuilder

import net.minecraft.world.level.block.Blocks.DIRT_PATH
import org.schoolustc.structs.Path
import org.schoolustc.structs.blockbuilder.OpenRangeBuilder.Companion.toOpenArea
import org.schoolustc.structs.builder.BuildingBuilder
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.structure.StructureBuildScope
import kotlin.math.roundToInt

class BuildingBlock(para: BlockBuilderPara):BlockBuilder(para) {
    override fun StructureBuildScope.build() = mutableListOf<MyStruct>().also { list ->
        val pos = area.middle(::y)
        val area = Area2D(pos.x..pos.x,pos.z..pos.z).expand(5)
        val direction = rand from Direction2D.entries.filter { it !in nextToWalls }
        val height = rand from 1..6
        val flatTop = !(height >= 5 && rand.nextBool(0.3f))

        val path1 = area.sliceEnd(direction,1).slice(direction.left,4..6).offset(direction,1)
        val path1Mid = path1.middle{_,_->0}
        val path2 = openRange
            .map { it.first to it.toOpenArea(para.area) }
            .filter { it.second.middle{_,_->0}.atDirectionOf(direction,path1Mid) }
            .minByOrNull { it.second.distanceToMid(path1) }
        list += BuildingBuilder(area,path1.midY(::y).roundToInt(),direction,height,flatTop).build()
        if(path2 != null) list += Path(
            path1.middle(),
            path2.second.middle(),
            direction.toOrientation(),
            path2.first.reverse.toOrientation(),
            3.0,
            DIRT_PATH
        )
    } + getLights() + getLeafWalls()
}