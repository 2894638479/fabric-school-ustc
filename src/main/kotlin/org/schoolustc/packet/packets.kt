package org.schoolustc.packet

import io.netty.buffer.Unpooled
import net.minecraft.network.FriendlyByteBuf
import org.schoolustc.fullId

val OPEN_QUESTION_GUI = fullId("open_question_gui")
val QUESTION_CHOOSE = fullId("question_chose")
val SYNC_CONTAINER_QUESTION_BANK = fullId("sync_container_question_bank")
val TEACHING_TABLE_START_LEARN = fullId("teaching_table_start_learn")
fun packetBuf() = FriendlyByteBuf(Unpooled.buffer())
