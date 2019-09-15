package ru.ra66it.updaterforspotify.presentation.workers

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkersEnqueueManager @Inject constructor(context: Context) {

    private val workManager: WorkManager = WorkManager.getInstance(context)

    fun enqueuePeriodicCheckingIfDontExist(days: Long) {
        enqueuePeriodicChecking(days, ExistingPeriodicWorkPolicy.KEEP)
    }

    fun enqueuePeriodicChecking(days: Long, existingPeriodicWorkPolicy: ExistingPeriodicWorkPolicy) {
        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        val work = PeriodicWorkRequestBuilder<CheckingWorker>(days, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()

        workManager.enqueueUniquePeriodicWork(CheckingWorker.TAG,  existingPeriodicWorkPolicy, work)
    }

    fun stopPeriodicChecking() {
        workManager.cancelUniqueWork(CheckingWorker.TAG)
    }

}