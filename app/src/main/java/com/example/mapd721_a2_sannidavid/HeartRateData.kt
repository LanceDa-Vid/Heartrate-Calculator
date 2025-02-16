package com.example.mapd721_a2_sannidavid

import java.util.UUID

// HeartRateData.kt
data class HeartRateData(
    val heartRate: Int,
    val timestamp: Long,
    val id: String = UUID.randomUUID().toString()
)

