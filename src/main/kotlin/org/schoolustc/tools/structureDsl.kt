package org.schoolustc.tools

import net.minecraft.core.BlockPos
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.block.Block
import org.schoolustc.SchoolUSTC.logger

class MyStructure(
    val xSize: Int,
    val ySize: Int,
    val zSize: Int,
    private val generator: StructureGenerator.()->Unit
){
    fun generate(
        worldGenLevel: WorldGenLevel,
        x:Int,y:Int,z:Int,
        revX:Boolean,revZ:Boolean,rotate:Boolean
    ){
        StructureGenerator(worldGenLevel, x, y, z, xSize, zSize, revX, revZ, rotate).generator()
    }
    fun generate(
        worldGenLevel: WorldGenLevel,
        pos: BlockPos,
        revX:Boolean,revZ:Boolean,rotate:Boolean
    ){
        StructureGenerator(worldGenLevel, pos.x, pos.y, pos.z, xSize, zSize, revX, revZ, rotate).generator()
    }
}

data class Point(
    val x:Int,
    val y:Int,
    val z:Int
){
    override fun toString():String{
        return "$x $y $z"
    }
}

class StructureGenerator(
    private val worldGenLevel: WorldGenLevel,
    private val x:Int,
    private val y:Int,
    private val z:Int,
    private val xSize:Int,
    private val zSize:Int,
    private val revX:Boolean,
    private val revZ:Boolean,
    private val rotate:Boolean
) {
    infix fun Block.at(pos: Point) {
        val xAdd = if(revX) xSize - pos.x else pos.x
        val zAdd = if(revZ) zSize - pos.z else pos.z
        val xx = x + if(rotate) zAdd else xAdd
        val zz = z + if(rotate) xAdd else zAdd
        val yy = y + pos.y
        worldGenLevel.setBlock(
            BlockPos(xx,yy,zz),
            defaultBlockState(),
            3
        )
    }
    infix fun Block.fill(area:Pair<Point,Point>) {
        val (p1,p2) = area
        for(i in p1.x..p2.x){
            for(j in p1.y..p2.y){
                for(k in p1.z..p2.z){
                    at(Point(i,j,k))
                }
            }
        }
    }
    //立方建筑的四面墙
    infix fun Block.fillWall(area: Pair<Point, Point>) {
        val (p1,p2) = area
        fill(Point(p1.x,p1.y,p1.z) to Point(p2.x,p2.y,p1.z))
        fill(Point(p1.x,p1.y,p2.z) to Point(p2.x,p2.y,p2.z))
        fill(Point(p1.x,p1.y,p1.z+1) to Point(p1.x,p2.y,p2.z-1))
        fill(Point(p2.x,p1.y,p1.z+1) to Point(p2.x,p2.y,p2.z-1))
    }
}