package com.example.mapd721_a2_sannidavid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant

class HeartRateViewModel(
    private val healthConnectManager: HealthConnectManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HeartRateUiState())
    val uiState: StateFlow<HeartRateUiState> = _uiState

    fun updateHeartRate(value: String) {
        _uiState.update { it.copy(heartRate = value) }
    }

    fun updateTimestamp(timestamp: Long) {
        _uiState.update { it.copy(timestamp = timestamp) }
    }

    fun saveHeartRate() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                val heartRate = _uiState.value.heartRate.toIntOrNull()
                    ?: throw IllegalArgumentException("Invalid heart rate")

                healthConnectManager.saveHeartRate(
                    heartRate = heartRate,
                    timestamp = _uiState.value.timestamp
                )
                loadHeartRateHistory()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun loadHeartRateHistory() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                healthConnectManager.readHeartRateData().collect { heartRateData ->
                    _uiState.update {
                        it.copy(heartRateHistory = heartRateData.sortedByDescending { data -> data.timestamp })
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}