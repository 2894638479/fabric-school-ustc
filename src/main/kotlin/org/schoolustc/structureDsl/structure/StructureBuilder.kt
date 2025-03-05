package org.schoolustc.structureDsl.structure

import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder
import org.schoolustc.structureDsl.Area2D
import org.schoolustc.structureDsl.Point
import org.schoolustc.structureDsl.nextBool
import org.schoolustc.structureDsl.nextInt
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.struct.StructGenConfig
import org.schoolustc.structurePieces.*
import kotlin.math.ln
import kotlin.math.log2

class StructureBuilder(
    private val context:GenerationContext,
    private val builder:StructurePiecesBuilder
) {
    private inline val rand get() = context.random
    fun MyStruct.add() = builder.addPiece(this)
    fun List<MyStruct>.add() = forEach { builder.addPiece(it) }
    fun y(x:Int,z:Int) = context.chunkGenerator.getBaseHeight(
        x,z,
        Heightmap.Types.WORLD_SURFACE_WG,
        context.heightAccessor,
        context.randomState
    ) - 1
    fun randBool(trueChance:Float) = context.random.nextFloat() < trueChance
    inline val randBool get() = randBool(0.5f)
    fun randArea(posX:Int,posZ:Int,size:IntProgression): Area2D {
        val width = rand.nextInt(size)
        val height = rand.nextInt(size)
        val x = posX - width / 2
        val z = posZ - height / 2
        return Area2D(
            x..<x + width,
            z..<z + height
        )
    }
    class SplitResult(
        val wallArea:List<Area2D>,
        val wallCorArea:List<Area2D>,
        val blockArea:List<Area2D>,
        val streetArea:List<Area2D>,
        val roadArea:List<Area2D>,
        val splitterArea:List<Area2D>,
        val gateArea:List<Area2D>
    ){
    }
    fun splitArea(area:Area2D):SplitResult{
        val wallMaxLength = 10
        val streetWidth = 7
        val roadWidth = 3
        val splitterWidth = 1
        val blockMinSize = 15
        val gateWidth = 11
        val gateThick = 2


        val wall = mutableListOf<Area2D>()
        val wallCor = mutableListOf<Area2D>()
        val block = mutableListOf<Area2D>()
        val street = mutableListOf<Area2D>()
        val road = mutableListOf<Area2D>()
        val splitter = mutableListOf<Area2D>()
        val gate = mutableListOf<Area2D>()

        fun splitWall(range:IntRange):List<Int>{
            val full = range.last - range.first
            val count = full / (wallMaxLength + 1) + 1
            val each = full / count
            val rest = full % count
            val length = List(count){ if(it < rest) each + 1 else each }
            val result = mutableListOf<Int>()
            length.forEachIndexed { i, it ->
                result += if(i == 0) range.first + it
                else result[i-1] + it
            }
            return listOf(range.first) + result
        }
        fun addWall(area:Area2D){
            if (area.w == 1) {
                val l = splitWall(area.z)
                l.zipWithNext { a, b -> wall += Area2D(area.x,a+1..b-1) }
                l.dropLast(1).drop(1).forEach { wallCor += Area2D(area.x,it..it) }
            } else if(area.h == 1) {
                val l = splitWall(area.x)
                l.zipWithNext { a, b -> wall += Area2D(a+1..b-1,area.z) }
                l.dropLast(1).drop(1).forEach { wallCor += Area2D(it..it,area.z) }
            } else error("w or h != 1")
        }
        block += area.padding(1)
        val splitTime = (log2(area.size.toFloat())).toInt()
        if(splitTime <= 0) error("split time <= 0")

        fun getRoadWidth(fullWidth:IntRange):IntRange?{
            val remain = fullWidth.last - fullWidth.first + 1 - blockMinSize*2
            if(remain <= 0) return null
            val r = rand.nextFloat()
            val width = if(remain >= streetWidth && r*40f < remain) streetWidth
                else if(remain >= roadWidth && r*10f < remain) roadWidth
                else splitterWidth
            val offset = rand.nextInt(0..remain - width) + blockMinSize
            return fullWidth.first + offset..< fullWidth.first + offset + width
        }
        for(i in 0..splitTime){
            val r = rand.nextInt(block.indices)
            val area = block[r]
            area.run {
                if(rand.nextBool(w.toFloat() / (w + h).toFloat())){
                    getRoadWidth(x)?.run {
                        block.removeAt(r)
                        block += Area2D(x1..<first,z)
                        block += Area2D(last + 1..x2,z)
                        val width = last - first + 1
                        when(width){
                            streetWidth -> street
                            roadWidth -> road
                            splitterWidth -> splitter
                            else -> error("unknown road width $width")
                        } += Area2D(this,z)
                    }
                } else {
                    getRoadWidth(z)?.run {
                        block.removeAt(r)
                        block += Area2D(x,z1..<first)
                        block += Area2D(x,last + 1..z2)
                        val width = last - first + 1
                        when(width){
                            streetWidth -> street
                            roadWidth -> road
                            splitterWidth -> splitter
                            else -> error("unknown road width $width")
                        } += Area2D(x,this)
                    }
                }
            }
        }
        run {
            val wallAreaList = mutableListOf(
                Area2D(area.x, area.z1..area.z1),
                Area2D(area.x, area.z2..area.z2),
                Area2D(area.x1..area.x1, area.z),
                Area2D(area.x2..area.x2, area.z)
            )
            val gateThickList = listOf(
                area.z1 - gateThick + 1..area.z1,
                area.z1..area.z1 + gateThick - 1,
                area.x1 - gateThick + 1..area.x1,
                area.x2..area.x2 + gateThick - 1
            )
            street.getOrNull(0)?.let {
                val rotate = it.w > it.h
                val r = rand.nextBoolean()
                val index = if(rotate) if(r) 2 else 3 else if(r) 0 else 1
                val wallArea = wallAreaList[index]
                val gateThick = gateThickList[index]
                wallAreaList.removeAt(index)
                fun split(streetRange:IntRange):IntRange{
                    val streetWidth = streetRange.last - streetRange.first + 1
                    val offset = (streetWidth - gateWidth) / 2
                    return streetRange.first + offset..<streetRange.first + offset + gateWidth
                }
                if(rotate){
                    val range = split(it.z)
                    val z1 = range.first
                    val z2 = range.last
                    wallAreaList += Area2D(wallArea.x,wallArea.z.first..z1)
                    wallAreaList += Area2D(wallArea.x,z2..wallArea.z.last)
                    gate += Area2D(gateThick,range)
                } else {
                    val range = split(it.x)
                    val x1 = range.first
                    val x2 = range.last
                    wallAreaList += Area2D(wallArea.x.first..x1,wallArea.z)
                    wallAreaList += Area2D(x2..wallArea.x.last,wallArea.z)
                    gate += Area2D(range,gateThick)
                }
            }


            wallAreaList.forEach { addWall(it) }
            wallCor += Area2D(area.x1..area.x1,area.z1..area.z1)
            wallCor += Area2D(area.x2..area.x2,area.z2..area.z2)
            wallCor += Area2D(area.x1..area.x1,area.z2..area.z2)
            wallCor += Area2D(area.x2..area.x2,area.z1..area.z1)
        }
        return SplitResult(
            wall,wallCor,block,street,road,splitter,gate
        )
    }
    fun Area2D.toWall():OuterWallPiece{
        if(w == 1) return OuterWallPiece(
            StructGenConfig(Point(x1,0,z1),false,false,true), h
        )
        if(h == 1) return OuterWallPiece(
            StructGenConfig(Point(x1,0,z1),false,false,false), w
        )
        error("w or h != 1")
    }
    fun Area2D.toWallCor() = OuterWallCornerPiece(StructGenConfig(Point(x1,0,z1),false,false,false))
    fun Area2D.toStreet() = split(5).map { StreetPiece(it,w > h) }
    fun Area2D.toRoad() = split(8).map { RoadPiece(it,w > h) }
    fun Area2D.toSplitter() = split(8).map { SplitterPiece(it,w > h) }
    fun Area2D.toGate() = GatePiece(this,minY)
    val Area2D.minY get() = listOf(y(x1,z1),y(x2,z2),y(x1,z2),y(x2,z1)).min()
}