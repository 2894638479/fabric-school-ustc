package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStructInfo
import org.schoolustc.structureDsl.struct.MyStructWithConfig
import org.schoolustc.structureDsl.struct.scope.StructBuildScopeWithConfig
import org.schoolustc.structureDsl.struct.scope.StructGenConfig

class HallwayCrossing(
    config: StructGenConfig,
    val length:Int,
    val door:Int,
    val block: Block
):MyStructWithConfig(Companion, Point(5,5,length),config) {
    companion object : MyStructInfo<HallwayCrossing>("hallway_crossing"){
        override val defaultDirection = Direction2D.ZPlus
        override fun loadTag(tag: CompoundTag) = HallwayCrossing(
            tag.read("C"),
            tag.read("l"),
            tag.read("d"),
            tag.read("b")
        )

        override fun HallwayCrossing.saveTag(tag: CompoundTag) {
            tag.write("C",config)
            tag.write("l",length)
            tag.write("d",door)
            tag.write("b",block)
        }
    }

    init {
        door.match { it in (0..<length).padding(2) }
    }

    override fun StructBuildScopeWithConfig.build() {
        val wall = block
        val floor = block
        val light = SEA_LANTERN
        inRelativeView {
            val zRange = 0..<length
            wall fill Area(0.range,0..4,zRange)
            wall fill Area(4.range,0..4,zRange)
            floor fill Area(1..3,0.range,zRange)
            AIR fill Area(0.range,1..4,door-1..door+1)
            AIR fill Area(4.range,1..4,door-1..door+1)
            for(i in 2..length-2 step 4){
                light fill Point(2,0,i)
            }
            val glass = GLASS_PANE.state.connected(Direction2D.ZPlus,Direction2D.ZMin)
            for(i in 1..<door - 3 step 4){
                glass fill Area(0.range,2..4,i..i+1)
                glass fill Area(4.range,2..4,i..i+1)
            }
            for(i in door + 3..<length - 2 step 4){
                glass fill Area(0.range,2..4,i..i+1)
                glass fill Area(4.range,2..4,i..i+1)
            }
        }
    }
}