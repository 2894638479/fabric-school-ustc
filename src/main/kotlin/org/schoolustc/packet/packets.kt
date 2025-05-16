package org.schoolustc.packet

import io.netty.buffer.Unpooled
import net.minecraft.network.FriendlyByteBuf
import org.schoolustc.fullId

val OPEN_QUESTION_GUI = fullId("open_question_gui")
val QUESTION_CHOOSE = fullId("question_chose")
fun packetBuf() = FriendlyByteBuf(Unpooled.buffer())
