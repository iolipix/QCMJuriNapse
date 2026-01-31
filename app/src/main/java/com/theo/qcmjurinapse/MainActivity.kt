package com.theo.qcmjurinapse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.theo.qcmjurinapse.database.QuizDatabase
import com.theo.qcmjurinapse.repository.QuestionRepository
import com.theo.qcmjurinapse.ui.QuizScreen
import com.theo.qcmjurinapse.ui.screens.*
import com.theo.qcmjurinapse.ui.theme.QCMJuridiqueNapseTheme
import com.theo.qcmjurinapse.viewmodel.*

/**
 * Activité principale de l'application QCM Juridique Napse
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            QCMJuridiqueNapseTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    QCMApp()
                }
            }
        }
    }
}

@Composable
private fun QCMApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val database = QuizDatabase.getDatabase(context)
    val repository = QuestionRepository(context, database)

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        // Écran d'accueil avec liste des semestres
        composable("home") {
            val viewModel: HomeViewModel = viewModel {
                HomeViewModel(repository)
            }
            HomeScreen(
                onNavigateToSubjects = { semesterId, semesterName ->
                    navController.navigate("subjects/$semesterId/$semesterName")
                },
                viewModel = viewModel
            )
        }

        // Écran des matières d'un semestre
        composable("subjects/{semesterId}/{semesterName}") { backStackEntry ->
            val semesterId = backStackEntry.arguments?.getString("semesterId") ?: ""
            val semesterName = backStackEntry.arguments?.getString("semesterName") ?: ""
            
            val viewModel: SubjectViewModel = viewModel {
                SubjectViewModel(repository)
            }
            
            SubjectScreen(
                semesterId = semesterId,
                semesterName = semesterName,
                onNavigateToQuiz = { subjectId, subjectName ->
                    navController.navigate("quiz/$subjectId/$subjectName")
                },
                onNavigateToHistory = { subjectId, subjectName ->
                    navController.navigate("history/$subjectId/$subjectName")
                },
                onNavigateBack = {
                    navController.navigateUp()
                },
                viewModel = viewModel
            )
        }

        // Écran du quiz pour une matière
        composable("quiz/{subjectId}/{subjectName}") { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""
            val subjectName = backStackEntry.arguments?.getString("subjectName") ?: ""
            
            QuizScreen(
                subjectId = subjectId,
                subjectName = subjectName,
                onNavigateBack = {
                    navController.navigateUp()
                },
                onQuizCompleted = {
                    // Revenir à l'écran des matières
                    navController.popBackStack()
                }
            )
        }

        // Écran d'historique pour une matière
        composable("history/{subjectId}/{subjectName}") { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""
            val subjectName = backStackEntry.arguments?.getString("subjectName") ?: ""
            
            val viewModel: HistoryViewModel = viewModel {
                HistoryViewModel(repository)
            }
            
            HistoryScreen(
                subjectId = subjectId,
                subjectName = subjectName,
                onNavigateBack = {
                    navController.navigateUp()
                },
                onStartNewQuiz = {
                    navController.navigate("quiz/$subjectId/$subjectName") {
                        // Nettoyer la pile pour éviter les retours multiples
                        popUpTo("subjects/{semesterId}/{semesterName}")
                    }
                },
                viewModel = viewModel
            )
        }
    }
}
