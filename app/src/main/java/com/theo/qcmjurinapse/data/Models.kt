package com.theo.qcmjurinapse.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Modèle de données pour une question de QCM
 * 
 * @param id Numéro unique de la question
 * @param question Texte de la question juridique
 * @param choices Liste des réponses possibles (A, B, C, D...)
 * @param correctAnswers Indices des bonnes réponses (commence à 0)
 */
data class Question(
    val id: Int,
    val question: String,
    val choices: List<String>,
    val correctAnswers: List<Int>
)

/**
 * Modèle pour les données QCM par matière depuis JSON
 */
data class SubjectQuizData(
    val semester: String,
    val subject: String,
    val questions: List<Question>
)

/**
 * Modèle pour un semestre d'études
 */
data class Semester(
    val id: String,
    val name: String,
    val subjects: List<Subject>
)

/**
 * Modèle pour une matière juridique
 */
data class Subject(
    val id: String,
    val name: String,
    val semesterId: String,
    val description: String? = null,
    val questionsCount: Int = 0,
    val averageScore: Double = 0.0,
    val completedQuizzes: Int = 0
)

/**
 * État d'une réponse utilisateur
 */
data class ReponseUtilisateur(
    val index: Int,
    val estSelectionnee: Boolean = false,
    val estCorrecte: Boolean? = null, // null = pas encore validée
    val estMauvaiseReponse: Boolean = false
)

/**
 * État d'une question dans le quiz
 */
data class EtatQuestion(
    val question: Question,
    val reponsesUtilisateur: List<ReponseUtilisateur>,
    val estValidee: Boolean = false,
    val estCorrecte: Boolean? = null
) {
    companion object {
        /**
         * Crée un état initial pour une question
         */
        fun creerEtatInitial(question: Question): EtatQuestion {
            val reponses = question.choices.mapIndexed { index, _ ->
                ReponseUtilisateur(
                    index = index,
                    estSelectionnee = false
                )
            }
            return EtatQuestion(
                question = question,
                reponsesUtilisateur = reponses,
                estValidee = false
            )
        }
    }
}

/**
 * État global du quiz
 */
data class EtatQuiz(
    val subjectId: String = "",
    val subjectName: String = "",
    val questions: List<Question> = emptyList(),
    val questionActuelleIndex: Int = 0,
    val score: Int = 0,
    val questionsValidees: Int = 0,
    val estTermine: Boolean = false,
    val questionsRatees: List<Int> = emptyList() // IDs des questions ratées pour révision
) {
    val questionActuelle: Question? 
        get() = questions.getOrNull(questionActuelleIndex)
    
    val nombreTotalQuestions: Int
        get() = questions.size
        
    val progression: Float
        get() = if (nombreTotalQuestions > 0) questionsValidees.toFloat() / nombreTotalQuestions else 0f
        
    val pourcentageReussite: Double
        get() = if (questionsValidees > 0) (score.toDouble() / questionsValidees) * 100 else 0.0
}

// Room Database Entities

/**
 * Entité pour stocker les résultats de quiz
 */
@Entity(tableName = "quiz_results")
data class QuizResult(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val subjectId: String,
    val subjectName: String,
    val score: Int,
    val totalQuestions: Int,
    val percentage: Double,
    val dateCompleted: Long = System.currentTimeMillis(),
    val timeSpentSeconds: Long = 0
)

/**
 * Entité pour stocker les questions ratées par l'utilisateur
 */
@Entity(tableName = "failed_questions")
data class FailedQuestion(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val questionId: Int,
    val subjectId: String,
    val failureCount: Int = 1,
    val lastFailureDate: Long = System.currentTimeMillis()
)

/**
 * Entité pour stocker la progression par matière
 */
@Entity(tableName = "subject_progress")
data class SubjectProgress(
    @PrimaryKey val subjectId: String,
    val subjectName: String,
    val totalQuizzesCompleted: Int = 0,
    val bestScore: Int = 0,
    val averageScore: Double = 0.0,
    val totalQuestionsAnswered: Int = 0,
    val lastQuizDate: Long? = null
)
