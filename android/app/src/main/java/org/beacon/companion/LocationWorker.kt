package org.beacon.companion

import android.annotation.SuppressLint
import android.content.Context
import android.os.BatteryManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Tasks
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class LocationWorker(context: Context, params: WorkerParameters): CoroutineWorker(context, params) {
    @SuppressLint("MissingPermission") // Enqueuer verifies runtime permission before scheduling.
    override suspend fun doWork(): Result {
        val id=inputData.getString("deviceId") ?: return Result.failure()
        val token=inputData.getString("deviceToken") ?: return Result.failure()
        val location=Tasks.await(LocationServices.getFusedLocationProviderClient(applicationContext).getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY,null)) ?: return Result.retry()
        val battery=applicationContext.getSystemService(BatteryManager::class.java).getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val body=JSONObject(mapOf("latitude" to location.latitude,"longitude" to location.longitude,"accuracy" to location.accuracy,"battery" to battery,"network" to "connected","recordedAt" to java.time.Instant.ofEpochMilli(location.time).toString())).toString()
        val request=Request.Builder().url("${BuildConfig.API_BASE_URL}/api/device/$id/telemetry").header("X-Device-Token",token).post(body.toRequestBody("application/json".toMediaType())).build()
        OkHttpClient().newCall(request).execute().use { return if(it.isSuccessful) Result.success() else Result.retry() }
    }
}
