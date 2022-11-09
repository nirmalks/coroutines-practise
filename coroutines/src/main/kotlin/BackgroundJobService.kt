import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.ConcurrentHashMap

class BackgroundJobService : AutoCloseable {
    val jobs = ConcurrentHashMap<String, BackgroundJob<*>>()

    operator fun get(jobId: String): BackgroundJob<*>? = jobs[jobId]

    /**
     * Start background job asynchronously
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun <S : Any> startJob(
        jobId: String,
        status: S,
        block: suspend (SendChannel<(S) -> Unit>) -> Unit
    ): BackgroundJob<S> {
        val job = GlobalScope.launch {
            val progress = Channel<(S) -> Unit>(Channel.BUFFERED)
            launch {
                for (update in progress) {
                    update(status)
                }
            }
            println(block(progress))
            block(progress)
            progress.close()
            println("startJob: end {} $jobId")
        }

        val bgJob = BackgroundJob(job = job, id = jobId, status = status)
        job.invokeOnCompletion { ex -> finishJob(bgJob, ex) }
        jobs[jobId] = bgJob
        println("startJob: {}, {} $jobId, $status")
        return bgJob
    }

    private fun finishJob(bgJob: BackgroundJob<*>, ex: Throwable?) {
        val job = jobs.remove(bgJob.id) ?: kotlin.run {
            println("finishJob: Background job [{}} not found ${bgJob}")
            return
        }
        val lengthSec = job.start.until(Instant.now(), ChronoUnit.SECONDS)
        when (ex) {
            null ->
                println("finishJob: {}, {} sec, {} $job.id, $lengthSec, $job.status")
        }
    }

    override fun close() {
        jobs.values.map { it.job }.forEach { it.cancel() }
    }
}
