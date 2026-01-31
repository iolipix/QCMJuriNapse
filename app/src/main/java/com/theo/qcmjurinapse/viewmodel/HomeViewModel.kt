package com.theo.qcmjurinapse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theo.qcmjurinapse.data.Semester
import com.theo.qcmjurinapse.repository.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel pour l'écran d'accueil (semestres)
 */
class HomeViewModel(
    private val repository: QuestionRepository
) : ViewModel() {

    private val _semestres = MutableStateFlow<List<Semester>>(emptyList())
    val semestres: StateFlow<List<Semester>> = _semestres.asStateFlow()

    private val _estEnChargement = MutableStateFlow(false)
    val estEnChargement: StateFlow<Boolean> = _estEnChargement.asStateFlow()

    init {
        chargerSemestres()
    }

    /**
     * Charge la liste des semestres disponibles
     */
    private fun chargerSemestres() {
        viewModelScope.launch {
            _estEnChargement.value = true
            try {
                val semestresData = repository.getSemesterData()
                _semestres.value = semestresData
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _estEnChargement.value = false
            }
        }
    }
}
