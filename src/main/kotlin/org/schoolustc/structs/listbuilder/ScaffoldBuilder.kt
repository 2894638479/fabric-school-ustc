package org.schoolustc.structs.listbuilder

import net.minecraft.data.worldgen.features.TreeFeatures
import org.schoolustc.structs.Road
import org.schoolustc.structs.Splitter
import org.schoolustc.structs.Street
import org.schoolustc.structs.blockbuilder.*
import org.schoolustc.structs.builder.GateBuilder
import org.schoolustc.structs.builder.RoadBuilder
import org.schoolustc.structs.builder.WallCornerBuilder
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.Area2D.Companion.area2D
import org.schoolustc.structureDsl.struct.MyRoadStruct
import org.schoolustc.structureDsl.struct.MyRoadStructInfo
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.struct.builder.MyStructBuilder
import org.schoolustc.structureDsl.struct.builder.MyStructListBuilder
import org.schoolustc.structureDsl.structure.StructureBuildScope
import kotlin.math.log2
import kotlin.math.roundToInt

class ScaffoldBuilder(
    val area:Area2D,
    val minBlockSize:Int = 15
): MyStructListBuilder<MyStruct>() {
    override fun StructureBuildScope.build() = mutableListOf<MyStruct>().also { list ->
        fun MyStructListBuilder<*>.addToList(){
            list.addAll(this.build(this@build))
        }
        fun MyStructBuilder<*>.addToList(){
            list.add(this.build())
        }
        val innerArea = area.padding(1)
        val blockList = mutableListOf(innerArea)
        val splitTime = (log2(innerArea.size.toFloat())).toInt().match { it > 0 }
        var gatePos:Pair<Direction2D,Int>? = null

        val roadBuilders = mutableListOf<RoadBuilder<*>>()
        fun <T : MyRoadStruct> addRoad(type:MyRoadStructInfo<T>) :Unit? {
            val choices = blockList.flatMap {
                Direction2D.entries.map { direction -> direction to it }
            }.filter { (direction,block)-> direction.run {
                block.width >= type.width + 2 * minBlockSize
            } }.ifEmpty { return null }
            val (direction,block) = rand from choices
            blockList.remove(block)
            direction.run {
                val pos = rand.nextInt(minBlockSize..block.width - minBlockSize - type.width)
                if(gatePos == null) gatePos = rand from listOf(direction,direction.reverse) to pos + 1
                blockList += area2D(block.l,block.w.first..<block.w.first + pos)
                blockList += area2D(block.l,block.w.first + pos + type.width..block.w.last)
                roadBuilders += RoadBuilder(
                    area2D(block.l,block.w.first + pos..<block.w.first + pos + type.width),
                    direction,
                    type
                )
            }
            return Unit
        }


        val streetMark = rand.nextInt(splitTime / 4..splitTime / 2).match { it > 0 }
        val roadMark = rand.nextInt(splitTime * 3 / 4..splitTime)
        for(i in 0..<splitTime){
            if(i < streetMark) addRoad(Street) ?: break
            else if(i < roadMark) addRoad(Road) ?: break
            else addRoad(Splitter) ?: break
        }
        val wallBuilders = mutableListOf<WallListBuilder>()
        val wallCornerBuilders = mutableListOf<WallCornerBuilder>()
        for (d in Direction2D.entries) d.run {
            val wallArea = area.sliceEnd(this,1)
            if(this == gatePos?.first){
                val builder = GateBuilder(wallArea,gatePos!!.second, gatePos!!.first) { avgY.roundToInt() }.apply { addToList() }
                wallBuilders += WallListBuilder(right,builder.wallArea1)
                wallBuilders += WallListBuilder(right,builder.wallArea2)
            } else {
                wallBuilders += WallListBuilder(right,wallArea)
            }
        }
        area.run {
            fun add(x:Int,z:Int) { wallCornerBuilders += WallCornerBuilder(x, z) }
            add(x1,z1);add(x2,z1);add(x1,z2);add(x2,z2)
        }
        blockList.forEach { area ->
            val nextWalls = Direction2D.entries.filter {
                area.nextTo(this@ScaffoldBuilder.area.sliceEnd(it,1)) != null
            }
            val nextSplitter = Direction2D.entries.filter {
                roadBuilders.firstOrNull { road ->road.type == Splitter && area.nextTo(road.area) == it } != null
            }
            val para = BlockBuilderPara(area,nextWalls,nextSplitter,this)
            val block = rand from mapOf(
                { TreeBlock(para,rand from mapOf(
                    TreeFeatures.OAK_BEES_005 to 5,
                    TreeFeatures.JUNGLE_TREE to 1,
                    TreeFeatures.JUNGLE_BUSH to 1,
                    TreeFeatures.MANGROVE to 2,
                    TreeFeatures.BIRCH_BEES_005 to 3
                ))} to 1,
                {NormalBlock(para)} to 1,
                {SakuraBlock(para)} to 1,
                {BuildingBlock(para)} to 1
            )

            block().addToList()
        }
        roadBuilders.forEach { it.addToList() }
        wallBuilders.forEach { it.addToList() }
        wallCornerBuilders.forEach { it.addToList() }
    }
}