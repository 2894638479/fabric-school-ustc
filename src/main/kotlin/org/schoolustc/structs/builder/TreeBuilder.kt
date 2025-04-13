package org.schoolustc.structs.builder

import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import org.schoolustc.structs.Tree
import org.schoolustc.structureDsl.Point
import org.schoolustc.structureDsl.struct.scope.StructGenConfig
import org.schoolustc.structureDsl.struct.builder.MyStructBuilder

class TreeBuilder(
    val pos:Point,
    val key: ResourceKey<ConfiguredFeature<*, *>>,
    val type:Tree.TreeType
):MyStructBuilder<Tree> {
    override fun build(): Tree {
        return Tree(StructGenConfig(Point(pos.x - 5,pos.y,pos.z - 5)),key,type)
    }
}