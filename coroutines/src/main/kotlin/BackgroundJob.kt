import kotlinx.coroutines.Job
import java.time.Instant

/**
 * Background job abstraction
 * Contains data, that may be useful for reporting
 */
data class BackgroundJob<S : Any>(
    val job: Job,
    val id: String,
    val start: Instant = Instant.now(),
    val status: S
) : Job by job {
    override fun toString(): String = "BackgroundJob(id='$id', start=$start, status=$status)"
}
