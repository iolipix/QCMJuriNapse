package com.theo.qcmjurinapse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.theo.qcmjurinapse.data.*

@Composable
fun EcranChargement() {
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
fun EcranErreur(
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
fun EcranResultats(
    score: Int,
    total: Int,
    pourcentage: Double,
    onRecommencer: () -> Unit,
    onRevisionIntelligente: () -> Unit
) {
    val pourcentageInt = pourcentage.toInt()
    
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
                
                // Score principal
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
                    text = "$pourcentageInt%",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Évaluation du score
                val (message, couleur, icone) = when {
                    pourcentage >= 90 -> Triple("Excellent !", MaterialTheme.colorScheme.tertiary, Icons.Default.EmojiEvents)
                    pourcentage >= 80 -> Triple("Très bien !", MaterialTheme.colorScheme.tertiary, Icons.Default.ThumbUp)
                    pourcentage >= 70 -> Triple("Bien !", MaterialTheme.colorScheme.primary, Icons.Default.ThumbUp)
                    pourcentage >= 60 -> Triple("Passable", MaterialTheme.colorScheme.primary, Icons.Default.TrendingUp)
                    else -> Triple("À améliorer", MaterialTheme.colorScheme.error, Icons.Default.TrendingDown)
                }
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = couleur.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icone,
                            contentDescription = null,
                            tint = couleur,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = message,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = couleur
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Boutons d'action
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onRevisionIntelligente,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Révision intelligente")
                    }
                    
                    OutlinedButton(
                        onClick = onRecommencer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Recommencer")
                    }
                }
            }
        }
    }
}

@Composable
fun ContenuQuiz(
    etatQuiz: EtatQuiz,
    etatQuestion: EtatQuestion,
    onSelectionReponse: (Int) -> Unit,
    onValider: () -> Unit,
    onQuestionSuivante: () -> Unit,
    peutValider: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Barre de progression
        BarreProgression(etatQuiz)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Carte de la question
        CarteQuestion(
            etatQuestion = etatQuestion,
            onSelectionReponse = onSelectionReponse
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Boutons d'action
        if (!etatQuestion.estValidee) {
            Button(
                onClick = onValider,
                enabled = peutValider,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Valider la réponse")
            }
        } else {
            Button(
                onClick = onQuestionSuivante,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (etatQuiz.questionActuelleIndex + 1 < etatQuiz.nombreTotalQuestions) {
                        "Question suivante"
                    } else {
                        "Terminer le quiz"
                    }
                )
            }
        }
    }
}

@Composable
private fun BarreProgression(etatQuiz: EtatQuiz) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Question ${etatQuiz.questionActuelleIndex + 1}/${etatQuiz.nombreTotalQuestions}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Score: ${etatQuiz.score}/${etatQuiz.questionsValidees}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { etatQuiz.progression },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primaryContainer,
        )
    }
}

@Composable
private fun CarteQuestion(
    etatQuestion: EtatQuestion,
    onSelectionReponse: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Texte de la question
            Text(
                text = etatQuestion.question.question,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Réponses
            etatQuestion.question.choices.forEachIndexed { index, choix ->
                val reponse = etatQuestion.reponsesUtilisateur[index]
                CarteReponse(
                    choix = choix,
                    reponse = reponse,
                    estValidee = etatQuestion.estValidee,
                    onSelectionReponse = { onSelectionReponse(index) }
                )
                if (index < etatQuestion.question.choices.lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun CarteReponse(
    choix: String,
    reponse: ReponseUtilisateur,
    estValidee: Boolean,
    onSelectionReponse: () -> Unit
) {
    val couleurFond = when {
        estValidee && reponse.estCorrecte == true -> MaterialTheme.colorScheme.tertiaryContainer
        estValidee && reponse.estMauvaiseReponse -> MaterialTheme.colorScheme.errorContainer
        reponse.estSelectionnee && !estValidee -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    val couleurContour = when {
        estValidee && reponse.estCorrecte == true -> MaterialTheme.colorScheme.tertiary
        estValidee && reponse.estMauvaiseReponse -> MaterialTheme.colorScheme.error
        reponse.estSelectionnee && !estValidee -> MaterialTheme.colorScheme.primary
        else -> Color.Transparent
    }
    
    val icone = when {
        estValidee && reponse.estCorrecte == true -> Icons.Default.Check
        estValidee && reponse.estMauvaiseReponse -> Icons.Default.Close
        else -> null
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !estValidee) { onSelectionReponse() }
            .then(
                if (couleurContour != Color.Transparent) {
                    Modifier.border(2.dp, couleurContour, RoundedCornerShape(8.dp))
                } else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = couleurFond
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = choix,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (icone != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = icone,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = when (icone) {
                        Icons.Default.Check -> MaterialTheme.colorScheme.tertiary
                        Icons.Default.Close -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            } else if (reponse.estSelectionnee && !estValidee) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.RadioButtonChecked,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
