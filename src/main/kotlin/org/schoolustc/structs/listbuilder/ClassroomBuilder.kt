package org.schoolustc.structs.listbuilder

import net.minecraft.util.RandomSource
import org.schoolustc.structs.*
import org.schoolustc.structs.listbuilder.ClassroomBuilder.HallwayCrossingBase.Companion.hallwayCrossingBase
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.struct.builder.MyStructListBuilder
import org.schoolustc.structureDsl.struct.scope.StructGenConfig
import org.schoolustc.structureDsl.structure.StructureBuildScope

class ClassroomBuilder(val area: Area2D,val y:Int,val rand:RandomSource):MyStructListBuilder<MyStruct>() {
    class Door(val direction:Direction2D,val pos:Point,val choices:(Direction2D,Point)-> Base?){
        var connect:Base? = null
        fun choose() = choices(direction, pos)
    }
    abstract class Base(val depth:Int) {
        abstract val area:Area2D
        abstract val doors: List<Door>
        abstract val score:Double
        abstract fun generateByHeight(y:Int):MyStruct
        abstract fun generateTopByHeight(y:Int):MyStruct
        protected fun Direction2D.revIf(boolean: Boolean) = if(boolean) reverse else this
        val doorConnected get() = doors.mapNotNull { it.connect }
        val childSequence = sequence {
            suspend fun SequenceScope<Base>.iterateDoorConnected(base:Base){
                base.doors.forEach {
                    it.connect?.let {
                        yield(it)
                        iterateDoorConnected(it)
                    }
                }
            }
            iterateDoorConnected(this@Base)
        }
        val childSequenceWithParent = sequence {
            suspend fun SequenceScope<Pair<Base,Base>>.iterateDoorConnected(base:Base){
                base.doors.forEach {
                    it.connect?.let {
                        yield(it to base)
                        iterateDoorConnected(it)
                    }
                }
            }
            iterateDoorConnected(this@Base)
        }
        val childAndSelf = sequence {
            yield(this@Base)
            childSequence.forEach { yield(it) }
        }
        val childAndSelfWithParent = sequence {
            yield(this@Base to null)
            childSequenceWithParent.forEach { yield(it) }
        }
        var layerCount = 0
        fun generateAllLayer(y:Int) = (0..<layerCount).map{ generateByHeight(y + 5*it) } + generateTopByHeight(y+5*layerCount)
        fun generateAllChildren(y:Int):List<MyStruct>{
            return childAndSelf.map { it.generateAllLayer(y) }.flatten().toList()
        }
        fun overlapChild(area:Area2D):Boolean {
            return childAndSelf.firstOrNull { it.area overlap area } != null
        }
        val scoreSum:Double get() = childAndSelf.sumOf { it.score }
        fun hasChild(predicate:(Base)->Boolean) = childAndSelf.firstOrNull { predicate(it) } != null
    }
    class RoomBase(val direction:Direction2D,val pos:Point,val rand: RandomSource,depth:Int): Base(depth){
        val rev = false//rand.nextBool(0.5)
        override val score get() = 15.0
        override val area = Area2D(pos.x.range,pos.z.range)
            .expand(direction,7)
            .expand(direction.left.revIf(rev),8)
            .expand(direction.right.revIf(rev),3)
        override val doors = listOf<Door>()
        override fun generateByHeight(y: Int): MyStruct {
            val config = StructGenConfig.byDirection(area,y,direction.reverse,Classroom)
            return Classroom(config)
        }

        override fun generateTopByHeight(y: Int): MyStruct {
            val config = StructGenConfig.byDirection(area,y,direction.reverse,ClassroomTop)
            return ClassroomTop(config)
        }
    }
    class HallwayBase(val direction:Direction2D,val pos:Point,val rand:RandomSource,depth:Int): Base(depth){
        override val score get() = 2.0
        val length = rand from 1..5
        override val area = Area2D(pos.x.range,pos.z.range)
            .expand(direction,length - 1)
            .expand(direction.left,2)
            .expand(direction.right,2)
        override val doors = run {
            val door1 = Door(direction, pos.offset(direction, length)) { direction,pos ->
                (rand from mapOf(
                    ::hallwayCrossingBase to 2,
                    ::RoomBase to 3,
                    ::StairwellBase to 1
                ))(direction, pos, rand, depth + 1)
            }
            listOf(door1)
        }
        override fun generateByHeight(y: Int): MyStruct {
            val config = StructGenConfig.byDirection(area,y,direction,Hallway)
            return Hallway(config,length)
        }

        override fun generateTopByHeight(y: Int): MyStruct {
            val config = StructGenConfig.byDirection(area,y,direction,HallwayTop)
            return HallwayTop(config,length)
        }
    }
    class HallwayCrossingBase(val direction:Direction2D,val pos:Point,val rand:RandomSource,val isBegin:Boolean,depth:Int): Base(depth){
        companion object {
            fun hallwayCrossingBase(direction:Direction2D,pos:Point,rand:RandomSource,depth: Int) = HallwayCrossingBase(direction,pos,rand,false,depth)
        }
        override val score get() = 4.0
        val length = rand from 8..15
        val door = rand from 3..length - 4
        override val area = Area2D(pos.x.range,pos.z.range)
            .expand(direction,length - 1)
            .expand(direction.left,2)
            .expand(direction.right,2)
        override val doors = run {
            val endChoice = {
                rand.from(
                    ::RoomBase to 4,
                    ::StairwellBase to 1,
                    ::hallwayCrossingBase to 1
                )
            }
            val midChoice = {
                rand.from(
                    ::HallwayBase to 1,
                    ::hallwayCrossingBase to 1
                )
            }
            val door1 = Door(direction, pos.offset(direction, length)) { direction, pos ->
                endChoice()(direction, pos, rand, depth + 1)
            }
            val door2 = Door(direction.left, pos.offset(direction, door).offset(direction.left, 3)) { direction, pos ->
                midChoice()(direction, pos, rand, depth + 1)
            }
            val door3 = Door(direction.right, pos.offset(direction, door).offset(direction.left, 3)) { direction, pos ->
                midChoice()(direction, pos, rand, depth + 1)
            }
            if(isBegin){
                val door4 = Door(direction.reverse, pos.offset(direction.reverse)) { direction, pos ->
                    endChoice()(direction, pos, rand, depth + 1)
                }
                val door31 = Door(direction.right, pos.offset(direction, door).offset(direction.left, 3)) { _, _ -> null }
                listOf(door1,door2,door31,door4)
            } else listOf(door1,door2,door3)
        }

        override fun generateByHeight(y: Int): MyStruct {
            val config = StructGenConfig.byDirection(area,y,direction,HallwayCrossing)
            return HallwayCrossing(config,length, door)
        }

        override fun generateTopByHeight(y: Int): MyStruct {
            val config = StructGenConfig.byDirection(area,y,direction,HallwayCrossingTop)
            return HallwayCrossingTop(config,length, door)
        }
    }
    class StairwellBase(val direction:Direction2D,val pos:Point,val rand:RandomSource,depth:Int): Base(depth){
        val rev = rand.nextBool(0.5)
        override val score get() = 5.0
        override val area = Area2D(pos.x.range,pos.z.range)
            .expand(direction.right.revIf(rev),2)
            .expand(direction.left.revIf(rev),8)
            .expand(direction,6)
        override val doors = run {
            val direction1 = direction
            val pos1 = pos.offset(direction1,7)
            val door1 = Door(direction1,pos1){direction,pos ->
                rand.from(
                    ::HallwayBase to 1,
                    ::hallwayCrossingBase to 1
                )(direction,pos,rand,depth + 1)
            }
            listOf(door1)
        }

        override fun generateByHeight(y: Int): MyStruct {
            val config = StructGenConfig.byDirection(area,y,direction.revIf(rev),Stairwell)
            return Stairwell(config)
        }

        override fun generateTopByHeight(y: Int): MyStruct {
            val config = StructGenConfig.byDirection(area,y,direction.revIf(rev),StairwellTop)
            return StairwellTop(config)
        }
    }
    override fun StructureBuildScope.build(): List<MyStruct> {
        val tries = List(50){
            fun randBegin():Base{
                val beginDirection = rand from Direction2D.entries
                val beginPos = rand from area.padding(5)
                val begin = HallwayCrossingBase(beginDirection,beginPos.run { Point(first,y,second) },rand,true,0)
                if(begin.area !in area) return randBegin()
                return begin
            }
            val begin = randBegin()
            fun Base.calChildren(){
                if(depth >= 6) return
                doors.forEach {
                    val chosen = it.choose() ?: return@forEach
                    if(chosen.area !in this@ClassroomBuilder.area) return@forEach
                    if(begin.overlapChild(chosen.area)) return@forEach
                    it.connect = chosen
                }
                doors.forEach { it.connect?.calChildren() }
            }
            begin.apply { calChildren() }
        }
        val chosen = tries.filter { it.hasChild { it is StairwellBase } }
            .maxByOrNull { it.scoreSum + if(it.hasChild { it is RoomBase }) 1000 else 0 }
            ?: return emptyList()
        chosen.childAndSelf.forEach {
            if(it is StairwellBase){
                it.layerCount = rand from 3..6
            }
        }
        while (chosen.childAndSelf.minOf { it.layerCount } == 0) {
            chosen.childAndSelfWithParent.forEach { (it, parent) ->
                if(it.layerCount != 0) return@forEach
                it.layerCount = (it.doorConnected + parent).maxOf { it?.layerCount ?: 0 }
                if(it.layerCount > 1) {
                    if(rand.nextBool(0.2)) it.layerCount--
                }
            }
        }
        return chosen.generateAllChildren(y)
    }
}