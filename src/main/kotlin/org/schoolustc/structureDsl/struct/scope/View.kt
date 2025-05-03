package org.schoolustc.structureDsl.struct.scope

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.Blocks.*
import net.minecraft.world.level.block.ChestBlock
import net.minecraft.world.level.block.StairBlock
import net.minecraft.world.level.block.entity.ChestBlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.*
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
import org.schoolustc.fullId
import org.schoolustc.interfaces.palettes
import org.schoolustc.logger
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.Direction2D.Companion.fromMcDirection

abstract class View(val scope: StructBuildScope) {
    private val bound = scope.boundingBox.run { Area2D(minX()..maxX(), minZ()..maxZ()) }
    protected abstract fun Point.finalXZ(): Point
    protected abstract fun Point.finalY(): Point
    private fun Point.final() = finalXZ().run { if(this in bound) finalY() else null }
    protected abstract fun Direction2D.final(): Direction2D
    protected abstract fun Area2D.final(): Area2D
    private fun Area.finalXZ() = toArea2D().final().toArea(y)
    private infix fun BlockState.setTo(finalPos: Point) = scope.world.setBlock(finalPos.blockPos,this,3)
    protected fun surfHeight(finalX:Int,finalZ: Int) = scope.world.getHeight(Heightmap.Types.WORLD_SURFACE_WG,finalX,finalZ) - 1
    fun height(x:Int,z:Int):Int? {
        return try {
            Point(x, 0, z).finalXZ().let { surfHeight(it.x, it.z) }
        } catch (_:Throwable){
            null
        }
    }
    fun block(pos: Point) = scope.world.getBlockState(pos.final()?.blockPos ?: run{ return AIR.state } )

    inline val Block.state get() = defaultBlockState()

    infix fun BlockState.fill(point: Point) { this setTo (point.final()?.apply { if(this !in bound) return } ?: return) }
    infix fun Block.fill(point: Point) = state fill point

    infix fun BlockState.fill(area: Area) { area.finalXZ().cut()?.forEach { this setTo it.finalY() } }
    infix fun Block.fill(area: Area) = state fill area
    @JvmName("fill1")
    infix fun (()-> BlockState).fill(area: Area) { area.finalXZ().cut()?.forEach { invoke() setTo it.finalY() } }
    infix fun (()-> Block).fill(area: Area) { area.finalXZ().cut()?.forEach { invoke().state setTo it.finalY() } }

    infix fun BlockState.fill(shape: Shape2D) { shape.forEach { if (it in bound) fill(it) } }
    infix fun Block.fill(shape: Shape2D) = state fill shape
    @JvmName("fill1")
    infix fun (()-> BlockState).fill(shape: Shape2D) { shape.forEach { if (it in bound) invoke() fill it } }
    infix fun (()-> Block).fill(shape: Shape2D) { shape.forEach { if (it in bound) invoke() fill it } }


    infix fun (()-> BlockState).fillUnder(points:Sequence<Point>){
        points.forEach {
            fun Point.next() = run { Point(x, y - 1, z) }
            var finalPos = it.final()?.next() ?: return@forEach
            while (!scope.world.getBlockState(finalPos.blockPos).isSolidRender(scope.world.level,finalPos.blockPos)){
                if(finalPos.y <= scope.world.level.minBuildHeight) break
                invoke() setTo finalPos
                finalPos = finalPos.next()
            }
        }
    }
    infix fun Block.fillUnder(points: Sequence<Point>) = {state} fillUnder points

    infix fun String.put(startPos: Point) = putNbtStruct(this,startPos,true)
    infix fun String.putA(startPos: Point) = putNbtStruct(this,startPos,false)

    infix fun ResourceKey<ConfiguredFeature<*, *>>.plant(pos: Point):Boolean? {
        val feature = scope.world.level.registryAccess()
            .registryOrThrow(Registries.CONFIGURED_FEATURE)
            .getHolder(this).orElse(null)
            ?.value() ?: error("feature plant error")
        return try {
            feature.place(
                scope.world,
                scope.chunkGenerator,
                scope.rand,
                pos.final()?.blockPos ?: run { return null },
            )
        } catch (_:Throwable) {
            false
        }
    }
    fun chest(
        pos: Point,
        facing: Direction2D,
        chance:Double,
        addItem:(Int)-> ItemStack
    ){
        val finalPos = pos.final() ?: return
        Blocks.CHEST.state.setValue(ChestBlock.FACING,facing.final().toMcDirection()) setTo finalPos
        val entity = scope.world.getBlockEntity(finalPos.blockPos) as ChestBlockEntity
        for(i in 0..<entity.containerSize){
            if(scope.rand.nextBool(chance)) addItem(i).let { entity.setItem(i,it) }
        }
    }
    private fun Area.cut(): Area?{
        fun IntRange.cut(other:IntRange):IntRange{
            val l = kotlin.math.max(first,other.first)
            val r = kotlin.math.min(last,other.last)
            return l..r
        }
        val x = x.cut(bound.x)
        if(x.isEmpty()) return null
        val z = z.cut(bound.z)
        if(z.isEmpty()) return null
        return Area(x, y, z)
    }

    private fun getNbtStruct(name:String): StructureTemplate?{
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
                    val finalPos = it.pos.run{Point(x,y,z)}.plus(startPos).final()
                    if(finalPos != null) it.state.final() setTo finalPos
                }
            }
        }
    }
    private fun BlockState.final() = convertDirection()
    private fun BlockState.convertDirectionBoolean(): BlockState {
        if(!hasProperty(Direction2D.ZPlus.toMcProperty())) return this
        var state = this
        Direction2D.entries.forEach{ dir ->
            state = state.setValue(dir.final().toMcProperty(),getValue(dir.toMcProperty()))
        }
        return state
    }
    private fun BlockState.convertDirectionProperty(property: DirectionProperty):BlockState{
        if(!hasProperty(property)) return this
        return setValue(property,(fromMcDirection(getValue(property)) ?: return this).final().toMcDirection())
    }
    private fun BlockState.convertStoneWallDirection():BlockState {
        var finalState = this
        Direction2D.entries.forEach { dir ->
            val prop = dir.toMcWallProperty()
            if(hasProperty(prop)){
                val value = getValue(prop)
                val prop1 = dir.final().toMcWallProperty()
                if(finalState.hasProperty(prop1))
                finalState = finalState.setValue(prop1,value)
            }
        }
        OAK_STAIRS
        return finalState
    }
    private fun BlockState.convertDirection() = this
        .convertDirectionProperty(BlockStateProperties.FACING)
        .convertDirectionProperty(BlockStateProperties.HORIZONTAL_FACING)
        .convertStoneWallDirection()
        .convertDirectionBoolean()
    fun BlockState.connected(vararg direction: Direction2D): BlockState {
        var result = this
        for (d in direction){
            result = result.setValue(d.final().toMcProperty(),true)
        }
        return result
    }
    fun BlockState.connected(wallSide: WallSide, vararg direction: Direction2D): BlockState {
        var result = this
        for (d in direction){
            result = result.setValue(d.final().toMcWallProperty(),wallSide)
        }
        return result
    }

    fun Block.stairState(facing: Direction2D, shape: StairsShape = StairsShape.STRAIGHT, half: Half = Half.BOTTOM) =
        state
            .setValue(StairBlock.FACING,facing.final().toMcDirection())
            .setValue(BlockStateProperties.STAIRS_SHAPE,shape)
            .setValue(BlockStateProperties.HALF,half)
    fun Block.leafState(persist:Boolean) = state.setValue(BlockStateProperties.PERSISTENT,persist)
    fun Block.slabState(type:SlabType) = state.setValue(BlockStateProperties.SLAB_TYPE,type)
    fun Block.doublePlantState(half:DoubleBlockHalf) = state.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF,half)
}