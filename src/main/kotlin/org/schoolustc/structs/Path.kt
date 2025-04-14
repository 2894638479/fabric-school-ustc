package org.schoolustc.structs

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks.DIRT_PATH
import org.schoolustc.calc.Bezier
import org.schoolustc.calc.Pt
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.struct.MyStructInfo
import org.schoolustc.structureDsl.struct.scope.StructBuildScope
import kotlin.math.roundToInt

class Path(
    val p1: Pt,
    val p2: Pt,
    val d1: Orientation2D,
    val d2: Orientation2D,
    val w: Double = 3.0,
    val block: Block = DIRT_PATH
):MyStruct(Companion, getBoundingArea(p1,p2,w, d1, d2).toArea(maxRange)) {
    companion object : MyStructInfo<Path>("grass_path"){
        override val defaultDirection = Direction2D.XPlus
        override fun loadTag(tag: CompoundTag) = tag.run {
            Path(
                read("p1"),
                read("p2"),
                read("d1"),
                read("d2"),
                read("w"),
                read("b")
            )
        }

        override fun Path.saveTag(tag: CompoundTag) = tag.run {
            write("p1",p1)
            write("p2",p2)
            write("d1",d1)
            write("d2",d2)
            write("w",w)
            write("b",block)
        }

        fun getBoundingArea(p1:Pt,p2:Pt,w:Double,d1:Orientation2D,d2:Orientation2D):Area2D{
            val d = p1.distanceTo(p2)
            val c1 = p1.offset(d1,d/3)
            val c2 = p2.offset(d2,d/3)
            val list = listOf(p1,p2,c1,c2)
            return Area2D(
                list.minOf { it.x }.roundToInt()..list.maxOf { it.x }.roundToInt(),
                list.minOf { it.z }.roundToInt()..list.maxOf { it.z }.roundToInt()
            ).expand(w.toInt() + 1)
        }
    }
    val bezier = Bezier.byOrientation(p1,p2,d1,d2)
    val points = bezier.getNearPoints(w)
    override fun StructBuildScope.build() = inRawSurfView {
        DIRT_PATH fill points
    }
}