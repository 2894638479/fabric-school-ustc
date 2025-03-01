package org.schoolustc.interfaces;


import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate

interface PaletteGetter{
    fun getPalettes(): List<StructureTemplate.Palette>
}

val StructureTemplate.palettes get() = (this as PaletteGetter).getPalettes()

