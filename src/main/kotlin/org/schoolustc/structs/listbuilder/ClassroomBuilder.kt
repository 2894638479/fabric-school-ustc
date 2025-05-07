package org.schoolustc.structs.listbuilder

import net.minecraft.util.RandomSource
import org.schoolustc.logger
import org.schoolustc.structs.Classroom
import org.schoolustc.structs.Hallway
import org.schoolustc.structs.HallwayCrossing
import org.schoolustc.structs.Stairwell
import org.schoolustc.structs.listbuilder.ClassroomBuilder.HallwayCrossingBase.Companion.hallwayCrossingBase
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.struct.builder.MyStructListBuilder
import org.schoolustc.structureDsl.struct.scope.StructGenConfig
import org.schoolustc.structureDsl.structure.StructureBuildScope

class ClassroomBuilder(val area: Area2D,val y:Int,val rand:RandomSource):MyStructListBuilder<MyStruct>() {
    class Door(val direction:Direction2D,val pos:Point,val choice:()-> Base)
    abstract class Base(val depth:Int) {
        abstract val area:Area2D
        abstract val doors: List<Door>
        abstract fun generateByHeight(y:Int):MyStruct
        protected fun Direction2D.revIf(boolean: Boolean) = if(boolean) reverse else this
        val child = mutableListOf<Base>()
        val childCount:Int get() = child.sumOf { it.childCount + 1 }
        fun generateAllChildren(y:Int):List<MyStruct>{
            return child.map { it.generateAllChildren(y) }.flatten() + generateByHeight(y)
        }
        fun overlapChild(area:Area2D):Boolean {
            return area overlap this.area || child.firstOrNull { it.overlapChild(area) } != null
        }
    }
    class RoomBase(val direction:Direction2D,val pos:Point,val rand: RandomSource,depth:Int): Base(depth){
        val rev = false//rand.nextBool(0.5)
        override val area = Area2D(pos.x.range,pos.z.range)
            .expand(direction,7)
            .expand(direction.left.revIf(rev),8)
            .expand(direction.right.revIf(rev),3)
        override val doors = listOf<Door>()
        override fun generateByHeight(y: Int): MyStruct {
            val config = StructGenConfig.byDirection(area,y,direction.reverse,Classroom)
            return Classroom(config)
        }
    }
    class HallwayBase(val direction:Direction2D,val pos:Point,val rand:RandomSource,depth:Int): Base(depth){
        val length = rand from 1..5
        override val area = Area2D(pos.x.range,pos.z.range)
            .expand(direction,length - 1)
            .expand(direction.left,2)
            .expand(direction.right,2)
        override val doors = run {
            val pos1 = pos.offset(direction,length)
            val door1 = Door(direction,pos1){
                (rand from mapOf(
                    ::HallwayBase to 1,
                    ::RoomBase to 1,
                ))(direction,pos1,rand,depth + 1)
            }
            listOf(door1)
        }
        override fun generateByHeight(y: Int): MyStruct {
            val config = StructGenConfig.byDirection(area,y,direction,Hallway)
            return Hallway(config,length)
        }
    }
    class HallwayCrossingBase(val direction:Direction2D,val pos:Point,val rand:RandomSource,val isBegin:Boolean,depth:Int): Base(depth){
        companion object {
            fun hallwayCrossingBase(direction:Direction2D,pos:Point,rand:RandomSource,depth: Int) = HallwayCrossingBase(direction,pos,rand,false,depth)
        }
        val length = rand from 8..15
        val door = rand from 3..length - 4
        override val area = Area2D(pos.x.range,pos.z.range)
            .expand(direction,length - 1)
            .expand(direction.left,2)
            .expand(direction.right,2)
        override val doors = run {
            val pos1 = pos.offset(direction,length)
            val door1 = Door(direction,pos1){
                rand.from(
                    ClassroomBuilder::HallwayBase to 1,
                    ClassroomBuilder::RoomBase to 1,
                    ClassroomBuilder::StairwellBase to 1,
                    ::hallwayCrossingBase to 1
                )(direction,pos1,rand,depth + 1)
            }
            val direction2 = direction.left
            val pos2 = pos.offset(direction,door).offset(direction2,3)
            val door2 = Door(direction2,pos2){
                rand.from(
                    ClassroomBuilder::HallwayBase to 1,
                    ::hallwayCrossingBase to 1
                )(direction2,pos2,rand,depth + 1)
            }
            val direction3 = direction.right
            val pos3 = pos.offset(direction,door).offset(direction2,3)
            val door3 = Door(direction3,pos3){
                rand.from(
                    ClassroomBuilder::HallwayBase to 1,
                    ::hallwayCrossingBase to 1
                )(direction3,pos3,rand,depth + 1)
            }
            if(isBegin){
                val direction4 = direction.reverse
                val pos4 = pos.offset(direction4)
                val door4 = Door(direction4,pos4){
                    rand.from(
                        ClassroomBuilder::HallwayBase to 1,
                        ClassroomBuilder::RoomBase to 1,
                        ClassroomBuilder::StairwellBase to 1,
                        ::hallwayCrossingBase to 1
                    )(direction4,pos4,rand,depth + 1)
                }
                listOf(door1,door2,door3,door4)
            } else listOf(door1,door2,door3)
        }

        override fun generateByHeight(y: Int): MyStruct {
            val config = StructGenConfig.byDirection(area,y,direction,HallwayCrossing)
            return HallwayCrossing(config,length, door)
        }
    }
    class StairwellBase(val direction:Direction2D,val pos:Point,val rand:RandomSource,depth:Int): Base(depth){
        val rev = rand.nextBool(0.5)
        override val area = Area2D(pos.x.range,pos.z.range)
            .expand(direction.right.revIf(rev),2)
            .expand(direction.left.revIf(rev),8)
            .expand(direction,6)
        override val doors = run {
            val direction1 = direction
            val pos1 = pos.offset(direction1,7)
            val door1 = Door(direction1,pos1){
                rand.from(
                    ClassroomBuilder::HallwayBase to 1,
                    ::hallwayCrossingBase to 1
                )(direction1,pos1,rand,depth + 1)
            }
            listOf(door1)
        }

        override fun generateByHeight(y: Int): MyStruct {
            val config = StructGenConfig.byDirection(area,y,direction.revIf(rev),Stairwell)
            return Stairwell(config)
        }
    }
    override fun StructureBuildScope.build(): List<MyStruct> {
        val tries = List(10){
            val beginDirection = rand from Direction2D.entries
            val beginPos = rand from area.padding(5)
            val begin = HallwayCrossingBase(beginDirection,beginPos.run { Point(first,y,second) },rand,true,0)
            fun Base.calChildren(){
                if(depth >= 1) return
                child += doors.map { it.choice() }.filter {
                    it.area in this@ClassroomBuilder.area
                            && !overlapChild(it.area)
                }
                child.forEach { it.calChildren() }
            }
            begin.apply { calChildren() }
        }
        val chosen = tries.maxByOrNull { it.childCount }
        logger.info(chosen?.area.toString())
        return chosen?.generateAllChildren(y) ?: listOf()
    }
}