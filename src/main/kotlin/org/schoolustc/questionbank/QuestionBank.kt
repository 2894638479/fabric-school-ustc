package org.schoolustc.questionbank

import kotlinx.serialization.Serializable

class QuestionBank(
    val subject:String,
    questions:List<Question>,
) {
    val questions = HashMap<String,Question>(questions.size * 3 / 2 + 1)
    init {
        questions.forEach { this.questions[it.question] = it }
    }

    @Serializable
    data class Question(
        val question:String,
        val choices:List<String>,
        val answer:Int
    )
}