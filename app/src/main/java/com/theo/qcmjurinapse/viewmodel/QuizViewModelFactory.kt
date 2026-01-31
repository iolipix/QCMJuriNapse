package com.theo.qcmjurinapse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.theo.qcmjurinapse.repository.QuestionRepository

/**
 * Factory pour créer le QuizViewModel avec ses dépendances
 */
class QuizViewModelFactory(
    private val repository: QuestionRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            return QuizViewModel(repository) as T
        }
        throw IllegalArgumentException("Classe ViewModel inconnue: ${modelClass.name}")
    }
}
