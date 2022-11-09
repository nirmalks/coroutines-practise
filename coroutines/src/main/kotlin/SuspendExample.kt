import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun main() = runBlocking{
    launch {
        val time = measureTimeMillis {
            val one = doSomethingUsefulOne()
            val two = doSomethingUsefulTwo()
            println("The answer is ${one + two}")
        }
    }

    noSuspenddoSomethingUsefulOne()
    noSuspendoSomethingUsefulTwo()
    println("after all calls")
}

suspend fun doSomethingUsefulOne(): Int {
    delay(1000L) // pretend we are doing something useful here
    println("suspend thread awake")
    return 13
}

 suspend fun doSomethingUsefulTwo(): Int {
     println("inside suspend do something useful 2")
    delay(1000L) // pretend we are doing something useful here, too
    return 29
}

fun noSuspenddoSomethingUsefulOne(): Int {
    Thread.sleep(5000L)

    println("thread awake")
    return 13
}

 fun noSuspendoSomethingUsefulTwo(): Int {
    println("inside do something useful 2")
    return 29
}