import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        getFlow().collect {
                value -> println("value $value")
        }
        channelFlow {
            (0..10).forEach {
                send(it)
            }
        }.flowOn(Dispatchers.Default).collect {
                value -> println("value $value")
        }
    }

}

fun getFlow() = flow<Int> {
    for (i in 1..3) {
        delay(100) // pretend we are doing something useful here
        emit(i) // emit next value
    }
}