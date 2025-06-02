package org.schoolustc.structureDsl.struct.scope

import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.Blocks.AIR
import net.minecraft.world.level.block.Blocks.CHEST
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.ChestBlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.*
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.LegacyRandomSource
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.levelgen.structure.templatesystem.*
import org.schoolustc.fullId
import org.schoolustc.logger
import org.schoolustc.structureDsl.*


abstract class View(val scope: StructBuildScope) {
    private val bound = scope.boundingBox.run { Area2D(minX()..maxX(), minZ()..maxZ()) }
    protected abstract fun Point.finalXZ(): Point
    protected abstract fun Point.finalY(): Point
    private fun Point.final() = finalXZ().run { if(this in bound) finalY() else null }
    protected abstract fun Direction2D.final(): Direction2D
    protected abstract fun Area2D.final(): Area2D
    protected abstract val mirror:Boolean
    private val mirrorValue get() = if(mirror) Mirror.LEFT_RIGHT else Mirror.NONE
    private val rotateValue get() = when(Direction2D.XPlus.final()){
        Direction2D.XPlus -> Rotation.NONE
        Direction2D.ZPlus -> Rotation.CLOCKWISE_90
        Direction2D.ZMin -> Rotation.COUNTERCLOCKWISE_90
        Direction2D.XMin -> Rotation.CLOCKWISE_180
    }
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
    private fun blockFinalPos(finalPos:Point) = scope.world.getBlockState(finalPos.blockPos)
    fun block(pos: Point) = blockFinalPos(pos.final() ?: run{ return AIR.state } )

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

    fun fillIf(block:BlockState,pos:Point,predicate:(BlockState)->Boolean){
        val finalPos = pos.final() ?: return
        if(predicate(blockFinalPos(finalPos))){
            block setTo finalPos
        }
    }
    fun fillIf(block:BlockState,area:Area,predicate:(BlockState)->Boolean){
        area.finalXZ().cut()?.forEach {
            val finalPos = it.finalY()
            if(predicate(blockFinalPos(finalPos))){
                block setTo finalPos
            }
        }
    }
    fun fillIf(block:Block,pos:Point,predicate:(BlockState)->Boolean) = fillIf(block.state,pos,predicate)
    fun fillIf(block:Block,area:Area,predicate:(BlockState)->Boolean) = fillIf(block.state,area,predicate)

    infix fun Block.fillWall(area:Area) = state fillWall area
    infix fun BlockState.fillWall(area: Area) = {this} fillWall area
    infix fun (()->BlockState).fillWall(area:Area) = area.run {
        fill(Area(x,y,z1.range))
        fill(Area(x,y,z2.range))
        fill(Area(x1.range,y,z.padding(1)))
        fill(Area(x2.range,y,z.padding(1)))
    }

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

    infix fun String.put(startPos: Point) = putNbtStruct(this,startPos, listOf(BlockIgnoreProcessor.AIR))
    infix fun String.putA(startPos: Point) = putNbtStruct(this,startPos, listOf())

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
        CHEST.state.setValue(ChestBlock.FACING,facing.final().toMcDirection()) setTo finalPos
        val entity = scope.world.getBlockEntity(finalPos.blockPos) as ChestBlockEntity
        for(i in 0..<entity.containerSize){
            scope.rand.withChance(chance) {
                addItem(i).let { entity.setItem(i, it) }
            }
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
    class Treatment<T:BlockEntity>(
        val type:BlockEntityType<T>,
        val func:(T)->Unit
    ){
        operator fun <V> invoke(entity:V){
            (entity as? T)?.let(func)
        }
    }
    fun putNbtStruct(name:String, startPos: Point,processor:List<StructureProcessor>,afterTreatment:List<Treatment<*>>){
        val treatmentBlockPos = afterTreatment.map { mutableListOf<BlockPos>() }
        class CountProcessor : StructureProcessor() {
            override fun getType() = StructureProcessorType { Codec.unit(CountProcessor()) }
            override fun processBlock(
                levelReader: LevelReader,
                blockPos: BlockPos,
                blockPos2: BlockPos,
                structureBlockInfo: StructureTemplate.StructureBlockInfo,
                structureBlockInfo2: StructureTemplate.StructureBlockInfo,
                structurePlaceSettings: StructurePlaceSettings
            ): StructureTemplate.StructureBlockInfo {
                val id = structureBlockInfo2.nbt?.getString("id") ?: return structureBlockInfo2
                val type = BuiltInRegistries.BLOCK_ENTITY_TYPE.get(ResourceLocation(id)) ?: return structureBlockInfo2
                val index = afterTreatment.indexOfFirst { it.type == type }.also { if(it == -1) return structureBlockInfo2 }
                treatmentBlockPos[index] += structureBlockInfo2.pos
                return structureBlockInfo2
            }
        }
        putNbtStruct(name, startPos, processor + CountProcessor())
        treatmentBlockPos.forEachIndexed { i, it ->
            it.forEach {
                if(scope.boundingBox.isInside(it)) {
                    scope.world.getBlockEntity(it)?.let { entity ->
                        afterTreatment[i](entity)
                    }
                }
            }
        }
    }
    fun putNbtStruct(name:String, startPos: Point,processor:List<StructureProcessor>){
        val struct = getNbtStruct(name) ?: return logger.warn("not found structure nbt $name")
        val settings = StructurePlaceSettings()
            .setIgnoreEntities(true)
            .setMirror(mirrorValue)
            .setRotation(rotateValue)
            .setBoundingBox(bound.toArea().toBoundingBox())
            .apply { processor.forEach { addProcessor(it) } }
        val pos = startPos.finalXZ().finalY().blockPos
        struct.placeInWorld(scope.world,pos,pos,settings,LegacyRandomSource(scope.rand.nextLong()),2)
    }
    private fun BlockState.final() = convertDirection()
    private fun BlockState.convertDirection() = mirror(mirrorValue).rotate(rotateValue)
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
    fun BlockState.setHorizontalDirection(direction2D: Direction2D) =
        setValue(HorizontalDirectionalBlock.FACING,direction2D.final().toMcDirection())
    fun Block.stairState(facing: Direction2D, shape: StairsShape = StairsShape.STRAIGHT, half: Half = Half.BOTTOM) =
        state
            .setValue(StairBlock.FACING,facing.final().toMcDirection())
            .setValue(BlockStateProperties.STAIRS_SHAPE,shape)
            .setValue(BlockStateProperties.HALF,half)
    fun Block.leafState(persist:Boolean) = state.setValue(BlockStateProperties.PERSISTENT,persist)
    fun Block.slabState(type:SlabType) = state.setValue(BlockStateProperties.SLAB_TYPE,type)
    fun Block.doublePlantState(half:DoubleBlockHalf) = state.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF,half)
}