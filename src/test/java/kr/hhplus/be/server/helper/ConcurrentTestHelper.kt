package kr.hhplus.be.server.helper

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

object ConcurrentTestHelper {
    fun executeAsyncTasks(
        taskCount: Int,
        task: () -> Unit,
    ): List<Boolean> {
        val executorService = Executors.newFixedThreadPool(200)
        try {
            val futureList =
                (0 until taskCount).map {
                    CompletableFuture.supplyAsync({
                        try {
                            task()
                            true
                        } catch (e: Exception) {
                            e.printStackTrace()
                            false
                        }
                    }, executorService)
                }
            CompletableFuture.allOf(*futureList.toTypedArray()).join()
            return futureList.map { it.get() }
        } finally {
            executorService.shutdown()
        }
    }

    fun executeAsyncTasksWithIndex(
        taskCount: Int,
        task: (Int) -> Unit,
    ): List<Boolean> {
        val executorService = Executors.newFixedThreadPool(200)
        try {
            val futureList =
                (0 until taskCount).map { index ->
                    CompletableFuture.supplyAsync({
                        try {
                            task(index)
                            true
                        } catch (e: Exception) {
                            e.printStackTrace()
                            false
                        }
                    }, executorService)
                }
            CompletableFuture.allOf(*futureList.toTypedArray()).join()
            return futureList.map { it.get() }
        } finally {
            executorService.shutdown()
        }
    }
}
