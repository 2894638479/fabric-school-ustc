package org.schoolustc.packet

import io.netty.buffer.Unpooled
import net.minecraft.network.FriendlyByteBuf
import org.schoolustc.fullId

val OPEN_QUESTION_GUI = fullId("open_question_gui")
val QUESTION_CHOOSE = fullId("question_chose")
val TEACHING_TABLE_START_LEARN = fullId("teaching_table_start_learn")
val TEACHING_TABLE_FINISH_LEARN = fullId("teaching_table_finish_learn")
val GRADING_MACHINE_GRADING = fullId("grading_machine_grading")
fun packetBuf() = FriendlyByteBuf(Unpooled.buffer())
