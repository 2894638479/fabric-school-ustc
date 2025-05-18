package org.schoolustc.questionbank

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

class QuestionBank(
    val subject:String,
    val questionList:List<Question>,
) {
//    val questionMap = HashMap<String,Question>(questionList.size * 3 / 2 + 1)
//    init {
//        questionList.forEach { this.questionMap[it.question] = it }
//    }

    @Serializable
    data class Question(
        val question:String,
        val choices:List<String>,
        val answer:Int,
        val difficulty:Int
    )
    @Serializable
    class QuestionBankClient(
        val subject: String,
        val questionList: List<QuestionClient>
    ){
//        @Transient val questionMap = HashMap<String,QuestionClient>(questionList.size * 3 / 2 + 1)
//        init {
//            questionList.forEach { this.questionMap[it.question] = it }
//        }
        @Serializable
        data class QuestionClient(
            val question:String,
            val choices:List<String>,
            val difficulty:Int
        )
    }
    fun toClient() = QuestionBankClient(
        subject,
        questionList.map { QuestionBankClient.QuestionClient(it.question,it.choices,it.difficulty) }
    )
}