package org.schoolustc.packet

import kotlinx.atomicfu.atomic
import kotlinx.serialization.json.Json
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import org.schoolustc.questionbank.QuestionBank

var cachedQuestionBank:List<QuestionBank.QuestionBankClient> by atomic(listOf())

fun registerQuestionBankPacket(){
    ClientPlayNetworking.registerGlobalReceiver(SYNC_CONTAINER_QUESTION_BANK){ minecraft, clientPacketListener, friendlyByteBuf, packetSender ->
        val json = friendlyByteBuf.readByteArray()
        val banks = Json.decodeFromString<List<QuestionBank.QuestionBankClient>>(String(json))
        cachedQuestionBank = banks
    }
}