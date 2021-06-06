package ru.ra66it.updaterforspotify.presentation.workers

import androidx.work.*
import java.util.concurrent.TimeUnit

class WorkersEnqueueManager(private val workManager: WorkManager) {

    fun enqueuePeriodicCheckingIfDontExist(isEnableNotification: Boolean, days: Long) {
        if (isEnableNotification.not()) return
        enqueuePeriodicChecking(days, ExistingPeriodicWorkPolicy.KEEP)
    }

    fun enqueuePeriodicChecking(
        days: Long,
        existingPeriodicWorkPolicy: ExistingPeriodicWorkPolicy
    ) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val work = PeriodicWorkRequestBuilder<CheckingWorker>(days, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(CheckingWorker.TAG, existingPeriodicWorkPolicy, work)
    }

    fun stopPeriodicChecking() {
        workManager.cancelUniqueWork(CheckingWorker.TAG)
    }

}