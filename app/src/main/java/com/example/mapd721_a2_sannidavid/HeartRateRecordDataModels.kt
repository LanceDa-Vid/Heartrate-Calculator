package com.example.mapd721_a2_sannidavid

import java.util.UUID

// HeartRateData.kt
data class HeartRateData(
    val heartRate: Int,
    val timestamp: Long,
    val id: String = UUID.randomUUID().toString()
)

// HeartRateUiState.kt
data class HeartRateUiState(
    val heartRate: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val heartRateHistory: List<HeartRateData> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)