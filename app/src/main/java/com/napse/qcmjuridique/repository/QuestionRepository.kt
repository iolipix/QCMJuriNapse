package com.napse.qcmjuridique.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.napse.qcmjuridique.data.*
import com.napse.qcmjuridique.database.QuizDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Repository pour gérer les questions QCM et les données associées
 */
class QuestionRepository(
    private val context: Context,
    private val database: QuizDatabase
) {
    
    private val gson = Gson()
    private val quizResultDao = database.quizResultDao()
    private val failedQuestionDao = database.failedQuestionDao()
    private val subjectProgressDao = database.subjectProgressDao()
    
    /**
     * Retourne la liste des semestres et matières disponibles
     */
    suspend fun getSemesterData(): List<Semester> {
        return withContext(Dispatchers.IO) {
            listOf(
                Semester(
                    id = "semestre5",
                    name = "Semestre 5",
                    subjects = listOf(
                        Subject("droit_travail", "Droit du travail", "semestre5"),
                        Subject("libertes_fondamentales", "Libertés fondamentales", "semestre5"),
                        Subject("droit_judiciaire_prive", "Droit judiciaire privé", "semestre5"),
                        Subject("contentieux_administratif", "Contentieux administratif", "semestre5"),
                        Subject("droit_international_prive", "Droit international privé (DIP)", "semestre5"),
                        Subject("droit_prive_biens", "Droit privé des biens", "semestre5")
                    )
                )
            )
        }
    }
    
    /**
     * Charge les questions pour une matière spécifique
     */
    suspend fun chargerQuestions(subjectId: String): SubjectQuizData? {
        return withContext(Dispatchers.IO) {
            try {
                val fileName = "${subjectId}.json"
                val jsonString = context.assets.open(fileName).bufferedReader().use {
                    it.readText()
                }
                
                val typeToken = object : TypeToken<SubjectQuizData>() {}.type
                gson.fromJson<SubjectQuizData>(jsonString, typeToken)
                
            } catch (e: IOException) {
                e.printStackTrace()
                null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    
    /**
     * Génère un quiz avec révision intelligente
     * Privilégie les questions ratées précédemment
     */
    suspend fun genererQuizIntelligent(
        subjectId: String, 
        nombreQuestions: Int = 20
    ): List<Question> {
        return withContext(Dispatchers.IO) {
            val quizData = chargerQuestions(subjectId) ?: return@withContext emptyList()
            val toutesQuestions = quizData.questions
            
            // Récupérer les IDs des questions ratées
            val questionsRatees = failedQuestionDao.getMostFailedQuestionIds(subjectId)
            
            // Séparer questions ratées et questions normales
            val questionsRateesFiltered = toutesQuestions.filter { it.id in questionsRatees }
            val autresQuestions = toutesQuestions.filter { it.id !in questionsRatees }
            
            // Construire le quiz : 70% questions ratées, 30% autres questions
            val nombreQuestionsRatees = minOf(questionsRateesFiltered.size, (nombreQuestions * 0.7).toInt())
            val nombreAutresQuestions = nombreQuestions - nombreQuestionsRatees
            
            val questionsSelectionnees = mutableListOf<Question>()
            questionsSelectionnees.addAll(questionsRateesFiltered.shuffled().take(nombreQuestionsRatees))
            questionsSelectionnees.addAll(autresQuestions.shuffled().take(nombreAutresQuestions))
            
            questionsSelectionnees.shuffled()
        }
    }
    
    /**
     * Mélange les questions de manière aléatoire
     */
    fun melangerQuestions(questions: List<Question>): List<Question> {
        return questions.shuffled()
    }
    
    /**
     * Sauvegarde le résultat d'un quiz
     */
    suspend fun sauvegarderResultatQuiz(
        subjectId: String,
        subjectName: String,
        score: Int,
        totalQuestions: Int,
        questionsRatees: List<Int>,
        timeSpentSeconds: Long = 0
    ) {
        withContext(Dispatchers.IO) {
            val percentage = if (totalQuestions > 0) (score.toDouble() / totalQuestions) * 100 else 0.0
            
            // Sauvegarder le résultat
            val result = QuizResult(
                subjectId = subjectId,
                subjectName = subjectName,
                score = score,
                totalQuestions = totalQuestions,
                percentage = percentage,
                timeSpentSeconds = timeSpentSeconds
            )
            quizResultDao.insertResult(result)
            
            // Mettre à jour la progression
            subjectProgressDao.updateProgressAfterQuiz(subjectId, subjectName, score, totalQuestions)
            
            // Enregistrer les questions ratées
            questionsRatees.forEach { questionId ->
                failedQuestionDao.addOrIncrementFailedQuestion(questionId, subjectId)
            }
        }
    }
    
    /**
     * Marque une question comme réussie (pour diminuer sa priorité en révision)
     */
    suspend fun marquerQuestionReussie(questionId: Int, subjectId: String) {
        withContext(Dispatchers.IO) {
            failedQuestionDao.markQuestionAsSuccessful(questionId, subjectId)
        }
    }
    
    // Méthodes pour accéder aux données de la base
    fun getResultatsParMatiere(subjectId: String): Flow<List<QuizResult>> {
        return quizResultDao.getResultsForSubject(subjectId)
    }
    
    fun getTousLesResultats(): Flow<List<QuizResult>> {
        return quizResultDao.getAllResults()
    }
    
    fun getResultatsRecents(limit: Int = 10): Flow<List<QuizResult>> {
        return quizResultDao.getRecentResults(limit)
    }
    
    fun getProgressionMatieres(): Flow<List<SubjectProgress>> {
        return subjectProgressDao.getAllProgress()
    }
    
    suspend fun getProgressionMatiere(subjectId: String): SubjectProgress? {
        return subjectProgressDao.getProgressForSubject(subjectId)
    }
    
    suspend fun supprimerDonneesMatiere(subjectId: String) {
        withContext(Dispatchers.IO) {
            quizResultDao.deleteResultsForSubject(subjectId)
            failedQuestionDao.clearFailedQuestionsForSubject(subjectId)
            subjectProgressDao.deleteProgressForSubject(subjectId)
        }
    }
}