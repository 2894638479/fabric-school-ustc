package org.schoolustc.datapack

import kotlinx.atomicfu.atomic
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.ResourceManager
import org.schoolustc.fullId
import org.schoolustc.logger

var questionBankMap by atomic(hashMapOf<String, QuestionBank>())
    private set
var questionBankList = listOf<QuestionBank>()
    private set
var questionBankClientList = listOf<QuestionBank.QuestionBankClient>()

var knowledgeBankList = listOf<KnowledgeBank>()
    private set

private fun setBanks(list:List<QuestionBank>){
    val hashMap = HashMap<String, QuestionBank>(list.size * 3 / 2 + 1)
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
    ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(object:SimpleSynchronousResourceReloadListener{
        override fun getFabricId() = fullId("knowledge_bank_loader")
        override fun onResourceManagerReload(manager: ResourceManager) {
            val knowledgeLists = mutableMapOf<String,List<KnowledgeBank.Knowledge>>()
            manager.listResourceStacks("knowledge_banks"){
                val path = it.path
                path.count { it == '/' } == 2 && path.endsWith(".json")
            }.forEach { (location, list) ->
                val subject = location.path.split("/")[1]
                list.forEach {
                    val stream = it.open()
                    try {
                        val question = Json.decodeFromStream<KnowledgeBank.Knowledge>(stream)
                        knowledgeLists[subject] = knowledgeLists[subject]?.plus(question) ?: listOf(question)
                    } catch (e: Exception) {
                        logger.warn("knowledge bank deserialize failed: ${location.path}")
                    } finally {
                        stream.close()
                    }
                }
            }
            knowledgeBankList = knowledgeLists.map { KnowledgeBank(it.key,it.value) }
        }
    })
}