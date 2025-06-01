package org.schoolustc.gui

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FormattedText
import net.minecraft.util.FormattedCharSequence
import net.minecraft.world.entity.player.Inventory
import org.schoolustc.fullId
import org.schoolustc.items.STUDENT_CARD
import org.schoolustc.items.StudentCardItem
import org.schoolustc.items.StudentCardItem.Companion.subjectInfo
import org.schoolustc.packet.TEACHING_TABLE_FINISH_LEARN
import org.schoolustc.packet.TEACHING_TABLE_START_LEARN
import org.schoolustc.packet.packetBuf
import org.schoolustc.questionbank.QuestionBank

class TeachingTableMenuScreen(
    menu:TeachingTableMenu,
    inventory: Inventory,
    title: Component
): AbstractContainerScreen<TeachingTableMenu>(menu,inventory,title){
    companion object {
        val backPng1 = fullId("textures/gui/container/teaching_table_1.png")
        val backPng2 = fullId("textures/gui/container/teaching_table_2.png")
        val pngWidth1 = 116
        val pngWidth2 = 176
        val pngHeight = 166
        val fullPngWidth = pngWidth1 + pngWidth2
    }
    private val minX get() = leftPos - pngWidth1
    private var leftButton = Button.builder(Component.literal("")){}.build()
        set(value) { removeWidget(field);field = value;addRenderableWidget(field) }
    private var rightButton = Button.builder(Component.literal("")){}.build()
        set(value) { removeWidget(field);field = value;addRenderableWidget(field) }
    private var pages : List<List<QuestionBank.QuestionBankClient>> = listOf()
    private var subjectButtons:List<Button> = listOf()
        set(value) { field.forEach { removeWidget(it) };field = value;field.forEach { addRenderableWidget(it) } }
    private fun subjectString(name:String) = Language.getInstance().getOrDefault("question_bank.subject.$name",name)
    private var infoStr:List<FormattedCharSequence> = listOf()
    private var subjectInfoItem:StudentCardItem.SubjectInfo.SubjectInfoItem? = null
    private fun updateSubjectInfoItem(){
        fun ret() = run {
            infoStr = listOf()
            subjectInfoItem = null
        }
        val info = subjectInfo ?: return ret()
        val subject = curBank?.subject ?: return ret()
        val infoItem = info[subject]
        val stageStr = when(infoItem.stage){
            StudentCardItem.SubjectLearnStage.NONE -> "未学习"
            StudentCardItem.SubjectLearnStage.LEARNING -> "学习中"
            StudentCardItem.SubjectLearnStage.FINISHED -> "已结课"
        }
        var str = "科目：${subjectString(subject)}\n" +
                "状态：$stageStr"
        if(infoItem.stage != StudentCardItem.SubjectLearnStage.NONE){
            str += "\n学分：${String.format("%.3f",infoItem.score)}\n" +
                    "成绩：${String.format("%.3f",infoItem.grade)}\n" +
                    "已做题数：${infoItem.questions}"
        }
        if(infoItem.stage == StudentCardItem.SubjectLearnStage.FINISHED){
            str += "\nGPA：${String.format("%.3f",infoItem.gpa)}"
        }
        infoStr = font.split(FormattedText.of(str),110)
        subjectInfoItem = infoItem
    }
    private var learnButton:Button? = null
    private fun updateLearnButton(){
        learnButton?.let { removeWidget(it) }
        val item = subjectInfoItem ?: return
        when(item.stage){
            StudentCardItem.SubjectLearnStage.NONE -> Button.builder(Component.literal("学习")){ startLearn(item.name) }
            StudentCardItem.SubjectLearnStage.LEARNING -> if(item.score + 0.00001 > 2)
                Button.builder(Component.literal("结课")){ finishLearn(item.name) } else null
            StudentCardItem.SubjectLearnStage.FINISHED -> null
        }?.let {
            val button = it.bounds(leftPos + 10,topPos + 55,30 + 16,15).build()
            learnButton = button
            addRenderableWidget(button)
        }
    }
    private var subjectInfo:StudentCardItem.SubjectInfo? = null
        set(value) {
            field = value
            updateSubjectInfoItem()
            updateLearnButton()
        }
    private var curBank:QuestionBank.QuestionBankClient? = null
        set(value) {
            field = value
            updateSubjectInfoItem()
            updateLearnButton()
        }
    private var curPage = 0
        set(value) {
            leftButton.active = false
            rightButton.active = false
            pages.ifEmpty {
                field = 0
                return
            }
            if(value < 0) field = 0
            if(value > pages.size - 1) field = pages.size - 1
            field = value
            subjectButtons = pages[field].mapIndexed { i, bank ->
                Button.builder(Component.literal(subjectString(bank.subject))){
                    subjectButtons.forEach { it.active = true }
                    it.active = false
                    curBank = bank
                }.bounds(minX + 8,topPos + 17+i*21,100,21).build().apply {
                    if(bank == curBank) active = false
                }
            }
            if(field != 0) leftButton.active = true
            if(field != pages.size - 1) rightButton.active = true
        }
    override fun init() {
        super.init()
        leftPos = (width - fullPngWidth) / 2 + pngWidth1
        topPos = (height - pngHeight) / 2
        titleLabelX = (pngWidth2 - font.width(title)) / 2
        leftButton = Button.builder(Component.literal("<")){curPage--}.bounds(minX + 8,topPos + 144,30,16).build()
        rightButton = Button.builder(Component.literal(">")){curPage++}.bounds(minX + 78,topPos + 144,30,16).build()
        curPage = curPage

        menu.studentCardContainer.addListener{
            val itemStack = it.getItem(0)
            subjectInfo = if(itemStack.`is`(STUDENT_CARD)) {
                itemStack.subjectInfo
            } else null
        }
    }
    private fun GuiGraphics.renderText(text:String,x:Int,y:Int) = drawString(font,text,x,y,0,false)
    private fun GuiGraphics.renderText(text: List<FormattedCharSequence>,x:Int,y:Int) = text.forEachIndexed { i, text ->
        drawString(font, text, x, y + i * font.lineHeight, 0, false)
    }
    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        renderBackground(guiGraphics)
        guiGraphics.blit(backPng1,minX,topPos, 0, 0, pngWidth1, pngHeight)
        guiGraphics.blit(backPng2,leftPos,topPos, 0, 0, pngWidth2, pngHeight)
        guiGraphics.renderText(Language.getInstance().getOrDefault("text.subject"),minX + 8,topPos + 6)
    }

    val banks = menu.clientQuestionBank
    init {
        pages = banks.chunked(6)
        curPage = curPage
    }
    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX,mouseY, partialTick)
        val text = "${curPage + 1}/${pages.size}"
        val width = font.width(text)
        guiGraphics.renderText(text,minX + 58 - width/2,topPos + 150)
        renderTooltip(guiGraphics, mouseX, mouseY)
        guiGraphics.renderText(infoStr,leftPos + 64,topPos + 25)
    }
    private fun startLearn(subjectName:String){
        val info = subjectInfo ?: return
        subjectInfo = info.startLearn(subjectName)
        ClientPlayNetworking.send(TEACHING_TABLE_START_LEARN, packetBuf()
            .writeVarInt(menu.containerId).writeByteArray(subjectName.encodeToByteArray()))
    }
    private fun finishLearn(subjectName: String){
        val info = subjectInfo ?: return
        subjectInfo = info.startLearn(subjectName)
        ClientPlayNetworking.send(TEACHING_TABLE_FINISH_LEARN, packetBuf()
            .writeVarInt(menu.containerId).writeByteArray(subjectName.encodeToByteArray()))
    }
}