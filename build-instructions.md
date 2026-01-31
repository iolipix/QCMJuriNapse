# 📱 Installation QCM Juridique Napse

## Méthode rapide avec Android Studio

1. **Télécharger Android Studio** : https://developer.android.com/studio
2. **Ouvrir le projet** dans Android Studio
3. **Connecter le téléphone** en USB avec débogage activé
4. **Cliquer sur Run** ▶️

## Configuration téléphone Android

### Activer le débogage USB :
1. Paramètres → À propos du téléphone
2. Appuyer 7 fois sur "Numéro de build"  
3. Paramètres → Options de développeur
4. Activer "Débogage USB"
5. Connecter en USB et autoriser le débogage

## Compilation manuelle (alternative)

Si Android Studio n'est pas disponible, tu peux :

1. **Installer Java JDK 11+**
2. **Installer Android SDK**
3. **Compiler** :
   ```bash
   ./gradlew assembleDebug
   ```
4. **APK généré** dans : `app/build/outputs/apk/debug/`
5. **Transférer l'APK** sur le téléphone
6. **Installer** en activant "Sources inconnues"

## Fonctionnalités de l'app

✅ Navigation entre semestres et matières  
✅ Quiz interactifs avec corrections  
✅ Système de révision intelligente  
✅ Historique et statistiques  
✅ Mode sombre  
✅ Base de données locale  
✅ 25 questions complètes en Droit Judiciaire Privé

## Structure des matières

**Semestre 5** :
- Droit du travail
- Libertés fondamentales  
- Droit administratif des biens
- Droit judiciaire privé ⭐ (enrichi)
- Droit pénal spécial
- Droit des obligations

L'application est prête à être utilisée pour tes révisions ! 🎓