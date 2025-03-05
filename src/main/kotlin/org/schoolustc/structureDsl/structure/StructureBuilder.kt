package org.schoolustc.structureDsl.structure

import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder
import org.schoolustc.structureDsl.Area2D
import org.schoolustc.structureDsl.Point
import org.schoolustc.structureDsl.nextInt
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.struct.StructGenConfig
import org.schoolustc.structurePieces.OuterWallCornerPiece
import org.schoolustc.structurePieces.OuterWallPiece

class StructureBuilder(
    private val context:GenerationContext,
    private val builder:StructurePiecesBuilder
) {
    private inline val rand get() = context.random
    fun MyStruct.add() = builder.addPiece(this)
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
        val blockMinSize = 10


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
                l.zipWithNext { a, b -> wall.add(Area2D(area.x,a+1..b-1)) }
                l.dropLast(1).drop(1).forEach { wallCor.add(Area2D(area.x,it..it)) }
            } else if(area.h == 1) {
                val l = splitWall(area.x)
                l.zipWithNext { a, b -> wall.add(Area2D(a+1..b-1,area.z)) }
                l.dropLast(1).drop(1).forEach { wallCor.add(Area2D(it..it,area.z)) }
            } else error("w or h != 1")
        }
        addWall(Area2D(area.x,area.z1..area.z1))
        addWall(Area2D(area.x,area.z2..area.z2))
        addWall(Area2D(area.x1..area.x1,area.z))
        addWall(Area2D(area.x2..area.x2,area.z))
        wallCor.add(Area2D(area.x1..area.x1,area.z1..area.z1))
        wallCor.add(Area2D(area.x2..area.x2,area.z2..area.z2))
        wallCor.add(Area2D(area.x1..area.x1,area.z2..area.z2))
        wallCor.add(Area2D(area.x2..area.x2,area.z1..area.z1))
        block.add(area.padding(1))
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
}