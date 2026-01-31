package com.theo.qcmjurinapse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theo.qcmjurinapse.data.QuizResult
import com.theo.qcmjurinapse.repository.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel pour l'écran d'historique des résultats
 */
class HistoryViewModel(
    private val repository: QuestionRepository
) : ViewModel() {

    private val _resultats = MutableStateFlow<List<QuizResult>>(emptyList())
    val resultats: StateFlow<List<QuizResult>> = _resultats.asStateFlow()

    private val _statistiques = MutableStateFlow(StatistiquesMatiere())
    val statistiques: StateFlow<StatistiquesMatiere> = _statistiques.asStateFlow()

    private val _estEnChargement = MutableStateFlow(false)
    val estEnChargement: StateFlow<Boolean> = _estEnChargement.asStateFlow()

    /**
     * Charge l'historique pour une matière spécifique
     */
    fun chargerHistorique(subjectId: String) {
        viewModelScope.launch {
            _estEnChargement.value = true
            try {
                // Collecter les résultats
                repository.getResultatsParMatiere(subjectId).collect { resultats ->
                    _resultats.value = resultats
                    
                    // Calculer les statistiques
                    if (resultats.isNotEmpty()) {
                        val stats = calculerStatistiques(resultats)
                        _statistiques.value = stats
                    } else {
                        _statistiques.value = StatistiquesMatiere()
                    }
                    
                    _estEnChargement.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _estEnChargement.value = false
            }
        }
    }

    /**
     * Calcule les statistiques à partir de la liste des résultats
     */
    private fun calculerStatistiques(resultats: List<QuizResult>): StatistiquesMatiere {
        return StatistiquesMatiere(
            totalQuiz = resultats.size,
            meilleurScore = resultats.maxOfOrNull { it.percentage }?.toInt() ?: 0,
            moyenneScore = if (resultats.isNotEmpty()) {
                resultats.map { it.percentage }.average()
            } else 0.0,
            totalQuestionsRepondues = resultats.sumOf { it.totalQuestions },
            totalQuestionsCorrectes = resultats.sumOf { it.score },
            tempsTotal = resultats.sumOf { it.timeSpentSeconds }
        )
    }

    /**
     * Data class pour les statistiques d'une matière
     */
    data class StatistiquesMatiere(
        val totalQuiz: Int = 0,
        val meilleurScore: Int = 0,
        val moyenneScore: Double = 0.0,
        val totalQuestionsRepondues: Int = 0,
        val totalQuestionsCorrectes: Int = 0,
        val tempsTotal: Long = 0
    ) {
        val tauxReussite: Double
            get() = if (totalQuestionsRepondues > 0) {
                (totalQuestionsCorrectes.toDouble() / totalQuestionsRepondues) * 100
            } else 0.0
    }
}
