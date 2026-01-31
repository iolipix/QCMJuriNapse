# QCM Juridique Napse

Une application Android moderne développée en Kotlin avec Jetpack Compose pour réviser des QCM (Questions à Choix Multiples) de droit, organisée par semestres et matières avec suivi de progression et révision intelligente.

## 📱 Fonctionnalités

### 🏛️ Organisation par semestres et matières
- **Navigation hiérarchique** : Semestres → Matières → Quiz
- **Semestre 5** avec ses 6 matières juridiques :
  - Droit du travail
  - Libertés fondamentales
  - Droit judiciaire privé
  - Contentieux administratif
  - Droit international privé (DIP)
  - Droit privé des biens
- Interface intuitive avec cards et statistiques par matière

### ✅ Import de QCM par matière
- Import de QCM via fichiers JSON locaux organisés par matière
- Structure JSON flexible supportant les questions à réponses multiples
- Chargement dynamique selon la matière sélectionnée

### 🎯 Mode révision avec révision intelligente
- **Révision intelligente** : Les questions ratées reviennent plus souvent (70% questions ratées, 30% nouvelles)
- Affichage d'une question à la fois pour une meilleure concentration
- Interface optimisée pour la lecture de textes juridiques longs
- Cards cliquables pour les réponses avec feedback visuel immédiat

### ✨ Système de validation avancé
- Bouton "Valider" avec état activé/désactivé selon les sélections
- Feedback immédiat : bonnes réponses en vert, mauvaises en rouge
- Affichage de toutes les bonnes réponses après validation
- Support des questions à réponses multiples

### 📊 Suivi de progression et historique
- **Base de données Room** pour sauvegarder tous les résultats
- **Historique complet par matière** avec :
  - Date et heure de chaque quiz
  - Score détaillé (ex: 14/20 - 70%)
  - Temps passé sur le quiz
  - Statistiques globales (meilleur score, moyenne)
- **Progression par matière** :
  - Nombre de quiz complétés
  - Score moyen
  - Évolution dans le temps

### 🧠 Révision intelligente
- **Algorithme adaptatif** : Questions ratées prioritaires
- **Tracking des échecs** : Compteur par question ratée
- **Amélioration progressive** : Questions maîtrisées moins fréquentes
- **Statistiques de progression** pour chaque matière

### 📈 Écran d'historique détaillé
- **Vue globale** : Tous les quiz d'une matière
- **Statistiques complètes** :
  - Total quiz complétés
  - Meilleur score atteint
  - Score moyen
  - Évolution des performances
- **Détails par quiz** : Score, date, temps passé
- **Indicateurs visuels** de performance

### 🎨 Interface utilisateur moderne
- Design Material Design 3 avec navigation fluide
- Support natif du mode sombre
- Interface optimisée pour la lecture de longs textes juridiques
- Animations fluides et transitions naturelles
- Cards avec indicateurs de progression

## 🏗️ Architecture

### Architecture MVVM avec Room Database
- **Model** : Entities Room (QuizResult, SubjectProgress, FailedQuestion)
- **Repository** : QuestionRepository avec gestion cache et base de données
- **ViewModel** : ViewModels séparés par écran avec StateFlow
- **View** : Composables Jetpack Compose réactifs avec Navigation

### Technologies utilisées
- **Kotlin** : Langage principal
- **Jetpack Compose** : Interface utilisateur moderne
- **Room Database** : Persistance locale des données
- **Navigation Compose** : Navigation entre écrans
- **StateFlow** : Gestion d'état réactive
- **Gson** : Parsing JSON
- **Material Design 3** : Design system

## 📄 Format JSON par matière

Exemple de structure pour `droit_travail.json` :

```json
{
  "semester": "Semestre 5",
  "subject": "Droit du travail", 
  "questions": [
    {
      "id": 1,
      "question": "La durée légale du travail en France est fixée à :",
      "choices": [
        "35 heures par semaine",
        "39 heures par semaine",
        "40 heures par semaine", 
        "37 heures par semaine"
      ],
      "correctAnswers": [0]
    },
    {
      "id": 2,
      "question": "Lesquelles de ces affirmations sont correctes concernant les heures supplémentaires ?",
      "choices": [
        "Majoration de 25% pour les 8 premières heures",
        "Majoration de 50% au-delà de 43h",
        "Possibilité de repos compensateur",
        "Paiement obligatoire en argent"
      ],
      "correctAnswers": [0, 1, 2]
    }
  ]
}
```

## 📊 Base de données Room

### Tables principales :
- **quiz_results** : Résultats de chaque quiz (score, date, temps)
- **subject_progress** : Progression par matière (moyenne, meilleur score)
- **failed_questions** : Questions ratées avec compteur d'échecs

### Fonctionnalités avancées :
- **Révision intelligente** basée sur l'historique d'échecs
- **Statistiques temps réel** par matière  
- **Sauvegarde automatique** de tous les résultats
- **Optimisation des performances** avec Flow et coroutines

## 🚀 Installation et utilisation

### Prérequis
- Android Studio Hedgehog | 2023.1.1 ou plus récent
- Kotlin 1.9.20+
- SDK Android 24+ (Android 7.0)
- Gradle 8.2+

### Compilation
```bash
# Cloner le repository
git clone [url-du-repo]

# Ouvrir dans Android Studio
# Synchroniser les dépendances Gradle
# Compiler et lancer sur émulateur ou appareil
```

### Utilisation
1. **Accueil** : Sélectionner "Semestre 5"
2. **Matières** : Choisir une matière (ex: "Droit du travail")
3. **Quiz** : Commencer un quiz (révision intelligente par défaut)
4. **Historique** : Consulter ses performances via le bouton "Historique"

## 📱 Parcours utilisateur

### 🏠 Écran d'accueil
```
📚 QCM Juridique Napse
└── 📖 Semestre 5 (6 matières disponibles)
```

### 📝 Écran des matières
```
📖 Semestre 5
├── ⚖️ Droit du travail [🎯 Commencer] [📊 Historique]
├── 🏛️ Libertés fondamentales [🎯 Commencer] [📊 Historique]  
├── 📋 Droit judiciaire privé [🎯 Commencer] [📊 Historique]
├── 🏛️ Contentieux administratif [🎯 Commencer] [📊 Historique]
├── 🌍 Droit international privé [🎯 Commencer] [📊 Historique]
└── 🏠 Droit privé des biens [🎯 Commencer] [📊 Historique]
```

### 📊 Écran d'historique
```
📊 Historique - Droit du travail

📈 Statistiques globales
├── Quiz complétés: 12
├── Meilleur score: 90%  
└── Moyenne: 76%

📋 Historique des quiz (12)
├── 18/20 (90%) - 31/01/2026 à 14:30 - Durée: 12min
├── 15/20 (75%) - 30/01/2026 à 10:15 - Durée: 15min
└── 14/20 (70%) - 29/01/2026 à 16:45 - Durée: 18min
```

## 🛠️ Structure du projet

```
app/
├── src/main/java/com/napse/qcmjuridique/
│   ├── data/
│   │   └── Models.kt                 # Data classes et Room entities
│   ├── database/
│   │   ├── QuizDatabase.kt           # Configuration Room
│   │   └── QuizDao.kt                # Data Access Objects
│   ├── repository/
│   │   └── QuestionRepository.kt     # Logique métier et cache
│   ├── viewmodel/
│   │   ├── QuizViewModel.kt          # Quiz avec révision intelligente
│   │   ├── HomeViewModel.kt          # Navigation semestres
│   │   ├── SubjectViewModel.kt       # Navigation matières
│   │   ├── HistoryViewModel.kt       # Historique et statistiques
│   │   └── ViewModelFactories.kt     # Factory pattern
│   ├── ui/
│   │   ├── screens/
│   │   │   ├── HomeScreen.kt         # Écran d'accueil
│   │   │   ├── SubjectScreen.kt      # Écran des matières
│   │   │   └── HistoryScreen.kt      # Écran d'historique
│   │   ├── components/
│   │   │   └── QuizComponents.kt     # Composants réutilisables
│   │   ├── QuizScreen.kt             # Écran principal quiz
│   │   └── theme/                    # Thème et styles
│   └── MainActivity.kt               # Navigation principale
├── src/main/assets/                  # Fichiers JSON par matière
│   ├── droit_travail.json
│   ├── libertes_fondamentales.json
│   ├── droit_judiciaire_prive.json
│   ├── contentieux_administratif.json
│   ├── droit_international_prive.json
│   └── droit_prive_biens.json
└── src/main/res/                     # Ressources Android
```

## 🔧 Personnalisation et extension

### Ajouter de nouvelles matières
1. Créer un fichier JSON dans `/assets/`
2. Ajouter la matière dans `QuestionRepository.getSemesterData()`
3. L'application détectera automatiquement les nouvelles questions

### Ajouter de nouveaux semestres
1. Étendre la liste dans `getSemesterData()`
2. Organiser les fichiers JSON par semestre
3. Adapter l'interface si nécessaire

### Personnaliser l'algorithme de révision
- Modifier les proportions dans `genererQuizIntelligent()` (actuellement 70/30)
- Ajuster les critères de priorisation des questions ratées
- Implémenter de nouveaux algorithmes d'apprentissage adaptatif

## 🎯 Fonctionnalités avancées réalisées

### ✅ Révision intelligente
- Algorithme adaptatif basé sur l'historique d'échecs
- Questions ratées prioritaires (70% du quiz)
- Diminution progressive de la fréquence des questions maîtrisées

### ✅ Sauvegarde complète
- Room Database avec 3 tables optimisées
- Sauvegarde automatique de tous les résultats  
- Statistiques temps réel par matière
- Historique complet avec temps passé

### ✅ Interface moderne
- Navigation fluide entre 4 écrans principaux
- Material Design 3 avec dark mode
- Composables réutilisables et performants
- Animations et transitions naturelles

### ✅ Architecture robuste
- MVVM avec Repository pattern
- StateFlow pour la réactivité
- Factory pattern pour l'injection de dépendances
- Séparation claire des responsabilités

## 🤝 Contribution et extension

Ce projet est conçu pour être facilement extensible :

### Nouvelles fonctionnalités possibles :
- **Timer par quiz** avec sauvegarde du temps
- **Catégories de difficulté** (facile, moyen, difficile)
- **Mode exam blanc** avec conditions d'examen
- **Synchronisation cloud** des résultats
- **Partage de résultats** via réseaux sociaux
- **Mode multijoueur** en temps réel
- **Notifications de révision** programmées
- **Export PDF** des statistiques
- **Graphiques de progression** avancés
- **Mode hors ligne** complet

### Architecture extensible :
1. Fork le repository
2. Ajouter de nouveaux ViewModels pour nouvelles fonctionnalités
3. Étendre la base de données Room selon les besoins
4. Créer de nouveaux écrans Compose
5. Ouvrir une Pull Request

## 📄 Licence

[Licence à définir selon vos besoins]

## 📞 Support

Pour toute question ou suggestion concernant l'application QCM Juridique Napse, n'hésitez pas à ouvrir une issue sur GitHub.

---

**Développé avec ❤️ pour la communauté juridique française**  
*Une solution complète pour réviser efficacement le droit avec un suivi de progression personnalisé*