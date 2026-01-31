package com.theo.qcmjurinapse.database

import androidx.room.*
import com.theo.qcmjurinapse.data.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO pour gérer les résultats de quiz
 */
@Dao
interface QuizResultDao {
    
    @Query("SELECT * FROM quiz_results WHERE subjectId = :subjectId ORDER BY dateCompleted DESC")
    fun getResultsForSubject(subjectId: String): Flow<List<QuizResult>>
    
    @Query("SELECT * FROM quiz_results ORDER BY dateCompleted DESC")
    fun getAllResults(): Flow<List<QuizResult>>
    
    @Query("SELECT * FROM quiz_results ORDER BY dateCompleted DESC LIMIT :limit")
    fun getRecentResults(limit: Int = 10): Flow<List<QuizResult>>
    
    @Insert
    suspend fun insertResult(result: QuizResult)
    
    @Query("DELETE FROM quiz_results WHERE subjectId = :subjectId")
    suspend fun deleteResultsForSubject(subjectId: String)
    
    @Query("""
        SELECT AVG(percentage) as avgScore 
        FROM quiz_results 
        WHERE subjectId = :subjectId
    """)
    suspend fun getAverageScoreForSubject(subjectId: String): Double?
    
    @Query("""
        SELECT MAX(score) as bestScore 
        FROM quiz_results 
        WHERE subjectId = :subjectId
    """)
    suspend fun getBestScoreForSubject(subjectId: String): Int?
}

/**
 * DAO pour gérer les questions ratées
 */
@Dao
interface FailedQuestionDao {
    
    @Query("SELECT * FROM failed_questions WHERE subjectId = :subjectId ORDER BY failureCount DESC")
    fun getFailedQuestionsForSubject(subjectId: String): Flow<List<FailedQuestion>>
    
    @Query("SELECT questionId FROM failed_questions WHERE subjectId = :subjectId AND failureCount > :minFailures")
    suspend fun getMostFailedQuestionIds(subjectId: String, minFailures: Int = 1): List<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFailedQuestion(failedQuestion: FailedQuestion)
    
    @Query("SELECT * FROM failed_questions WHERE questionId = :questionId AND subjectId = :subjectId")
    suspend fun getFailedQuestion(questionId: Int, subjectId: String): FailedQuestion?
    
    @Query("""
        UPDATE failed_questions 
        SET failureCount = failureCount + 1, lastFailureDate = :currentTime 
        WHERE questionId = :questionId AND subjectId = :subjectId
    """)
    suspend fun incrementFailureCount(questionId: Int, subjectId: String, currentTime: Long)
    
    /**
     * Ajoute ou incrémente le compteur d'échec pour une question
     */
    @Transaction
    suspend fun addOrIncrementFailedQuestion(questionId: Int, subjectId: String) {
        val existing = getFailedQuestion(questionId, subjectId)
        if (existing != null) {
            incrementFailureCount(questionId, subjectId, System.currentTimeMillis())
        } else {
            insertFailedQuestion(
                FailedQuestion(
                    questionId = questionId,
                    subjectId = subjectId,
                    failureCount = 1,
                    lastFailureDate = System.currentTimeMillis()
                )
            )
        }
    }
    
    @Query("DELETE FROM failed_questions WHERE questionId = :questionId AND subjectId = :subjectId")
    suspend fun removeFailedQuestion(questionId: Int, subjectId: String)
    
    @Query("DELETE FROM failed_questions WHERE subjectId = :subjectId")
    suspend fun clearFailedQuestionsForSubject(subjectId: String)
    
    /**
     * Marque une question comme réussie (supprime ou diminue le compteur d'échec)
     */
    @Query("""
        UPDATE failed_questions 
        SET failureCount = CASE 
            WHEN failureCount > 1 THEN failureCount - 1 
            ELSE 0 
        END 
        WHERE questionId = :questionId AND subjectId = :subjectId
    """)
    suspend fun markQuestionAsSuccessful(questionId: Int, subjectId: String)
}

/**
 * DAO pour gérer la progression par matière
 */
@Dao
interface SubjectProgressDao {
    
    @Query("SELECT * FROM subject_progress ORDER BY subjectName")
    fun getAllProgress(): Flow<List<SubjectProgress>>
    
    @Query("SELECT * FROM subject_progress WHERE subjectId = :subjectId")
    suspend fun getProgressForSubject(subjectId: String): SubjectProgress?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: SubjectProgress)
    
    @Query("DELETE FROM subject_progress WHERE subjectId = :subjectId")
    suspend fun deleteProgressForSubject(subjectId: String)
    
    /**
     * Met à jour la progression après un quiz
     */
    @Transaction
    suspend fun updateProgressAfterQuiz(
        subjectId: String,
        subjectName: String,
        score: Int,
        totalQuestions: Int
    ) {
        val existing = getProgressForSubject(subjectId) ?: SubjectProgress(
            subjectId = subjectId,
            subjectName = subjectName
        )
        
        val newTotalCompleted = existing.totalQuizzesCompleted + 1
        val newTotalAnswered = existing.totalQuestionsAnswered + totalQuestions
        val newBestScore = maxOf(existing.bestScore, score)
        
        // Calcul de la nouvelle moyenne
        val currentTotalScore = existing.averageScore * existing.totalQuestionsAnswered
        val newAverageScore = if (newTotalAnswered > 0) {
            (currentTotalScore + score) / newTotalAnswered
        } else 0.0
        
        val updatedProgress = existing.copy(
            totalQuizzesCompleted = newTotalCompleted,
            bestScore = newBestScore,
            averageScore = newAverageScore,
            totalQuestionsAnswered = newTotalAnswered,
            lastQuizDate = System.currentTimeMillis()
        )
        
        insertOrUpdateProgress(updatedProgress)
    }
}
