package com.napse.qcmjuridique.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.napse.qcmjuridique.data.EtatQuestion
import com.napse.qcmjuridique.data.EtatQuiz
import com.napse.qcmjuridique.data.ReponseUtilisateur

/**
 * Contenu principal du quiz avec question et réponses
 */
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
    ) {
        // En-tête avec progression et score
        EnTeteQuiz(
            questionActuelle = etatQuiz.questionActuelleIndex + 1,
            totalQuestions = etatQuiz.nombreTotalQuestions,
            score = etatQuiz.score,
            questionsValidees = etatQuiz.questionsValidees,
            progression = etatQuiz.progression
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Contenu principal scrollable
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Card de la question
                CarteQuestion(
                    numeroQuestion = etatQuestion.question.id,
                    texteQuestion = etatQuestion.question.question
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Liste des réponses
                etatQuestion.reponsesUtilisateur.forEach { reponse ->
                    CarteReponse(
                        reponse = reponse,
                        texteReponse = etatQuestion.question.choices[reponse.index],
                        estValidee = etatQuestion.estValidee,
                        onSelection = { onSelectionReponse(reponse.index) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Espace pour le bouton flottant
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
        
        // Bouton d'action
        BoutonAction(
            estValidee = etatQuestion.estValidee,
            peutValider = peutValider,
            onValider = onValider,
            onQuestionSuivante = onQuestionSuivante,
            estDerniereQuestion = etatQuiz.questionActuelleIndex == etatQuiz.nombreTotalQuestions - 1
        )
    }
}

/**
 * En-tête avec progression, score et informations du quiz
 */
@Composable
private fun EnTeteQuiz(
    questionActuelle: Int,
    totalQuestions: Int,
    score: Int,
    questionsValidees: Int,
    progression: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Titre et numéro de question
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Question $questionActuelle sur $totalQuestions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Score : $score/$questionsValidees",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Barre de progression
            LinearProgressIndicator(
                progress = { progression },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Pourcentage de progression
            Text(
                text = "${(progression * 100).toInt()}% complété",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Card pour afficher la question
 */
@Composable
private fun CarteQuestion(
    numeroQuestion: Int,
    texteQuestion: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Question $numeroQuestion",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = texteQuestion,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
            )
        }
    }
}

/**
 * Card pour une réponse cliquable avec état visuel
 */
@Composable
private fun CarteReponse(
    reponse: ReponseUtilisateur,
    texteReponse: String,
    estValidee: Boolean,
    onSelection: () -> Unit
) {
    val couleurFond = when {
        estValidee && reponse.estCorrecte == true -> MaterialTheme.colorScheme.primaryContainer
        estValidee && reponse.estMauvaiseReponse -> MaterialTheme.colorScheme.errorContainer
        reponse.estSelectionnee -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surface
    }
    
    val couleurBordure = when {
        estValidee && reponse.estCorrecte == true -> MaterialTheme.colorScheme.primary
        estValidee && reponse.estMauvaiseReponse -> MaterialTheme.colorScheme.error
        reponse.estSelectionnee -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }
    
    val couleurTexte = when {
        estValidee && reponse.estCorrecte == true -> MaterialTheme.colorScheme.onPrimaryContainer
        estValidee && reponse.estMauvaiseReponse -> MaterialTheme.colorScheme.onErrorContainer
        else -> MaterialTheme.colorScheme.onSurface
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (reponse.estSelectionnee || estValidee) 2.dp else 1.dp,
                color = couleurBordure,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = !estValidee) { onSelection() },
        colors = CardDefaults.cardColors(containerColor = couleurFond),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (reponse.estSelectionnee) 4.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icône de sélection
            val icone = when {
                estValidee && reponse.estCorrecte == true -> Icons.Default.CheckCircle
                estValidee && reponse.estMauvaiseReponse -> Icons.Default.Cancel
                reponse.estSelectionnee -> Icons.Default.CheckCircle
                else -> Icons.Default.RadioButtonUnchecked
            }
            
            Icon(
                imageVector = icone,
                contentDescription = null,
                tint = couleurBordure,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Texte de la réponse
            Text(
                text = texteReponse,
                style = MaterialTheme.typography.bodyLarge,
                color = couleurTexte,
                modifier = Modifier.weight(1f),
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
            )
        }
    }
}

/**
 * Bouton d'action en bas d'écran
 */
@Composable
private fun BoutonAction(
    estValidee: Boolean,
    peutValider: Boolean,
    onValider: () -> Unit,
    onQuestionSuivante: () -> Unit,
    estDerniereQuestion: Boolean
) {
    if (estValidee) {
        Button(
            onClick = onQuestionSuivante,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = if (estDerniereQuestion) "Voir les résultats" else "Question suivante",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        Button(
            onClick = onValider,
            enabled = peutValider,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text(
                text = "Valider la réponse",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}