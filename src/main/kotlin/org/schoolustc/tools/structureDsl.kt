package org.schoolustc.tools

import net.minecraft.core.BlockPos
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.block.Block

class MyStructure(
    val xSize: Int,
    val ySize: Int,
    val zSize: Int,
    val generator: StructureGenerator.()->Unit
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
)

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
}