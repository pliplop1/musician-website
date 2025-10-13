# 📋 TODO: Tests, RGPD et RGAA

## ✅ Déjà implémenté

### Tests Backend (JUnit)
- ✅ UserServiceTest.java
- ✅ TrackServiceTest.java
- ✅ VideoServiceTest.java
- ✅ ConcertServiceTest.java
- ✅ PhotoServiceTest.java
- ✅ ArticleServiceTest.java
- ✅ UserRepositoryTest.java
- ✅ ArticleRepositoryTest.java
- ✅ SecurityConfigTest.java

---

## ❌ Tests Backend manquants

### Services sans tests (11 services)
1. ❌ BiographyServiceTest.java
2. ❌ HomepageSettingsServiceTest.java
3. ❌ EmailServiceTest.java
4. ❌ BadgeServiceTest.java
5. ❌ SocialLinkServiceTest.java
6. ❌ CommentServiceTest.java
7. ❌ NotificationServiceTest.java
8. ❌ MessageServiceTest.java
9. ❌ ConcertNotificationServiceTest.java
10. ❌ UserDetailsServiceImplTest.java

### Controllers sans tests (Priorité HAUTE pour CDA)
1. ❌ PublicApiControllerTest.java - **IMPORTANT** (API Vue.js)
2. ❌ AdminControllerTest.java
3. ❌ UserControllerTest.java
4. ❌ HomeControllerTest.java

### Tests d'intégration manquants
- ❌ Tests d'intégration API REST (MockMvc)
- ❌ Tests de sécurité (authentification/autorisation)
- ❌ Tests de validation des DTOs

---

## ❌ Tests Frontend manquants (Vue.js)

### Composables
- ❌ useFeaturedContent.test.js
- ❌ useRotationCache.test.js

### Composants principaux
- ❌ FeaturedPhotosSection.spec.js
- ❌ FeaturedVideosSection.spec.js
- ❌ FeaturedTracksSection.spec.js
- ❌ ConcertsSection.spec.js
- ❌ HeroSection.spec.js
- ❌ BiographySection.spec.js

---

## ❌ RGPD manquant (Priorité HAUTE)

### Pages légales obligatoires
1. ❌ **Politique de confidentialité** (`/privacy-policy`)
   - Collecte des données
   - Utilisation des données
   - Droits des utilisateurs (accès, rectification, suppression)
   - Durée de conservation
   - Contact DPO

2. ❌ **Mentions légales** (`/mentions-legales`)
   - Éditeur du site
   - Hébergeur
   - Responsable publication
   - Propriété intellectuelle

3. ❌ **Gestion des cookies** (`/cookies`)
   - Banner de consentement cookies
   - Types de cookies utilisés
   - Paramétrage cookies

### Fonctionnalités RGPD
4. ❌ **Droit à l'oubli** - Suppression compte utilisateur
5. ❌ **Export des données** - Télécharger ses données (JSON)
6. ❌ **Consentement explicite** - Checkbox lors inscription
7. ❌ **Double opt-in** - Email de confirmation (déjà partiellement fait)

### Controllers/Services RGPD
- ❌ PrivacyController.java (pages légales)
- ❌ DataExportService.java (export données utilisateur)
- ❌ ConsentService.java (gestion consentements)

---

## ❌ RGAA manquant (Accessibilité)

### HTML Sémantique
- ⚠️ **Vérifier** balises sémantiques (`<header>`, `<nav>`, `<main>`, `<footer>`)
- ❌ Attributs `lang` sur éléments multilingues
- ❌ Structure de titres cohérente (h1→h2→h3)

### Navigation clavier
- ❌ `tabindex` appropriés
- ❌ Navigation au clavier testée partout
- ❌ Focus visible sur tous les éléments interactifs

### ARIA
- ❌ `aria-label` sur boutons sans texte
- ❌ `aria-describedby` pour descriptions
- ❌ `role` appropriés (navigation, main, banner, etc.)
- ❌ `aria-live` pour notifications dynamiques

### Images
- ⚠️ **Vérifier** `alt` sur toutes les images
- ❌ Images décoratives avec `alt=""`

### Formulaires
- ⚠️ **Vérifier** labels associés aux inputs
- ❌ Messages d'erreur accessibles
- ❌ `aria-required` sur champs obligatoires
- ❌ `aria-invalid` sur champs en erreur

### Contrastes
- ❌ Vérifier ratios de contraste (4.5:1 minimum)
- ❌ Tester avec outils automatiques (axe DevTools)

### Multimédia
- ❌ Sous-titres pour vidéos
- ❌ Transcriptions pour audio
- ❌ Contrôles accessibles (play/pause)

---

## 🎯 Ordre de priorité recommandé

### Phase 1 - URGENT (Certification CDA)
1. ✅ Tests Controllers (PublicApiController, AdminController) - 80% couverture
2. ✅ Politique de confidentialité + Mentions légales
3. ✅ Tests services manquants (BiographyService, HomepageSettingsService)

### Phase 2 - IMPORTANT
4. ✅ Banner cookies + Gestion consentement
5. ✅ Droit à l'oubli + Export données
6. ✅ Tests Vue.js (composables + composants principaux)

### Phase 3 - BON À AVOIR
7. ✅ RGAA complet (ARIA, navigation clavier)
8. ✅ Tests d'intégration complets
9. ✅ Accessibilité multimédia

---

## 📊 Couverture estimée actuelle

- **Tests Backend**: ~40% (9 services testés / ~20 services)
- **Tests Frontend**: 0%
- **RGPD**: 10% (email confirmation existe)
- **RGAA**: 20% (HTML basique correct)

## 🎯 Objectif CDA

- **Tests Backend**: 70-80% minimum
- **Tests Frontend**: 60% minimum
- **RGPD**: 100% (obligatoire légalement)
- **RGAA**: 70% (niveau AA recommandé)
