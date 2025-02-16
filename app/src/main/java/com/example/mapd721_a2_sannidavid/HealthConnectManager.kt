package com.example.mapd721_a2_sannidavid

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.example.mapd721_a2_sannidavid.HeartRateData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit




class HealthConnectManager(private val context: Context) {
    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    companion object {
        val PERMISSIONS = setOf(
            HealthPermission.getReadPermission(HeartRateRecord::class),
            HealthPermission.getWritePermission(HeartRateRecord::class)
        )
    }

    suspend fun checkPermissions(): Boolean {
        return healthConnectClient.permissionController.getGrantedPermissions()
            .containsAll(PERMISSIONS)
    }

    suspend fun saveHeartRate(heartRate: Int, timestamp: Long) {
        try {
            val startTime = Instant.ofEpochMilli(timestamp)
            val endTime = startTime.plusSeconds(60)

            val heartRateRecord = HeartRateRecord(
                startTime = startTime,
                endTime = endTime,
                startZoneOffset = ZonedDateTime.now().offset,
                endZoneOffset = ZonedDateTime.now().offset,
                samples = listOf(
                    HeartRateRecord.Sample(
                        time = startTime,
                        beatsPerMinute = heartRate.toLong()
                    )
                )
            )

            healthConnectClient.insertRecords(listOf(heartRateRecord))
        } catch (e: Exception) {
            throw Exception("Failed to save heart rate: ${e.message}")
        }
    }

    suspend fun readHeartRateData(): Flow<List<HeartRateData>> = flow {
        try {
            val endTime = ZonedDateTime.now().toInstant()
            val startTime = endTime.minus(30, ChronoUnit.DAYS)

            val request = ReadRecordsRequest(
                recordType = HeartRateRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )

            val response = healthConnectClient.readRecords(request)
            val heartRateData = response.records.map { record ->
                record.samples.map { sample ->
                    HeartRateData(
                        heartRate = sample.beatsPerMinute.toInt(),
                        timestamp = sample.time.toEpochMilli()
                    )
                }
            }.flatten()

            emit(heartRateData)
        } catch (e: Exception) {
            throw Exception("Failed to read heart rate data: ${e.message}")
        }
    }
}