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
import kotlin.math.log2

class StructureBuilder(
    private val context:GenerationContext,
    private val builder:StructurePiecesBuilder
) {
    companion object{
        const val wallMaxLength = 10 //围墙分割的最大长度
        const val wallThick = 1 //围墙厚度
        const val streetWidth = 7 //街道宽度
        const val roadWidth = 3 //小路宽度
        const val splitterWidth = 1 //分割线宽度
        const val blockMinSize = 15 //街块最小宽高
        const val gateWidth = 11 //大门宽度
        const val gateThick = 2 //大门厚度
    }
    private inline val rand get() = context.random
    fun MyStruct.add() = builder.addPiece(this)
    fun List<MyStruct>.add() = forEach { builder.addPiece(it) }
    fun y(x:Int,z:Int) = context.chunkGenerator.getBaseHeight(
        x,z,
        Heightmap.Types.WORLD_SURFACE_WG,
        context.heightAccessor,
        context.randomState
    ) - 1
    fun randArea(posX:Int,posZ:Int,size:IntProgression): Area2D {
        val width = rand.nextInt(size)
        val height = rand.nextInt(size)
        val x = posX - width / 2
        val z = posZ - height / 2
        return Area2D(x,z,width,height)
    }
    val wallArea = mutableListOf<Area2D>()
    val wallCorArea = mutableListOf<Area2D>()
    val blockArea = mutableListOf<Area2D>()
    val streetArea = RoadArea(streetWidth,5f)
    val roadArea = RoadArea(roadWidth,1f)
    val splitterArea = RoadArea(splitterWidth,0.3f)
    val gateArea = mutableListOf<Area2D>()

    private fun addWall(area:Area2D){
        if (area.w == wallThick) {
            val l = area.z.split(wallMaxLength)
            l.zipWithNext { a, b -> wallArea += Area2D(area.x,a+ wallThick..b- wallThick) }
            l.dropLast(1).drop(1).forEach { wallCorArea += Area2D(area.x,it..it) }
        } else if(area.h == wallThick) {
            val l = area.x.split(wallMaxLength)
            l.zipWithNext { a, b -> wallArea += Area2D(a+ wallThick..b- wallThick,area.z) }
            l.dropLast(1).drop(1).forEach { wallCorArea += Area2D(it..it,area.z) }
        } else error("w or h != 1")
    }

    //随机道路宽度
    private fun getRoadWidth(fullWidth:IntRange):IntRange?{
        val remain = fullWidth.last - fullWidth.first + 1 - blockMinSize*2
        val road = chooseRoad(listOf(streetArea,roadArea,splitterArea),remain) { rand.nextFloat() } ?: return null
        val offset = rand.nextInt(0..remain - road.width) + blockMinSize
        return fullWidth.first + offset..< fullWidth.first + offset + road.width
    }
    private fun splitBlockOnce(){
        val r = rand.nextInt(blockArea.indices)
        val area = blockArea[r]
        area.run {
            fun IntRange.width() = last - first + 1
            if(rand.nextBool(w.toFloat() / (w + h).toFloat())){
                getRoadWidth(x)?.run {
                    blockArea.removeAt(r)
                    blockArea += Area2D(x1..<first,z)
                    blockArea += Area2D(last + 1..x2,z)
                    when(width()){
                        streetWidth -> streetArea
                        roadWidth -> roadArea
                        splitterWidth -> splitterArea
                        else -> error("unknown road width ${width()}")
                    } += Area2D(this,z)
                }
            } else {
                getRoadWidth(z)?.run {
                    blockArea.removeAt(r)
                    blockArea += Area2D(x,z1..<first)
                    blockArea += Area2D(x,last + 1..z2)
                    when(width()){
                        streetWidth -> streetArea
                        roadWidth -> roadArea
                        splitterWidth -> splitterArea
                        else -> error("unknown road width ${width()}")
                    } += Area2D(x,this)
                }
            }
        }
    }
    private fun findGateAndWall(startArea:Area2D){
        val wallAreaList = mutableListOf(
            Area2D(startArea.x, startArea.z1..startArea.z1),
            Area2D(startArea.x, startArea.z2..startArea.z2),
            Area2D(startArea.x1..startArea.x1, startArea.z),
            Area2D(startArea.x2..startArea.x2, startArea.z)
        )
        val gateThickList = listOf(
            startArea.z1 - gateThick + 1..startArea.z1,
            startArea.z2..startArea.z2 + gateThick - 1,
            startArea.x1 - gateThick + 1..startArea.x1,
            startArea.x2..startArea.x2 + gateThick - 1
        )
        streetArea.getOrNull(0)?.let {
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
                gateArea += Area2D(gateThick,range)
            } else {
                val range = split(it.x)
                val x1 = range.first
                val x2 = range.last
                wallAreaList += Area2D(wallArea.x.first..x1,wallArea.z)
                wallAreaList += Area2D(x2..wallArea.x.last,wallArea.z)
                gateArea += Area2D(range,gateThick)
            }
        }


        wallAreaList.forEach { addWall(it) }
        wallCorArea += Area2D(startArea.x1..startArea.x1,startArea.z1..startArea.z1)
        wallCorArea += Area2D(startArea.x2..startArea.x2,startArea.z2..startArea.z2)
        wallCorArea += Area2D(startArea.x1..startArea.x1,startArea.z2..startArea.z2)
        wallCorArea += Area2D(startArea.x2..startArea.x2,startArea.z1..startArea.z1)
    }

    fun splitArea(startArea:Area2D){
        blockArea += startArea.padding(1)
        val splitTime = (log2(startArea.size.toFloat())).toInt()
        if(splitTime <= 0) error("split time <= 0")
        for(i in 0..splitTime){
            splitBlockOnce()
        }
        findGateAndWall(startArea)
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