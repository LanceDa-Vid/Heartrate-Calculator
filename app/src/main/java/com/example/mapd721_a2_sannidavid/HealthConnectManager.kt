package com.example.mapd721_a2_sannidavid

import android.content.Context
import android.health.connect.datatypes.HeartRateRecord
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class HealthConnectManager(private val context: Context) {
    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    companion object {
        val REQUIRED_PERMISSIONS = setOf(
            HealthPermission.createReadPermission(HeartRateRecord::class),
            HealthPermission.createWritePermission(HeartRateRecord::class)
        )
    }

    suspend fun hasRequiredPermissions(): Boolean {
        return healthConnectClient.permissionController.getGrantedPermissions()
            .containsAll(REQUIRED_PERMISSIONS)
    }

    suspend fun saveHeartRate(heartRate: Int, timestamp: Long) {
        val heartRateRecord = HeartRateRecord(
            startTime = Instant.ofEpochMilli(timestamp),
            endTime = Instant.ofEpochMilli(timestamp).plus(1, ChronoUnit.MINUTES),
            samples = listOf(
                HeartRateRecord.Sample(
                    time = Instant.ofEpochMilli(timestamp),
                    beatsPerMinute = heartRate.toLong()
                )
            )
        )

        try {
            healthConnectClient.insertRecords(listOf(heartRateRecord))
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun readHeartRateData(): Flow<List<HeartRateData>> = flow {
        val endTime = ZonedDateTime.now().toInstant()
        val startTime = endTime.minus(30, ChronoUnit.DAYS)

        val request = ReadRecordsRequest(
            recordType = HeartRateRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
        )

        try {
            val records = healthConnectClient.readRecords(request)
            val heartRateData = records.records.map { record ->
                record.samples.map { sample ->
                    HeartRateData(
                        heartRate = sample.beatsPerMinute.toInt(),
                        timestamp = sample.time.toEpochMilli()
                    )
                }
            }.flatten()
            emit(heartRateData)
        } catch (e: Exception) {
            throw e
        }
    }
}