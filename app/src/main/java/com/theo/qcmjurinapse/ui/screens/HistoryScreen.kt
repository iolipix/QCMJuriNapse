package com.theo.qcmjurinapse.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle.viewModel
import com.theo.qcmjurinapse.data.QuizResult
import com.theo.qcmjurinapse.viewmodel.HistoryViewModel
import com.theo.qcmjurinapse.viewmodel.HistoryViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

/**
 * Écran d'historique des résultats pour une matière
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    subjectId: String,
    subjectName: String,
    onNavigateBack: () -> Unit,
    onStartNewQuiz: () -> Unit,
    viewModel: HistoryViewModel = viewModel()
) {
    val resultats by viewModel.resultats.collectAsStateWithLifecycle()
    val statistiques by viewModel.statistiques.collectAsStateWithLifecycle()
    val estEnChargement by viewModel.estEnChargement.collectAsStateWithLifecycle()
    
    LaunchedEffect(subjectId) {
        viewModel.chargerHistorique(subjectId)
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // App Bar
        CenterAlignedTopAppBar(
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Historique",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subjectName,
                        style = MaterialTheme.typography.bodyMedium
                    )
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
            actions = {
                IconButton(onClick = onStartNewQuiz) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Nouveau quiz"
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
        
        if (estEnChargement) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (resultats.isEmpty()) {
            // État vide
            EcranVide(onStartNewQuiz = onStartNewQuiz)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Statistiques globales
                item {
                    StatistiquesCard(statistiques = statistiques)
                }
                
                item {
                    Text(
                        text = "Historique des quiz (${resultats.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                // Liste des résultats
                items(resultats) { resultat ->
                    ResultatCard(resultat = resultat)
                }
            }
        }
    }
}

@Composable
private fun EcranVide(
    onStartNewQuiz: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Aucun quiz complété",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Commencez votre premier quiz pour voir vos résultats ici",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onStartNewQuiz
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Commencer un quiz")
        }
    }
}

@Composable
private fun StatistiquesCard(
    statistiques: HistoryViewModel.StatistiquesMatiere
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Statistiques globales",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(
                    title = "Quiz complétés",
                    value = statistiques.totalQuiz.toString(),
                    icon = Icons.Default.Quiz,
                    color = MaterialTheme.colorScheme.primary
                )
                StatCard(
                    title = "Meilleur score",
                    value = "${statistiques.meilleurScore}%",
                    icon = Icons.Default.EmojiEvents,
                    color = MaterialTheme.colorScheme.tertiary
                )
                StatCard(
                    title = "Moyenne",
                    value = "${statistiques.moyenneScore.toInt()}%",
                    icon = Icons.Default.TrendingUp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = color
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun ResultatCard(
    resultat: QuizResult
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy 'à' HH:mm", Locale.getDefault())
    val couleurScore = when {
        resultat.percentage >= 80 -> MaterialTheme.colorScheme.tertiary
        resultat.percentage >= 60 -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.error
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicateur de score coloré
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = couleurScore.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${resultat.percentage.toInt()}%",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = couleurScore
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${resultat.score}/${resultat.totalQuestions}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = dateFormat.format(Date(resultat.dateCompleted)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (resultat.timeSpentSeconds > 0) {
                    val minutes = resultat.timeSpentSeconds / 60
                    Text(
                        text = "Durée: ${minutes}min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Indicateur de performance
            val iconePerformance = when {
                resultat.percentage >= 80 -> Icons.Default.EmojiEvents
                resultat.percentage >= 60 -> Icons.Default.ThumbUp
                else -> Icons.Default.TrendingDown
            }
            
            Icon(
                imageVector = iconePerformance,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = couleurScore
            )
        }
    }
}
