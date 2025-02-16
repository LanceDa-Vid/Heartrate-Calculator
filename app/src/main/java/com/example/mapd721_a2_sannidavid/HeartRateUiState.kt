package com.example.mapd721_a2_sannidavid


// HeartRateUiState.kt
data class HeartRateUiState(
    val heartRate: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val heartRateHistory: List<HeartRateData> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)