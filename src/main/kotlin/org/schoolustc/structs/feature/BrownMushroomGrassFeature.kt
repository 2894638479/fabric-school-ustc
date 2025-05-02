package org.schoolustc.structs.feature

import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.data.worldgen.features.FeatureUtils
import net.minecraft.util.RandomSource
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Blocks.TALL_GRASS
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import net.minecraft.world.level.levelgen.feature.HugeBrownMushroomFeature
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration
import org.schoolustc.fullId

class BrownMushroomGrassFeature(codec: Codec<HugeMushroomFeatureConfiguration>) : HugeBrownMushroomFeature(codec) {
    companion object{
        val id = fullId("huge_brown_mushroom_grass")
        val key = FeatureUtils.createKey(id.toString())
    }
    override fun makeCap(
        levelAccessor: LevelAccessor,
        randomSource: RandomSource,
        blockPos: BlockPos,
        i: Int,
        mutableBlockPos: BlockPos.MutableBlockPos,
        hugeMushroomFeatureConfiguration: HugeMushroomFeatureConfiguration
    ) {
        super.makeCap(levelAccessor, randomSource, blockPos, i, mutableBlockPos, hugeMushroomFeatureConfiguration)
        for(j in -2..2){
            for(k in -2..2){
                fun notSolid() = !levelAccessor.getBlockState(mutableBlockPos).isSolidRender(levelAccessor, mutableBlockPos)
                val stateHigh = TALL_GRASS.defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF,DoubleBlockHalf.UPPER)
                val stateLow = TALL_GRASS.defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF,DoubleBlockHalf.LOWER)
                mutableBlockPos.setWithOffset(blockPos,j,i+1,k)
                if(notSolid()) setBlock(levelAccessor,mutableBlockPos,stateLow)
                mutableBlockPos.setWithOffset(blockPos,j,i+2,k)
                if(notSolid()) setBlock(levelAccessor,mutableBlockPos,stateHigh)
            }
        }
    }
}