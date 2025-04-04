package org.schoolustc.structs

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks.DIRT_PATH
import org.schoolustc.structureDsl.*
import org.schoolustc.structureDsl.struct.MyStruct
import org.schoolustc.structureDsl.struct.MyStructInfo
import org.schoolustc.structureDsl.struct.StructBuildScope
import org.schoolustc.structureDsl.struct.StructGenConfig
import java.lang.Math.pow
import kotlin.math.*

class Path(
    val a1: Area2D,
    val a2: Area2D,
    val d1: Direction2D,
    val d2: Direction2D,
    val block: Block
):MyStruct(Companion, StructGenConfig(getPoint(a1,a2)), getSize(a1, a2)) {
    companion object : MyStructInfo<Path>("grass_path"){
        override val defaultDirection = Direction2D.XPlus
        override fun loadTag(tag: CompoundTag): Path {
            val arr = tag.getIntArray("ar")
            arr.size.match(8)
            return Path(
                Area2D(arr[0]..arr[1],arr[2]..arr[3]),
                Area2D(arr[4]..arr[5],arr[6]..arr[7]),
                Direction2D.fromInt(tag.getInt("d1")),
                Direction2D.fromInt(tag.getInt("d2")),
                tag.getBlock()
            )
        }
        override fun Path.saveTag(tag: CompoundTag) {
            tag.putIntArray("ar", listOf(a1.x1,a1.x2,a1.z1,a1.z2,a2.x1,a2.x2,a2.z1,a2.z2))
            tag.putInt("d1",d1.toInt())
            tag.putInt("d2",d2.toInt())
            tag.putString("b",block.descriptionId)
        }
        private fun getPoint(a1: Area2D,a2: Area2D):Point{
            return Point(
                min(a1.x1,a2.x1),
                0,
                min(a1.z1,a2.z2),
            )
        }
        private fun getSize(a1: Area2D,a2: Area2D):Point{
            val p1 = getPoint(a1, a2)
            val p2 = Point(
                max(a1.x1,a2.x1),
                0,
                max(a1.z1,a2.z2),
            )
            return Point(p2.x - p1.x + 6,1,p2.z - p1.z + 6)
        }
    }
    private fun getPoints():Fillable{
        class Pt(
            val x:Double,
            val z:Double
        ){
            fun distanceTo(other:Pt) = sqrt((x - other.x).pow(2) + (z - other.z).pow(2))
            fun offset(direction: Direction2D,length:Double) = direction.run {
                if(isX) Pt(x + if(isPlus) length else -length,z)
                else Pt(x,z + if(isPlus) length else -length)
            }
            fun atDirectionOf(direction:Direction2D,other:Pt) = when(direction){
                Direction2D.XPlus -> x >= other.x
                Direction2D.XMin -> x <= other.x
                Direction2D.ZPlus -> z >= other.z
                Direction2D.ZMin -> z <= other.z
            }
        }
        val p1 = Pt(a1.x.middle,a1.z.middle)
        val p2 = Pt(a2.x.middle,a2.z.middle)
        val w1 = a1.width(d1)
        val w2 = a2.width(d2)
        val d = p1.distanceTo(p2)
        val c1 = p1.offset(d1,d/3)
        val c2 = p2.offset(d2,d/3)
        fun B(t:Double):Pt{
            val T = 1-t
            val TT = T*T
            val tt = t*t
            fun f(get:Pt.()->Double) = TT*T*p1.get() + 3*TT*t*c1.get() + 3*T*tt*c2.get() + tt*t*p2.get()
            return Pt(f{x},f{z})
        }
        var t = 0.0
        var T = 1.0
        fun w() = T*w1 + t*w2
        val step = 1.0 / (d)
        val points = object : LinkedHashSet<Point>(),Fillable{
            override fun fill(block: (Point) -> Unit) {
                for(i in this) block(i)
            }
        }
        while(t <= 1.0){
            val b = B(t)
            val w = w()/2 - 0.5
            fun atStart() = t*d < w1+1
            fun atEnd() = T*d < w2+1
            for(x in (b.x-w).roundToInt()..(b.x+w).roundToInt()){
                for(z in (b.z-w).roundToInt()..(b.z+w).roundToInt()){
                    val point = Point(x,0,z)
                    val pt = Pt(x.toDouble(),z.toDouble())
                    if(
                        (!atStart() || pt.atDirectionOf(d1,p1))
                        &&(!atEnd() || pt.atDirectionOf(d2,p2))
                    ) points += point
                }
            }
            t += step
            T = 1-t
        }
        return points
    }

    override fun StructBuildScope.build() {
        DIRT_PATH fillRawS getPoints()
    }
}