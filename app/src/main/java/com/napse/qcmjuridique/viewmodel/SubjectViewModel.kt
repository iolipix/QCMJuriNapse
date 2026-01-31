package com.napse.qcmjuridique.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.napse.qcmjuridique.data.Subject
import com.napse.qcmjuridique.repository.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel pour l'écran des matières d'un semestre
 */
class SubjectViewModel(
    private val repository: QuestionRepository
) : ViewModel() {

    private val _subjects = MutableStateFlow<List<Subject>>(emptyList())
    val subjects: StateFlow<List<Subject>> = _subjects.asStateFlow()

    private val _estEnChargement = MutableStateFlow(false)
    val estEnChargement: StateFlow<Boolean> = _estEnChargement.asStateFlow()

    /**
     * Charge les matières d'un semestre avec leur progression
     */
    fun chargerMatieresAvecProgression(semesterId: String) {
        viewModelScope.launch {
            _estEnChargement.value = true
            try {
                val semestres = repository.getSemesterData()
                val semestre = semestres.find { it.id == semesterId }
                
                if (semestre != null) {
                    // Enrichir chaque matière avec les données de progression
                    val matieresAvecProgression = semestre.subjects.map { subject ->
                        val progression = repository.getProgressionMatiere(subject.id)
                        val quizData = repository.chargerQuestions(subject.id)
                        
                        subject.copy(
                            questionsCount = quizData?.questions?.size ?: 0,
                            averageScore = progression?.averageScore ?: 0.0,
                            completedQuizzes = progression?.totalQuizzesCompleted ?: 0
                        )
                    }
                    _subjects.value = matieresAvecProgression
                } else {
                    _subjects.value = emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _subjects.value = emptyList()
            } finally {
                _estEnChargement.value = false
            }
        }
    }
}