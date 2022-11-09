
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class Client{
    val backgroundJobService = BackgroundJobService()


    val JOB_PREFIX = "some-entity"

    val upgradeQueue = Channel<Upgrade>()
    val upgrades = mutableMapOf<Long, BackgroundJob<*>>()

    val job: BackgroundJob<*> = backupQueueJob()

    /**
     * Background job, that monitor scheduled backups for device group
     *
     */
    private fun backupQueueJob() = backgroundJobService.startJob("$JOB_PREFIX-queue", SomeStatusEntity("idle")) { status ->
        for (upgrade in upgradeQueue) {
            status.send { s -> s.status = "Backup ${upgrade.id}" }
            val res = kotlin.runCatching { performUpgrade(upgrade) }
            if (res.isFailure) {
                val errorMessage = "Failed to upgrade ${upgrade.id})}"
                status.send { s -> s.status = errorMessage }
                continue
            } else {
                status.send { s -> s.status = "idle" }
            }
        }
    }

    fun scheduleBackup(upgrade: Upgrade) {
        val job = backgroundJobService.startJob("$JOB_PREFIX-queue", SomeStatusEntity("started")) { status ->
            println("status  $status")
            while (job.isActive) {
                status.send { s -> s.status = "wait till 10s" }
                delay(10000L)
                status.send { s -> s.status = "shchedule" }
                upgradeQueue.send(upgrade)
                status.send { s -> s.status = "done upgrade" }
            }

        }
        this.upgrades[upgrade.id] = job
    }

    fun performUpgrade(upgrade: Upgrade): Result<String> {
        //mocked
        return Result.success("some success")
    }

    fun start() {
        val upgrade1 = Upgrade("idle", 1)
        val upgrade2 = Upgrade("idle", 2)
        val list = listOf(upgrade1, upgrade2)
        for (upgrade in list) {
            scheduleBackup(upgrade)
        }
    }
}

fun main() {
    val client = Client()
    client.start()
}

