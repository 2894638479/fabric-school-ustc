package org.schoolustc.structs

import net.minecraft.data.worldgen.features.TreeFeatures
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.Blocks.*
import net.minecraft.world.level.levelgen.LegacyRandomSource
import org.schoolustc.calc.Bezier
import org.schoolustc.calc.Pt
import org.schoolustc.structs.blockbuilder.OpenRangeBuilder
import org.schoolustc.structs.blockbuilder.OpenRangeBuilder.Companion.toOpenArea
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.struct.MyStructInfo
import org.schoolustc.structureDsl.struct.scope.StructBuildScope
import kotlin.math.roundToInt
import kotlin.math.sqrt

class Park(val seed:Long,val area:Area2D):MyStruct(Companion,area.toArea(maxRange)){
    companion object : MyStructInfo<Park>("park"){
        override val defaultDirection = Direction2D.XPlus
        override fun loadTag(tag: CompoundTag) = Park(
            tag.read("s"),
            tag.read("a")
        )
        override fun Park.saveTag(tag: CompoundTag) {
            tag.write("s",seed)
            tag.write("a",area)
        }
    }

    private abstract class Item(val pos: Pt){
        abstract val r:Double
        abstract fun build()
        infix fun overlap(other:Item) = pos.distanceTo(other.pos) < r + other.r
        infix fun overlap(path:Bezier) = path.distanceTo(pos) < 1.5 + r
        fun inArea(area:Area2D) = pos.x + r < area.x2 && pos.x - r > area.x1 && pos.z + r < area.z2 && pos.z - r > area.z1
    }
    override fun StructBuildScope.build() {
        val rand = LegacyRandomSource(seed)
        val openRange = OpenRangeBuilder(area,rand) { true }.build()
        class Lantern(pos: Pt):Item(pos){
            override val r = 0.5
            override fun build() = inRawSurfView {
                val lantern = if(rand.nextBool(0.9)) LANTERN else SOUL_LANTERN
                lantern fill Point(pos.x.roundToInt(),1,pos.z.roundToInt())
            }
        }
        class Circle(val r:Double,val pos:Pt)
        fun Item.circle() = Circle(r, pos)
        infix fun RandomSource.from(c:Circle) = c.run {
            while (true) {
                val x = rand from pos.x - r..pos.x + r
                val z = rand from pos.z - r..pos.z + r
                val pt = Pt(x,z)
                if(pt.distanceTo(pos) < r) return@run pt
            }
            return c.pos
        }
        val flowers1 = mapOf(
            DANDELION to 1,
            POPPY to 1,
        )
        val flowers2 = mapOf(
            BLUE_ORCHID to 1,
            ALLIUM to 1,
            CORNFLOWER to 1,
        )
        val flowers3 = mapOf(
            AZURE_BLUET to 1,
            OXEYE_DAISY to 1,
            LILY_OF_THE_VALLEY to 1
        )
        val flowers4 = mapOf(
            RED_TULIP to 1,
            PINK_TULIP to 1,
            WHITE_TULIP to 1,
            ORANGE_TULIP to 2
        )
        class Flowers(pos: Pt,size:Double):Item(pos){
            override val r = size
            override fun build() = inRawSurfView {
                fun flower() = rand from (rand from mapOf(
                    flowers1 to 1,
                    flowers2 to 1,
                    flowers3 to 1,
                    flowers4 to 1,
                ))
                fun place() = rand from circle()
                repeat((r*r/2).toInt()){
                    flower() fill place().toPoint(1)
                }
            }
        }
        class Pavilion(pos:Pt,direction: Direction2D):Item(pos){
            override val r = 5.0
            override fun build() {
                inRawView {
                    val y = height(pos.x.roundToInt(),pos.z.roundToInt()) ?: return
                    STONE_BRICKS fill area.toArea(y.range)
                }
            }
            val area = Area2D(pos.x.toInt().range.expand(2),pos.z.toInt().range.expand(2))
        }
        class Shrub(pos:Pt):Item(pos){
            override val r = 5.0
            override fun build() {
                inRawSurfView {
                    TreeFeatures.JUNGLE_BUSH plant pos.toPoint(1)
                }
            }
        }
        val paths = mutableListOf<Path>()
        val pavilions = mutableListOf<Pavilion>()
        val items = mutableListOf<Item>()
        repeat(sqrt(area.size.toDouble()).toInt() / 15){
            val pos = rand from area.padding(7)
            val pt = Pt(pos.first.toDouble(),pos.second.toDouble())
            if(pavilions.firstOrNull { it.pos.distanceTo(pt) < 6 } == null)
            pavilions += Pavilion(pt,rand from Direction2D.entries)
        }
        items += pavilions
        pavilions.forEach { pav ->
            val min = openRange.minByOrNull { it.toOpenArea(area).middle().distanceTo(pav.pos) }
            val other = (rand from openRange).takeIf { it.toOpenArea(area).middle().distanceTo(pav.pos) in 8.0..30.0 }
            setOf(min,other).filterNotNull().forEach {
                val openArea = it.toOpenArea(area)
                val o = pav.pos.orientationTo(openArea.middle())
                paths += Path(
                    openArea.middle(),
                    pav.area.sliceEnd(o.nearestDirection(),1).middle(),
                    it.first.reverse.toOrientation(),
                    o
                )
            }
        }
        pavilions.forEach { i1 ->
            pavilions.forEach { i2 ->
                if(i1.pos.distanceTo(i2.pos) in 8.0..33.0){
                    paths += Path(i1.area.middle(),i2.area.middle(),i1.pos.orientationTo(i2.pos),i2.pos.orientationTo(i1.pos))
                }
            }
        }
        fun addItem(i:Item){
            items.forEach { if(i overlap it) return }
            paths.forEach { if(i overlap it.bezier) return }
            if(i.inArea(area)) return
            items += i
        }
        paths.forEach {
            val b = it.bezier
            val split = 8 / b.d
            var t = 0.0
            while(t <= 1){
                val orientation = b.orientation(t)
                val pt = b(t)
                addItem(Lantern(pt.offset(orientation.left(90.0),2.5)))
                addItem(Lantern(pt.offset(orientation.left(-90.0),2.5)))
                t += split
            }
        }
        repeat(area.size / 60){
            addItem(Flowers(Pt(rand from area.x.toDouble(),rand from area.z.toDouble()),rand from 3.0..6.0))
        }
        repeat(area.size / 40){
            addItem(Shrub(Pt(rand from area.x.toDouble(),rand from area.z.toDouble())))
        }
        inRawSurfView {
            paths.forEach { it.run { build() } }
            items.forEach { it.build() }
        }
    }
}