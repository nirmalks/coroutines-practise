import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        val parentJob = launch {
            delay(3000L)
            println("Job1 launched")
            val job2 = launch {
                println("Job2 launched")
                delay(3000L)
                println("Job2 is finishing")
            }
            job2.invokeOnCompletion { println("Job2 completed") }
            val job3 = launch {
                println("Job3 launched")
                delay(3000L)
                println("Job3 is finishing")
            }
            job3.cancel()
            job3.invokeOnCompletion { println("Job3 completed") }
        }
        parentJob.invokeOnCompletion { println("Job1 completed") }
        delay(500L)
        println("Job1 will be canceled")
        println("job status ${parentJob.isActive}")
        println("job cancelled? ${parentJob.isCancelled}")
//        parentJob.cancel()
        println("job cancelled? ${parentJob.isCancelled}")
    }
}