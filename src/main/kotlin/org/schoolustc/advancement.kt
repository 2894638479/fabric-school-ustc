package org.schoolustc

import net.minecraft.server.level.ServerPlayer


fun ServerPlayer.trigger(id:String){
    val advancement = server.advancements.getAdvancement(fullId(id))
    if (advancement != null) {
        val progress = advancements.getOrStartProgress(advancement)
        if (!progress.isDone) {
            for (criterion in progress.remainingCriteria) {
                advancements.award(advancement, criterion)
            }
        }
    }
}