package org.schoolustc.questionbank

import kotlinx.atomicfu.atomic
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.ResourceManager
import org.schoolustc.fullId
import org.schoolustc.logger

var questionBankMap by atomic(hashMapOf<String,QuestionBank>())
    private set
var questionBankList = listOf<QuestionBank>()
    private set
var questionBankClientList = listOf<QuestionBank.QuestionBankClient>()
private fun setBanks(list:List<QuestionBank>){
    val hashMap = HashMap<String,QuestionBank>(list.size * 3 / 2 + 1)
    list.forEach { hashMap[it.subject] = it }
    questionBankList = list
    questionBankClientList = list.map { it.toClient() }
    questionBankMap = hashMap
}

fun registerReloadListener(){
    ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(object:SimpleSynchronousResourceReloadListener{
        override fun getFabricId() = fullId("question_bank_loader")
        override fun onResourceManagerReload(manager: ResourceManager) {
            val questionLists = mutableMapOf<String,List<QuestionBank.Question>>()
            manager.listResourceStacks("question_banks"){
                val path = it.path
                path.count { it == '/' } == 2 && path.endsWith(".json")
            }.forEach { (location, list) ->
                val subject = location.path.split("/")[1]
                list.forEach {
                    val stream = it.open()
                    try {
                        val question = Json.decodeFromStream<QuestionBank.Question>(stream)
                        questionLists[subject] = questionLists[subject]?.plus(question) ?: listOf(question)
                    } catch (e: Exception) {
                        logger.warn("question bank deserialize failed: ${location.path}")
                    } finally {
                        stream.close()
                    }
                }
            }
            setBanks(questionLists.map { QuestionBank(it.key,it.value) })
        }
    })
}