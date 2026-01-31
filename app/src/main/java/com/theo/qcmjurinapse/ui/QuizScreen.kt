package com.theo.qcmjurinapse.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle.viewModel
import com.theo.qcmjurinapse.ui.components.*
import com.theo.qcmjurinapse.repository.QuestionRepository
import com.theo.qcmjurinapse.database.QuizDatabase
import com.theo.qcmjurinapse.viewmodel.QuizViewModel
import com.theo.qcmjurinapse.viewmodel.QuizViewModelFactory

/**
 * Écran principal du quiz QCM avec support des matières
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    subjectId: String,
    subjectName: String,
    onNavigateBack: () -> Unit,
    onQuizCompleted: () -> Unit
) {
    val context = LocalContext.current
    val database = QuizDatabase.getDatabase(context)
    val repository = remember { QuestionRepository(context, database) }
    val viewModel: QuizViewModel = viewModel(
        factory = QuizViewModelFactory(repository)
    )
    
    val etatQuiz by viewModel.etatQuiz.collectAsStateWithLifecycle()
    val etatQuestion by viewModel.etatQuestionActuelle.collectAsStateWithLifecycle()
    val estEnChargement by viewModel.estEnChargement.collectAsStateWithLifecycle()
    val messageErreur by viewModel.messageErreur.collectAsStateWithLifecycle()
    
    // Démarrer le quiz automatiquement
    LaunchedEffect(subjectId) {
        viewModel.demarrerQuiz(subjectId, subjectName)
    }
    
    // Gérer la fin du quiz
    LaunchedEffect(etatQuiz.estTermine) {
        if (etatQuiz.estTermine) {
            onQuizCompleted()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Bar avec titre et score
        CenterAlignedTopAppBar(
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = subjectName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (etatQuiz.nombreTotalQuestions > 0) {
                        Text(
                            text = "Score: ${etatQuiz.score}/${etatQuiz.questionsValidees}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Retour"
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
        
        when {
            estEnChargement -> {
                // Écran de chargement
                EcranChargement()
            }
            
            messageErreur != null -> {
                // Écran d'erreur
                EcranErreur(
                    message = messageErreur,
                    onRetry = { viewModel.demarrerQuiz(subjectId, subjectName) },
                    onDismiss = { viewModel.effacerErreur() }
                )
            }
            
            etatQuiz.estTermine -> {
                // Écran de fin avec résultats
                EcranResultats(
                    score = etatQuiz.score,
                    total = etatQuiz.nombreTotalQuestions,
                    pourcentage = etatQuiz.pourcentageReussite,
                    onRecommencer = { viewModel.recommencerQuiz() },
                    onRevisionIntelligente = { viewModel.melangerEtRecommencer() }
                )
            }
            
            etatQuestion != null -> {
                // Question actuelle
                ContenuQuiz(
                    etatQuiz = etatQuiz,
                    etatQuestion = etatQuestion,
                    onSelectionReponse = viewModel::selectionnerReponse,
                    onValider = viewModel::validerQuestion,
                    onQuestionSuivante = viewModel::questionSuivante,
                    peutValider = viewModel.peutValider()
                )
            }
            
            else -> {
                // État initial
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun EcranChargement() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Chargement des questions...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun EcranErreur(
    message: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Erreur",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Fermer")
                    }
                    Button(onClick = onRetry) {
                        Text("Réessayer")
                    }
                }
            }
        }
    }
}

@Composable
private fun EcranResultats(
    score: Int,
    total: Int,
    onRecommencer: () -> Unit,
    onMelangerEtRecommencer: () -> Unit
) {
    val pourcentage = if (total > 0) (score * 100) / total else 0
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Quiz terminé !",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Votre score",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "$score / $total",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "$pourcentage%",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onRecommencer,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Recommencer")
                    }
                    Button(
                        onClick = onMelangerEtRecommencer,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Mélanger")
                    }
                }
            }
        }
    }
}
