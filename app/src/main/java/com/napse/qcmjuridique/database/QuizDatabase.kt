package com.napse.qcmjuridique.database

import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.napse.qcmjuridique.data.*

/**
 * Base de données Room pour l'application QCM Juridique
 */
@Database(
    entities = [
        QuizResult::class,
        FailedQuestion::class,
        SubjectProgress::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class QuizDatabase : RoomDatabase() {
    
    abstract fun quizResultDao(): QuizResultDao
    abstract fun failedQuestionDao(): FailedQuestionDao
    abstract fun subjectProgressDao(): SubjectProgressDao
    
    companion object {
        @Volatile
        private var INSTANCE: QuizDatabase? = null
        
        /**
         * Singleton pour obtenir l'instance de la base de données
         */
        fun getDatabase(context: android.content.Context): QuizDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuizDatabase::class.java,
                    "quiz_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

/**
 * Convertisseurs pour les types complexes en Room
 */
class Converters {
    
    @TypeConverter
    fun fromListInt(value: List<Int>): String {
        return value.joinToString(",")
    }
    
    @TypeConverter
    fun toListInt(value: String): List<Int> {
        return if (value.isEmpty()) emptyList() else value.split(",").map { it.toInt() }
    }
}