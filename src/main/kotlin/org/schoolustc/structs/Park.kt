package org.schoolustc.structs

import net.minecraft.data.worldgen.features.TreeFeatures
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.Blocks.*
import net.minecraft.world.level.block.state.properties.Half
import net.minecraft.world.level.block.state.properties.SlabType
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

    override val customRandomSource get() = LegacyRandomSource(seed)
    override fun StructBuildScope.build() {
        inRawSurfView {
            GRASS_BLOCK fill area.toArea(0.range)
        }
        val openRange = OpenRangeBuilder(area,rand) { true }.build()
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
        val trees = mapOf(
            TreeFeatures.OAK_BEES_005 to 1,
            TreeFeatures.FANCY_OAK_BEES to 0.3,
            TreeFeatures.FANCY_OAK to 0.2,
        )
        class Lantern(pos: Pt):Item(pos){
            override val r = 0.5
            override fun build() = inRawSurfView {
                val lantern = if(rand.nextBool(0.9)) LANTERN else SOUL_LANTERN
                lantern fill pos.toPoint(1)
            }
        }
        class Tree(pos:Pt):Item(pos){
            override val r = 0.5
            override fun build() {
                inRawSurfView {
                    val treeType = rand from trees
                    treeType plant pos.toPoint(1)
                }
            }
        }
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
                repeat((r*r).toInt()){
                    flower() fill place().toPoint(1)
                }
                val tree = mutableSetOf<Pt>()
                repeat((r*r/4).toInt()){
                    tree += place()
                }
                tree.forEach { Tree(it).build() }
            }
        }
        class Pavilion(pos:Pt):Item(pos){
            override val r = 5.0
            override fun build() {
                inRawView {
                    val x = pos.x.roundToInt()
                    val z = pos.z.roundToInt()
                    val y = height(x,z) ?: return
                    AIR fill area.toArea(y..y+3)
                    "pavilion" put Point(x - 3,y,z - 3)
                }
            }
            val area = Area2D(pos.x.toInt().range.expand(2),pos.z.toInt().range.expand(2))
        }
        class Shrub(pos:Pt):Item(pos){
            override val r = 2.5
            override fun build() {
                inRawSurfView {
                    val result = TreeFeatures.JUNGLE_BUSH plant pos.toPoint(1)
                    if(result == false){
                        Flowers(pos,2.0).build()
                    }
                }
            }
        }
        class SmallTrees(pos:Pt,r:Double):Item(pos){
            override val r = r
            override fun build() = inRawView {
                val circle = circle().run{Circle(r - 0.5,pos)}
                val places = mutableSetOf<Point>()
                repeat((r*r*1.5).toInt()){
                    val pos = (rand from circle).toPoint {x,z -> height(x,z) ?: return@repeat }
                    places += pos
                }
                val trees = places.map { it to (rand from 4..6) }
                trees.forEach { (pos,h) ->
                    val leaf = SPRUCE_LEAVES.leafState(true)
                    pos.run {
                        leaf fill Point(x,y+h,z)
                        val midY = y+h-2..y+h-1
                        leaf fill Area(x-1..x+1,midY,z..z)
                        leaf fill Area(x..x,midY,z-1..z+1)
                        SPRUCE_LOG fill Area(x..x,y+1..y+h-1,z..z)
                    }
                }
            }
        }
        class Stair(pos:Pt,val direction:Direction2D):Item(pos){
            override val r = 1.5
            override fun build() = inRawView {
                val point = pos.toPoint{x,z -> (height(x,z) ?: return@inRawView) + 1 }
                val slab = POLISHED_ANDESITE_SLAB.slabState(SlabType.TOP)
                slab fill point
                val stairL = POLISHED_ANDESITE_STAIRS.stairState(direction.left, half = Half.TOP)
                stairL fill point.offset(direction.left.toDirection(),1)
                val stairR = POLISHED_ANDESITE_STAIRS.stairState(direction.right,half = Half.TOP)
                stairR fill point.offset(direction.right.toDirection(),1)
            }
        }
        val paths = mutableListOf<Path>()
        val pavilions = mutableListOf<Pavilion>()
        val items = mutableListOf<Item>()
        repeat(sqrt(area.size.toDouble()).toInt() / 15){
            val pos = rand from area.padding(7)
            val pt = Pt(pos.first.toDouble(),pos.second.toDouble())
            if(pavilions.firstOrNull { it.pos.distanceTo(pt) < 6 } == null)
            pavilions += Pavilion(pt)
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
        pavilions.forEachIndexed { i1, it1 ->
            pavilions.forEachIndexed { i2, it2 ->
                if(i2 > i1) {
                    if (it1.pos.distanceTo(it2.pos) in 8.0..33.0) {
                        paths += Path(
                            it1.area.middle(),
                            it2.area.middle(),
                            it1.pos.orientationTo(it2.pos),
                            it2.pos.orientationTo(it1.pos)
                        )
                    }
                }
            }
        }
        fun addItem(i:Item){
            items.forEach { if(i overlap it) return }
            paths.forEach { if(i overlap it.bezier) return }
            if(!i.inArea(area)) return
            items += i
        }
        paths.forEach {
            val b = it.bezier
            val split = 10 / b.d
            var t = split / 2
            while(t <= 1){
                val orientation = b.orientation(t)
                val pt = b(t)
                val left = if(rand.nextBool(0.5)) 90.0 else -90.0
                val o = orientation.left(left)
                addItem(Stair(pt.offset(o,3.0),o.nearestDirection()))
                t += split
            }
        }
        run {
            val stairs = mutableListOf<Stair>()
            repeat((area.xl + area.zl) / 10){
                val d = rand from Direction2D.entries
                val index = rand from area.range(d.left).padding(3)
                val range = index.range.expand(1)
                openRange.forEach {
                    if(d == it.first && (range overlap it.second)) return@repeat
                }
                val pt = (d to range).toOpenArea(area).middle()
                val stair = Stair(pt,d)
                stairs.forEach {
                    if(it overlap stair) return@repeat
                }
                stairs += stair
            }
            items += stairs
        }
        paths.forEach {
            val b = it.bezier
            val split = 7 / b.d
            var t = split / 2
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
            addItem(SmallTrees(Pt(rand from area.x.toDouble(),rand from area.z.toDouble()),rand from 4.0..7.0))
        }
        repeat(area.size / 40){
            addItem(Shrub(Pt(rand from area.x.toDouble(),rand from area.z.toDouble())))
        }
        repeat(area.size / 80){
            addItem(Tree(Pt(rand from area.x.toDouble(),rand from area.z.toDouble())))
        }
        inRawSurfView {
            paths.forEach { it.run { build() } }
            items.forEach { it.build() }
        }
    }
}