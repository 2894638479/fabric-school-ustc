package org.schoolustc.datapack

import kotlinx.serialization.Serializable

class KnowledgeBank(
    val subject:String,
    val knowledge:List<Knowledge>
) {
    @Serializable
    class Knowledge(
        val title:String,
        val info:String
    )
}