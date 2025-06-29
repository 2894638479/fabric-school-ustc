package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks.*
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStructInfo
import org.schoolustc.structureDsl.struct.MyStructWithConfig
import org.schoolustc.structureDsl.struct.scope.StructBuildScopeWithConfig
import org.schoolustc.structureDsl.struct.scope.StructGenConfig

class Hallway(config: StructGenConfig,val length:Int,val block: Block):MyStructWithConfig(Companion, Point(5,5,length),config) {
    companion object : MyStructInfo<Hallway>("hallway"){
        override val defaultDirection = Direction2D.ZPlus
        override fun loadTag(tag: CompoundTag) = Hallway(
            tag.read("C"),
            tag.read("l"),
            tag.read("b")
        )

        override fun Hallway.saveTag(tag: CompoundTag) {
            tag.write("C",config)
            tag.write("l",length)
            tag.write("b",block)
        }
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
            AIR fill Area(1..3,1..4,zRange)
            for(i in 1..length-3 step 4){
                light fill Point(2,0,i)
            }
            val glass = GLASS_PANE.state.connected(Direction2D.ZPlus,Direction2D.ZMin)
            for(i in 1..length - 3 step 4){
                glass fill Area(0.range,2..4,i..i+1)
                glass fill Area(4.range,2..4,i..i+1)
            }
        }
    }
}