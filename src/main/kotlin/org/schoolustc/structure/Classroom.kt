package org.schoolustc.structure

import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.tools.MyStructure
import org.schoolustc.tools.Point
import org.schoolustc.tools.StructureGenerator

val classroom = MyStructure(2,3,3){
    DIAMOND_BLOCK at Point(0,0,0)
    REDSTONE_BLOCK at Point(1,0,1)
    IRON_BLOCK at Point(0,2,0)
    GOLD_BLOCK at Point(1,0,2)
}