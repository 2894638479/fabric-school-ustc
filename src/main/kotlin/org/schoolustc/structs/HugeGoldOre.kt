package org.schoolustc.structs

import net.minecraft.data.worldgen.features.FeatureUtils
import net.minecraft.nbt.CompoundTag
import org.schoolustc.fullId
import org.schoolustc.logger
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.struct.MyStructInfo
import org.schoolustc.structureDsl.struct.scope.StructBuildScope

class HugeGoldOre(val pos:Point):MyStruct(Companion,pos.run { Area(x.range,y.range,z.range) }) {
    companion object : MyStructInfo<HugeGoldOre>("huge_gold_ore"){
        override val defaultDirection = Direction2D.XPlus
        override fun HugeGoldOre.saveTag(tag: CompoundTag) = tag.write("p",pos)
        override fun loadTag(tag: CompoundTag) = HugeGoldOre(tag.read("p"))
    }

    override fun StructBuildScope.build() {
        inRawView {
            FeatureUtils.createKey(fullId("huge_ore_gold").toString()) plant pos
        }
    }
}