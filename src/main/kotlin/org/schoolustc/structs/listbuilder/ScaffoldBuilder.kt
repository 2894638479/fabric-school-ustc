package org.schoolustc.structs.listbuilder

import org.schoolustc.structs.*
import org.schoolustc.structs.blockbuilder.*
import org.schoolustc.structs.builder.GateBuilder
import org.schoolustc.structs.builder.FixedWidthBuilder
import org.schoolustc.structs.builder.RoadBuilder
import org.schoolustc.structs.builder.WallCornerBuilder
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.Area2D.Companion.area2D
import org.schoolustc.structureDsl.struct.MyStructFixedWidth
import org.schoolustc.structureDsl.struct.MyStructFixedWidthInfo
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
        class Block(
            val area:Area2D,
            val side:(Direction2D)->MyStructFixedWidthInfo<*>?
        ){
            fun sideWidth(d:Direction2D) = side(d)?.width ?: 0
        }
        val blockList = mutableListOf(Block(innerArea){null})
        val splitTime = (log2(innerArea.size.toFloat())).toInt().match { it > 0 }
        var gatePos:Pair<Direction2D,Int>? = null

        val roadBuilders = mutableListOf<RoadBuilder<*,*>>()
        fun <T:MyStructFixedWidth,V:MyStructFixedWidth> addRoad(type:MyStructFixedWidthInfo<T>, side:MyStructFixedWidthInfo<V>?) :Unit? {
            val sideWidth = side?.width ?: 0
            val fullWidth = type.width + 2*sideWidth
            val choices = blockList.flatMap {
                Direction2D.entries.map { direction -> direction to it }
            }.filter { (direction,block)-> direction.run {
                block.area.width - block.sideWidth(left) - block.sideWidth(right) >= fullWidth + 2 * minBlockSize
            } }.ifEmpty { return null }
            val (direction, block) = rand from choices
            blockList.remove(block)
            val area = block.area
            direction.run {
                val pos = rand.nextInt(minBlockSize + block.sideWidth(left.min)..area.width - minBlockSize - fullWidth - block.sideWidth(left.plus))
                if(gatePos == null) gatePos = rand from listOf(direction,direction.reverse) to pos + 1 + sideWidth
                blockList += Block(area2D(area.l,area.w.first..<area.w.first + pos + sideWidth)) {
                    if(it == left.plus) side else block.side(it)
                }
                blockList += Block(area2D(area.l,area.w.first + pos + fullWidth - sideWidth..area.w.last)) {
                    if(it == left.min) side else block.side(it)
                }
                roadBuilders += RoadBuilder(
                    area2D(area.l,area.w.first + pos + sideWidth..<area.w.first + pos + type.width + sideWidth),
                    direction, type,side
                )
            }
            return Unit
        }
        fun <T:MyStructFixedWidth> addRoad(type:MyStructFixedWidthInfo<T>) = addRoad<T,MyStructFixedWidth>(type,null)


        val streetMark = rand.nextInt(splitTime / 4..splitTime / 2).match { it > 0 }
        val roadMark = rand.nextInt(splitTime * 3 / 4..splitTime)
        for(i in 0..<splitTime){
            if(i < streetMark) addRoad(Street,rand from mapOf(
                null to 4,
                TreeSide to 5,
                CherrySide to 5,
            )) ?: break
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

        val blockBuilders = mutableListOf<BlockBuilder>()
        val roadSideBuilders = mutableListOf<FixedWidthBuilder<*>>()
        blockList.forEach { block ->
            var area = block.area
            for(d in Direction2D.entries){
                val side = block.side(d) ?: continue
                roadSideBuilders += FixedWidthBuilder(area.sliceEnd(d,side.width),d.right,side)
                area = area.padding(side.width,d)
            }
            val nextWalls = Direction2D.entries.filter {
                area.nextTo(this@ScaffoldBuilder.area.sliceEnd(it,1)) != null
            }
            val nextSplitter = Direction2D.entries.filter {
                roadBuilders.firstOrNull { road ->road.type == Splitter && area.nextTo(road.area) == it } != null
            }
            val para = BlockBuilderPara(area,nextWalls,nextSplitter,this)
            val blockBuilder = rand from mapOf(
                {NormalBlock(para)} to 1,
                {BuildingBlock(para)} to 1,
                {ParkBlock(para)} to 1,
                {WhitePavilionBlock(para)} to 1,
            )
            blockBuilders += blockBuilder()
        }

        roadBuilders.forEach { it.addToList() }
        wallBuilders.forEach { it.addToList() }
        wallCornerBuilders.forEach { it.addToList() }
        roadSideBuilders.forEach { it.addToList() }
        blockBuilders.forEach { it.addToList() }
    }
}