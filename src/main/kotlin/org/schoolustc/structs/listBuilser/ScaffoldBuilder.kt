package org.schoolustc.structs.listBuilser

import org.schoolustc.logger
import org.schoolustc.structs.Road
import org.schoolustc.structs.Splitter
import org.schoolustc.structs.Street
import org.schoolustc.structs.builder.GateBuilder
import org.schoolustc.structs.builder.WallCornerBuilder
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.Area2D.Companion.area2D
import org.schoolustc.structureDsl.struct.IsRoad
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.struct.MyStructInfo
import org.schoolustc.structureDsl.struct.builder.MyStructBuilder
import org.schoolustc.structureDsl.structure.StructureBuildScope
import org.schoolustc.structureDsl.structure.builder.MyStructListBuilder
import kotlin.math.log2
import kotlin.math.roundToInt

class ScaffoldBuilder(
    val area:Area2D,
    val minBlockSize:Int = 15
): MyStructListBuilder<MyStruct> {
    override fun StructureBuildScope.build() = mutableListOf<MyStruct>().also { list ->
        fun MyStructListBuilder<*>.addToList(){
            list.addAll(this.run { this@build.build() })
        }
        fun MyStructBuilder<*>.addToList(){
            list.add(this.build())
        }
        val innerArea = area.padding(1)
        val blockList = mutableListOf(innerArea)
        val splitTime = (log2(innerArea.size.toFloat())).toInt().match { it > 0 }
        var gatePos:Pair<Direction2D,Int>? = null


        fun <T : MyStruct, V> addRoad(type:V) :Unit? where V : MyStructInfo<T>, V:IsRoad {
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
                RoadListBuilder(
                    area2D(block.l,block.w.first + pos..<block.w.first + pos + type.width),
                    direction,
                    type
                ).addToList()
            }
            return Unit
        }


        val streetMark = rand.nextInt(splitTime / 4..splitTime / 2).match { it > 0 }
        val roadMark = rand.nextInt(streetMark..splitTime)
        for(i in 0..<splitTime){
            if(i < streetMark) addRoad(Street) ?: break
            else if(i < roadMark) addRoad(Road) ?: break
            else addRoad(Splitter) ?: break
        }

        for (d in Direction2D.entries) d.run {
            val wallArea = area.sliceEnd(this,1)
            if(this == gatePos?.first){
                val builder = GateBuilder(wallArea,gatePos!!.second, gatePos!!.first) { avgY.roundToInt() }.apply { addToList() }
                WallListBuilder(right,builder.wallArea1).addToList()
                WallListBuilder(right,builder.wallArea2).addToList()
            } else {
                WallListBuilder(right,wallArea).addToList()
            }
        }
        area.run {
            fun add(x:Int,z:Int) = WallCornerBuilder(x,z).addToList()
            add(x1,z1);add(x2,z1);add(x1,z2);add(x2,z2)
        }
    }
}