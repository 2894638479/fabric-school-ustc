package org.schoolustc.structureDsl.struct

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks.CHEST
import net.minecraft.world.level.block.ChestBlock
import net.minecraft.world.level.block.StairBlock
import net.minecraft.world.level.block.entity.ChestBlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties.*
import net.minecraft.world.level.block.state.properties.Half
import net.minecraft.world.level.block.state.properties.StairsShape
import net.minecraft.world.level.block.state.properties.WallSide
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
import org.schoolustc.fullId
import org.schoolustc.interfaces.palettes
import org.schoolustc.logger
import org.schoolustc.structureDsl.*


class StructBuildScope(
    val world:WorldGenLevel,
    val config: StructGenConfig,
    val rand:RandomSource,
    val boundingBox: BoundingBox,
    val chunkGenerator: ChunkGenerator
) {

    private inline val Point.finalPos get() = finalPos(config){ _,_ -> config.pos.y + y }
    private fun y(x:Int,z:Int) = world.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z) - 1
    private inline val Point.finalSurfacePos get() = finalPos(config,::y)
    private inline val Direction2D.finalDirection get() = applyConfig(config)
    @JvmName("finalSurfacePosGetter") fun Point.getFinalSurfacePos() = finalSurfacePos
    inline val Block.state get() = defaultBlockState()

    abstract class View(val scope:StructBuildScope) {
        protected val pos = scope.config.pos
        private val bound = scope.boundingBox.run { Area2D(minX()..maxX(),minZ()..maxZ()) }
        protected abstract fun Point.final():Point
        protected abstract fun Point.finalY():Int
        protected abstract fun Direction2D.final():Direction2D
        private infix fun BlockState.setTo(finalPos: Point) = scope.world.setBlock(finalPos.blockPos,this,3)
        protected fun surfHeight(x:Int,z: Int) = scope.world.getHeight(Heightmap.Types.WORLD_SURFACE_WG,x,z) - 1
        fun block(pos:Point) = scope.world.getBlockState(pos.final().blockPos)

        private inline val Block.state get() = defaultBlockState()

        infix fun BlockState.fill(point:Point) { this setTo point.final().apply { if(this !in bound) return } }
        infix fun Block.fill(point:Point) = state fill point

        infix fun BlockState.fill(area:Area) { area.finalXZ().cut()?.forEach { this setTo Point(it.x,it.finalY(),it.z) } }
        infix fun Block.fill(area:Area) = state fill area
        @JvmName("fill1")
        infix fun (()->BlockState).fill(area:Area) { area.finalXZ().cut()?.forEach { invoke() setTo Point(it.x,it.finalY(),it.z) } }
        infix fun (()->Block).fill(area:Area) { area.finalXZ().cut()?.forEach { invoke().state setTo Point(it.x,it.finalY(),it.z) } }

        infix fun BlockState.fill(shape:Shape2D) { shape.forEach { if (it in bound) fill(it) } }
        infix fun Block.fill(shape:Shape2D) = state fill shape
        @JvmName("fill1")
        infix fun (()->BlockState).fill(shape:Shape2D) { shape.forEach { if (it in bound) invoke() fill it } }
        infix fun (()->Block).fill(shape:Shape2D) { shape.forEach { if (it in bound) invoke() fill it } }


        infix fun (()->BlockState).fillUnder(points:Sequence<Point>){
            points.forEach {
                fun Point.next() = run { Point(x,y-1,z) }
                var finalPos = it.final().next()
                while (scope.world.getBlockState(finalPos.blockPos).isAir){
                    invoke() setTo finalPos
                    finalPos = finalPos.next()
                }
            }
        }
        infix fun Block.fillUnder(points: Sequence<Point>) = {state} fillUnder points

        infix fun String.put(startPos: Point) = putNbtStruct(this,startPos,true)
        infix fun String.putA(startPos: Point) = putNbtStruct(this,startPos,false)

        infix fun ResourceKey<ConfiguredFeature<*, *>>.plant(pos:Point):Boolean? = scope
            .world
            .level
            .registryAccess()
            .registryOrThrow(Registries.CONFIGURED_FEATURE)
            .getHolder(this)
            .orElse(null)
            ?.value()
            ?.place(
                scope.world,
                scope.chunkGenerator,
                scope.rand,
                pos.final().also { if (it !in bound) return null }.blockPos,
            ) ?: error("tree place error")
        fun chest(
            pos:Point,
            facing:Direction2D,
            addItem:(Int)->ItemStack?
        ){
            val finalPos = pos.final()
            if(finalPos !in bound) return
            CHEST.state.setValue(ChestBlock.FACING,facing.final().toMcDirection()) setTo finalPos
            val entity = scope.world.getBlockEntity(finalPos.blockPos) as ChestBlockEntity
            for(i in 0..<entity.containerSize){
                addItem(i)?.let { entity.setItem(i,it) }
            }
        }
        private fun Area.finalXZ():Area{
            val config = scope.config
            val xAdd = if(config.revX) -x.last..-x.first else x
            val zAdd = if(config.revZ) -z.last..-z.first else z
            val finalX = (if (config.rotate) zAdd else xAdd).offset(config.pos.x)
            val finalZ = (if (config.rotate) xAdd else zAdd).offset(config.pos.z)
            return Area(finalX,y,finalZ)
        }
        private fun Area.cut():Area?{
            fun IntRange.cut(other:IntRange):IntRange{
                val l = kotlin.math.max(first,other.first)
                val r = kotlin.math.min(last,other.last)
                return l..r
            }
            val x = x.cut(bound.x)
            if(x.isEmpty()) return null
            val z = z.cut(bound.z)
            if(z.isEmpty()) return null
            return Area(x,y,z)
        }

        private fun getNbtStruct(name:String):StructureTemplate?{
            return (scope.world.server ?: return null)
                .structureManager
                .get(fullId(name))
                .orElse(null)
        }
        private fun putNbtStruct(name:String, startPos: Point, filterAir:Boolean){
            val struct = getNbtStruct(name) ?: return logger.warn("not found structure nbt $name")
            struct.palettes.forEach {
                it.blocks().forEach {
                    if(!(filterAir && it.state.isAir)){
                        val finalPos = it.pos.point.plus(startPos).final()
                        if(finalPos in bound) it.state.final() setTo finalPos
                    }
                }
            }
        }
        private fun BlockState.final() = convertDirection()
        private fun BlockState.convertDirection():BlockState{
            if(!hasProperty(EAST)) return this
            var state = this
            Direction2D.entries.forEach{ dir ->
                state = state.setValue(dir.final().toMcProperty(),getValue(dir.toMcProperty()))
            }
            return state
        }
    }

    class RelativeView(scope: StructBuildScope): View(scope){
        override fun Point.final() = finalPos(scope.config) { _,_ -> pos.y + y }
        override fun Point.finalY() = y + pos.y
        override fun Direction2D.final() = applyConfig(scope.config)
    }
    class SurfView(scope: StructBuildScope): View(scope){
        override fun Point.final() = finalPos(scope.config) { x,z -> y + surfHeight(x,z)}
        override fun Point.finalY() = y + surfHeight(x,z)
        override fun Direction2D.final() = applyConfig(scope.config)
    }
    class RawView(scope: StructBuildScope): View(scope){
        override fun Point.final() = this
        override fun Point.finalY() = y
        override fun Direction2D.final() = this
    }
    class RawSurfView(scope: StructBuildScope): View(scope){
        override fun Point.final() = Point(x,y + surfHeight(x,z),z)
        override fun Point.finalY() = y + surfHeight(x,z)
        override fun Direction2D.final() = this
    }

    inline fun inRelativeView(task:RelativeView.()->Unit) = RelativeView(this).task()
    inline fun inSurfView(task:SurfView.()->Unit) = SurfView(this).task()
    inline fun inRawView(task:RawView.()->Unit) = RawView(this).task()
    inline fun inRawSurfView(task:RawSurfView.()->Unit) = RawSurfView(this).task()


    fun BlockState.connected(vararg direction:Direction2D):BlockState {
        var result = this
        for (d in direction){
            result = result.setValue(d.finalDirection.toMcProperty(),true)
        }
        return result
    }
    fun BlockState.connected(wallSide: WallSide,vararg direction:Direction2D):BlockState {
        var result = this
        for (d in direction){
            result = result.setValue(d.finalDirection.toMcWallProperty(),wallSide)
        }
        return result
    }
    inline val Block.connectedX get() = state.connected(Direction2D.XMin,Direction2D.XPlus)
    inline val Block.connectedZ get() = state.connected(Direction2D.ZMin,Direction2D.ZPlus)

    fun Block.stairState(facing:Direction2D,shape:StairsShape = StairsShape.STRAIGHT,half: Half = Half.BOTTOM) =
        state
            .setValue(StairBlock.FACING,facing.finalDirection.toMcDirection())
            .setValue(STAIRS_SHAPE,shape)
            .setValue(HALF,half)
    fun Block.leafState(persist:Boolean) = state.setValue(PERSISTENT,persist)
}



