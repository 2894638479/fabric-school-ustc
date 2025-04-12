package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Blocks.*
import net.minecraft.world.level.block.state.properties.BlockStateProperties.FLOWER_AMOUNT
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStructFixedSize
import org.schoolustc.structureDsl.struct.MyStructFixedSizeInfo
import org.schoolustc.structureDsl.struct.StructBuildScope
import org.schoolustc.structureDsl.struct.StructGenConfig

class Tree(config: StructGenConfig,val key: ResourceKey<ConfiguredFeature<*, *>>,val treeType:TreeType):MyStructFixedSize(Companion,config) {
    companion object : MyStructFixedSizeInfo<Tree>("tree", Point(11,10,11)){
        override val defaultDirection = Direction2D.XPlus
        override fun loadTag(tag: CompoundTag) = Tree(
            tag.read("C"),
            tag.getResourceKey("K"),
            tag.read("T"),
        )
        override fun Tree.saveTag(tag: CompoundTag)  {
            tag.write("C",config)
            tag.putResourceKey("K",key)
            tag.write("T",treeType)
        }
    }
    enum class TreeType{
        SMALL,
        BIG,
        CHERRY,
        MUSHROOM;
        fun toInt() = when(this){
            SMALL -> 0
            BIG -> 1
            CHERRY -> 2
            MUSHROOM -> 3
        }
        companion object {
            fun fromInt(i:Int) = when(i){
                0 -> SMALL
                1 -> BIG
                2 -> CHERRY
                3 -> MUSHROOM
                else -> error("tree type error")
            }
        }
    }

    override fun StructBuildScope.build() {
        var y = 0
        var success:Boolean?
        inRelativeView {
            do {
                success = key plant Point(5,y,5)
                y++
            } while(success == false && y < 4)
            if(success == false) RED_WOOL fill Point(5,0,5)
        }
        if (treeType == TreeType.CHERRY) placeCherryFlower()
    }

    private fun StructBuildScope.placeCherryFlower() = inSurfView {
        for(i in 1..16){
            var pos = Point(rand from 1..9,1,rand from 1..9)
            fun randState() = PINK_PETALS.state.setValue(
                FLOWER_AMOUNT,
                rand from mapOf(
                    1 to 5f,
                    2 to 3f,
                    3 to 1f,
                    4 to 1f
                )
            )

            while(!block(pos).run { isAir || `is`(GRASS) || `is`(PINK_PETALS) }){
                pos = pos.offset(Direction.YPlus,1)
            }

            randState() fill pos
        }
    }
}