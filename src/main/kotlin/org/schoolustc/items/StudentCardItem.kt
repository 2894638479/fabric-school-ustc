package org.schoolustc.items

import kotlinx.serialization.Serializable
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import org.schoolustc.items.MoneyCardItem.Companion.ownerName
import org.schoolustc.structureDsl.itemMember


class StudentCardItem(properties: Properties):Item(properties) {
    companion object {
        var ItemStack.subjectInfo by itemMember<SubjectInfo>("subject_info")
    }
    @Serializable
    enum class SubjectLearnStage{NONE,LEARNING,FINISHED}
    @Serializable
    class SubjectInfo(
        val subjects:List<SubjectInfoItem> = listOf()
    ){
        @Serializable
        class SubjectInfoItem(
            val name:String,
            val score:Double = 0.0,
            val grade:Double = 0.0,// 0~100
            val questions:Int = 0,
            val stage:SubjectLearnStage = SubjectLearnStage.NONE
        ){
            companion object {
                val gpaMap = listOf(
                    95 to 4.3,
                    90 to 4.0,
                    85 to 3.7,
                    81 to 3.3,
                    77 to 3.0,
                    74 to 2.7,
                    71 to 2.3,
                    67 to 2.0,
                    64 to 1.7,
                    63 to 1.5,
                    60 to 1.3,
                    0 to 1.0,
                )
            }
            val gpa get() = gpaMap.firstOrNull { grade + 0.0001 > it.first }?.second ?: 0.0
            fun grade(score:Double,correct:Boolean):SubjectInfoItem{
                if(score == 0.0) return this
                val total = this.score * grade + if(correct) score * 100 else 0.0
                val totalScore = score + this.score
                val finalGrade = total / totalScore
                return SubjectInfoItem(name, totalScore, finalGrade, questions + 1, stage)
            }
            fun finishLearn() = SubjectInfoItem(name, score, grade, questions, SubjectLearnStage.FINISHED)
        }
        val score:Double get() = subjects.sumOf { it.score }
        val gpa:Double? get() = if(score == 0.0) null else subjects.sumOf { it.gpa * it.score } / score
        val grade:Double? get() = if(score == 0.0) null else subjects.sumOf { it.grade * it.score } / score
        val subjectCount:Int get() = subjects.count { it.stage == SubjectLearnStage.FINISHED }
        fun startLearn(name:String):SubjectInfo{
            val item = subjects.firstOrNull { it.name == name } ?: return SubjectInfo(this.subjects + SubjectInfoItem(
                name,stage = SubjectLearnStage.LEARNING
            ))
            return SubjectInfo(
                subjects.map {
                    if(it == item && it.stage == SubjectLearnStage.NONE){
                        SubjectInfoItem(
                            it.name,it.score,it.grade,it.questions,SubjectLearnStage.LEARNING
                        )
                    } else it
                }
            )
        }
        fun finishLearn(name: String):SubjectInfo{
            return SubjectInfo(
                subjects.map {
                    if(it.name == name) it.finishLearn() else it
                }
            )
        }
        operator fun get(subjectName:String) = subjects.firstOrNull{ it.name == subjectName } ?: SubjectInfoItem(subjectName)
        fun grading(name:String,difficulty:Int,correct:Boolean):SubjectInfo{
            val item = subjects.firstOrNull { it.name == name }
            val score = getScoreFromDifficulty(difficulty)
            return if(item == null) SubjectInfo(subjects + SubjectInfoItem(name).grade(score, correct))
            else SubjectInfo(subjects.map {
                if(it == item) it.grade(score, correct)
                else it
            })
        }
        fun getScoreFromDifficulty(difficulty: Int) = difficulty.toDouble() / 20
    }
    override fun appendHoverText(stack: ItemStack, level: Level?, list: MutableList<Component>, flag: TooltipFlag) {
        list += Component.literal("所有者：${stack.ownerName}")
        val subjectInfo = stack.subjectInfo
        list += Component.literal("学分：${String.format("%.3f",subjectInfo.score)}")
        list += Component.literal("GPA：${String.format("%.3f",subjectInfo.gpa)}")
        list += Component.literal("平均成绩：${String.format("%.3f",subjectInfo.grade)}")
        list += Component.literal("已学科目：${subjectInfo.subjectCount}")
    }

    override fun getDefaultInstance() = super.getDefaultInstance().apply {
        subjectInfo = SubjectInfo(listOf())
    }
}