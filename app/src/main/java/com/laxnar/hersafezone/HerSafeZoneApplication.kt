
package com.laxnar.hersafezone

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.laxnar.hersafezone.service.PresenceWorker
import java.util.concurrent.TimeUnit

class HerSafeZoneApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupPeriodicPresenceUpdate()
    }

    private fun setupPeriodicPresenceUpdate() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val presenceWorkRequest = PeriodicWorkRequestBuilder<PresenceWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "presence_update",
            ExistingPeriodicWorkPolicy.KEEP,
            presenceWorkRequest
        )
    }
}
