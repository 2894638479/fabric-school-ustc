package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStructFixedSize
import org.schoolustc.structureDsl.struct.StructBuildScope
import org.schoolustc.structureDsl.struct.StructGenConfig
import org.schoolustc.structureDsl.struct.MyStructFixedSizeInfo

class Tree(config: StructGenConfig,val treeType: ResourceKey<ConfiguredFeature<*, *>>):MyStructFixedSize(Companion,config) {
    companion object : MyStructFixedSizeInfo<Tree>("tree", Point(11,10,11)){
        override val defaultDirection = Direction2D.XPlus
        override fun loadTag(tag: CompoundTag) = Tree(tag.getConfig(),tag.getResourceKey())
        override fun Tree.saveTag(tag: CompoundTag)  {
            tag.putConfig(config)
            tag.putResourceKey(treeType)
        }
    }

    override fun StructBuildScope.build() {
        treeType place Point(5,0,5)
    }
}