package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks.*
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleProcessor
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStructFixedSize
import org.schoolustc.structureDsl.struct.MyStructFixedSizeInfo
import org.schoolustc.structureDsl.struct.scope.StructBuildScopeWithConfig
import org.schoolustc.structureDsl.struct.scope.StructGenConfig

class StairwellTop(config: StructGenConfig,val block: Block):MyStructFixedSize(Companion,config) {
    companion object : MyStructFixedSizeInfo<StairwellTop>(
        "stairwell_top",
        Point(11,2,7)
    ){
        override val defaultDirection = Direction2D.ZMin
        override fun StairwellTop.saveTag(tag: CompoundTag) {
            tag.write("C",config)
            tag.write("b",block)
        }
        override fun loadTag(tag: CompoundTag) = StairwellTop(tag.read("C"),tag.read("b"))
    }

    override fun StructBuildScopeWithConfig.build() {
        inRelativeView {
            putNbtStruct("stairwell_top",Point(0,0,0),listOf(RuleProcessor(listOf(ProcessorRule(
                BlockMatchTest(RED_CONCRETE),AlwaysTrueTest.INSTANCE,block.state
            )))))
        }
    }
}