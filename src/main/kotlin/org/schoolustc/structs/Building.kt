package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items.*
import net.minecraft.world.item.enchantment.Enchantments.ALL_DAMAGE_PROTECTION
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.Blocks.AIR
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStructFixedSize
import org.schoolustc.structureDsl.struct.MyStructFixedSizeInfo
import org.schoolustc.structureDsl.struct.scope.StructBuildScopeWithConfig
import org.schoolustc.structureDsl.struct.scope.StructGenConfig

class Building(
    config: StructGenConfig,
    val height:Int,
    val flatTop:Boolean
):MyStructFixedSize(Companion,config) {
    companion object : MyStructFixedSizeInfo<Building>("building", Point(11,100,11)){
        override fun loadTag(tag: CompoundTag) = Building(
            tag.read("C"),
            tag.read("h"),
            tag.read("f")
        )
        override fun Building.saveTag(tag: CompoundTag) {
            tag.write("C",config)
            tag.write("h",height)
            tag.write("f",flatTop)
        }
        override val defaultDirection = Direction2D.ZPlus
    }

    override fun StructBuildScopeWithConfig.build() = inRelativeView {
        infix fun String.putA(y:Int) = putA(Point(0,y,0))
        infix fun String.put(y:Int) = put(Point(0,y,0))
        val normalChestPoints = List(height + 2){Point(5,it * 4 + 1,5)}
        val specialChestPoints = if(flatTop) listOf() else listOf(Point(5,height*4+9,5))

        AIR fill Area(0..<11,1..<4,0..<11)
        "building_base" put 0
        for(i in 1..height){
            "building_middle" putA i*4
        }
        "building_top" putA height*4 + 4
        val topest = if(flatTop) "building_topest_flat" else "building_topest"
        topest putA height*4 + 8

        val baseShape = Shape2D(fixedArea.toArea2D()).apply {
            delPoint(10,0)
            delPoint(9,0)
            delPoint(10,1)
            delPoint(0,10)
            delPoint(1,10)
            delPoint(0,9)
        }

        Blocks.WHITE_CONCRETE fillUnder baseShape

        normalChestPoints.forEach {
            chest(it, defaultDirection,0.1){
                rand.from(
                    { ItemStack(DIAMOND,rand from 1..<10) } to 0.15,
                    { ItemStack(GOLD_INGOT,rand from 3..<20) } to 0.3,
                    { ItemStack(IRON_INGOT,rand from 5..<40) } to 0.5,
                )()
            }
        }
        specialChestPoints.forEach {
            chest(it, defaultDirection,0.08){
                ItemStack(rand.from(
                    DIAMOND_BOOTS to 1,
                    DIAMOND_HELMET to 1,
                    DIAMOND_LEGGINGS to 0.7,
                    DIAMOND_CHESTPLATE to 0.5
                )).apply{enchant(ALL_DAMAGE_PROTECTION , rand from 1..5)}
            }
        }
    }
}