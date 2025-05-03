package org.schoolustc

import kotlinx.coroutines.*
import kotlin.time.DurationUnit
import kotlin.time.measureTime
import kotlin.time.toDuration

object Profiler {
    private val scope = CoroutineScope(Dispatchers.Default)
    fun <T> task(name:String,timeOutMs:Long,block:()->T) = runBlocking {
        var finished = false
        val d = timeOutMs.toDuration(DurationUnit.MILLISECONDS)
        scope.launch {
            delay(timeOutMs)
            if(!finished) logger.warn("Profiler: $name timed out at $d")
        }
        val result : T
        val t = measureTime {
            result = block()
        }
//        logger.info("Profiler: $name finished at $t")
        finished = true
        result
    }
}