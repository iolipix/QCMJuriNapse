package com.napse.qcmjuridique.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.napse.qcmjuridique.data.*
import com.napse.qcmjuridique.repository.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel principal pour gérer l'état du quiz QCM avec révision intelligente
 */
class QuizViewModel(
    private val repository: QuestionRepository
) : ViewModel() {

    // État global du quiz
    private val _etatQuiz = MutableStateFlow(EtatQuiz())
    val etatQuiz: StateFlow<EtatQuiz> = _etatQuiz.asStateFlow()

    // État de la question actuelle
    private val _etatQuestionActuelle = MutableStateFlow<EtatQuestion?>(null)
    val etatQuestionActuelle: StateFlow<EtatQuestion?> = _etatQuestionActuelle.asStateFlow()

    // État de chargement
    private val _estEnChargement = MutableStateFlow(false)
    val estEnChargement: StateFlow<Boolean> = _estEnChargement.asStateFlow()

    // Messages d'erreur
    private val _messageErreur = MutableStateFlow<String?>(null)
    val messageErreur: StateFlow<String?> = _messageErreur.asStateFlow()

    // Tracking du temps et des questions ratées
    private var tempsDebut: Long = 0
    private val questionsRatees = mutableListOf<Int>()

    // Matière actuelle
    private var subjectId: String = ""
    private var subjectName: String = ""

    /**
     * Démarre un quiz pour une matière spécifique avec révision intelligente
     */
    fun demarrerQuiz(subjectId: String, subjectName: String, revisionIntelligente: Boolean = true) {
        viewModelScope.launch {
            _estEnChargement.value = true
            _messageErreur.value = null
            this@QuizViewModel.subjectId = subjectId
            this@QuizViewModel.subjectName = subjectName
            tempsDebut = System.currentTimeMillis()
            questionsRatees.clear()
            
            try {
                val questions = if (revisionIntelligente) {
                    repository.genererQuizIntelligent(subjectId, 20)
                } else {
                    repository.chargerQuestions(subjectId)?.questions ?: emptyList()
                }
                
                if (questions.isNotEmpty()) {
                    initialiserQuiz(questions, subjectId, subjectName)
                } else {
                    _messageErreur.value = "Aucune question n'a pu être chargée pour cette matière"
                }
            } catch (e: Exception) {
                _messageErreur.value = "Erreur lors du chargement : ${e.message}"
            } finally {
                _estEnChargement.value = false
            }
        }
    }

    /**
     * Initialise le quiz avec les questions chargées
     */
    private fun initialiserQuiz(questions: List<Question>, subjectId: String, subjectName: String) {
        _etatQuiz.value = EtatQuiz(
            subjectId = subjectId,
            subjectName = subjectName,
            questions = questions,
            questionActuelleIndex = 0,
            score = 0,
            questionsValidees = 0
        )
        
        // Initialiser la première question
        questions.firstOrNull()?.let { premieQuestion ->
            _etatQuestionActuelle.value = EtatQuestion.creerEtatInitial(premieQuestion)
        }
    }

    /**
     * Sélectionne ou désélectionne une réponse
     */
    fun selectionnerReponse(indexReponse: Int) {
        val etatActuel = _etatQuestionActuelle.value ?: return
        
        // Ne pas permettre de modifier après validation
        if (etatActuel.estValidee) return
        
        val nouvellesReponses = etatActuel.reponsesUtilisateur.mapIndexed { index, reponse ->
            if (index == indexReponse) {
                reponse.copy(estSelectionnee = !reponse.estSelectionnee)
            } else {
                reponse
            }
        }
        
        _etatQuestionActuelle.value = etatActuel.copy(reponsesUtilisateur = nouvellesReponses)
    }

    /**
     * Valide la question actuelle et calcule le score
     */
    fun validerQuestion() {
        val etatActuel = _etatQuestionActuelle.value ?: return
        
        // Récupérer les réponses sélectionnées
        val reponsesSelectionnees = etatActuel.reponsesUtilisateur
            .mapIndexed { index, reponse -> if (reponse.estSelectionnee) index else null }
            .filterNotNull()
            .toSet()
        
        // Vérifier si c'est correct
        val bonnesReponses = etatActuel.question.correctAnswers.toSet()
        val estCorrecte = reponsesSelectionnees == bonnesReponses
        
        // Marquer chaque réponse
        val reponsesValidees = etatActuel.reponsesUtilisateur.map { reponse ->
            val estBonneReponse = reponse.index in bonnesReponses
            val estSelectionnee = reponse.estSelectionnee
            
            reponse.copy(
                estCorrecte = estBonneReponse,
                estMauvaiseReponse = estSelectionnee && !estBonneReponse
            )
        }
        
        // Mettre à jour l'état de la question
        _etatQuestionActuelle.value = etatActuel.copy(
            reponsesUtilisateur = reponsesValidees,
            estValidee = true,
            estCorrecte = estCorrecte
        )
        
        // Enregistrer la question ratée si incorrect
        if (!estCorrecte) {
            questionsRatees.add(etatActuel.question.id)
        }
        
        // Mettre à jour le score global
        val etatQuizActuel = _etatQuiz.value
        _etatQuiz.value = etatQuizActuel.copy(
            score = if (estCorrecte) etatQuizActuel.score + 1 else etatQuizActuel.score,
            questionsValidees = etatQuizActuel.questionsValidees + 1,
            questionsRatees = questionsRatees.toList()
        )
    }

    /**
     * Passe à la question suivante
     */
    fun questionSuivante() {
        val etatQuizActuel = _etatQuiz.value
        val prochainIndex = etatQuizActuel.questionActuelleIndex + 1
        
        if (prochainIndex < etatQuizActuel.questions.size) {
            // Passer à la question suivante
            val prochaineQuestion = etatQuizActuel.questions[prochainIndex]
            _etatQuestionActuelle.value = EtatQuestion.creerEtatInitial(prochaineQuestion)
            _etatQuiz.value = etatQuizActuel.copy(questionActuelleIndex = prochainIndex)
        } else {
            // Quiz terminé - sauvegarder les résultats
            terminerQuiz()
        }
    }

    /**
     * Termine le quiz et sauvegarde les résultats
     */
    private fun terminerQuiz() {
        viewModelScope.launch {
            val etatQuizActuel = _etatQuiz.value
            val tempsEcoule = (System.currentTimeMillis() - tempsDebut) / 1000
            
            try {
                repository.sauvegarderResultatQuiz(
                    subjectId = etatQuizActuel.subjectId,
                    subjectName = etatQuizActuel.subjectName,
                    score = etatQuizActuel.score,
                    totalQuestions = etatQuizActuel.nombreTotalQuestions,
                    questionsRatees = questionsRatees,
                    timeSpentSeconds = tempsEcoule
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            // Marquer le quiz comme terminé
            _etatQuiz.value = etatQuizActuel.copy(estTermine = true)
            _etatQuestionActuelle.value = null
        }
    }

    /**
     * Recommence le quiz depuis le début
     */
    fun recommencerQuiz() {
        if (subjectId.isNotEmpty()) {
            demarrerQuiz(subjectId, subjectName, revisionIntelligente = false)
        }
    }

    /**
     * Recommence avec révision intelligente
     */
    fun melangerEtRecommencer() {
        if (subjectId.isNotEmpty()) {
            demarrerQuiz(subjectId, subjectName, revisionIntelligente = true)
        }
    }

    /**
     * Efface le message d'erreur
     */
    fun effacerErreur() {
        _messageErreur.value = null
    }

    /**
     * Vérifie si une réponse peut être validée (au moins une sélection)
     */
    fun peutValider(): Boolean {
        return _etatQuestionActuelle.value?.reponsesUtilisateur?.any { it.estSelectionnee } == true
    }
}