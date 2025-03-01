package org.schoolustc.mixin;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;
import org.schoolustc.interfaces.PaletteGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(StructureTemplate.class)
abstract class GetStructureTemplatePaletteMixin implements PaletteGetter {
    @Accessor("palettes")
    public abstract @NotNull List<StructureTemplate.Palette> getPalettes();
}