package org.schoolustc.structure

import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.tools.MyStructure
import org.schoolustc.tools.Point

fun classroom(xSize:Int,ySize:Int,zSize:Int) = MyStructure(xSize,ySize,zSize){
    RED_TERRACOTTA fillWall (Point(0,1,0) to Point(xSize - 1,ySize - 1,zSize - 1))
    SMOOTH_STONE fill (Point(0,0,0) to Point(xSize - 1,0,zSize - 1))
    AIR fill (Point(1,1,1) to Point(xSize - 2,ySize - 1,zSize - 2))

}