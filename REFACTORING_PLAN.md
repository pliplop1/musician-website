# Plan de Refactoring - Musician Website

**Date**: 2025-10-11
**Objectif**: Réduire la duplication de code, améliorer la maintenabilité et fragmenter les gros fichiers

---

## 📊 Statistiques

- **13 composants Vue** (1,296 lignes au total)
- **35 templates Thymeleaf** (environ 15,000+ lignes)
- **28 contrôleurs Java** (environ 5,000+ lignes)
- **Code dupliqué estimé**: ~30-40%

---

## 🔴 PROBLÈMES CRITIQUES DÉTECTÉS

### 1. **DUPLICATION MASSIVE entre FeaturedTracksSection.vue et FeaturedVideosSection.vue**

**Lignes**: 707 vs 589 lignes (environ 80% de code similaire)

#### Code dupliqué identifié:

| Fonction/Code | Emplacement | Description |
|---|---|---|
| `fetchTracks()` / `fetchVideos()` | Lines 10-56 | Même logique de cache localStorage (24h) |
| `isHtmlEmbed()` | Lines 59-62 / Similar | Détection HTML vs URL identique |
| `getPlayerType()` | Lines 72-81 / Similar | Détection Spotify/SoundCloud identique |
| `getExternalLink()` | Lines 84-102 / Similar | Conversion embed->normal URL |
| `openTrack()` / `openVideo()` | Lines 111-124 | Logique modale identique |
| `closeModal()` | Lines 127-132 | Fermeture modale identique |
| **Styles CSS** | Lines 253-707 | 95% des styles identiques (couleurs différentes) |
| Modal template | Lines 192-249 | Structure modale identique |
| Loading/Error states | Lines 147-155 | Gestion états identique |

#### Commentaires inutiles/debug:
```javascript
// Lines 112-118 - Logs de debug à supprimer
console.log('=== DEBUG openTrack ===')
console.log('Track data:', track)
// etc...
```

---

### 2. **DUPLICATION MASSIVE entre musique.html et videos.html**

**Lignes**: 872 vs 784 lignes (environ 85% de code similaire)

#### Code dupliqué identifié:

| Code | Emplacement | Description |
|---|---|---|
| **Styles CSS** | Lines 11-499 | 90% identiques (couleur primaire change) |
| `getPlayerType()` | Lines 596-607 vs Similar | Fonction JavaScript identique |
| `getExternalLink()` | Lines 610-627 vs Similar | Fonction JavaScript identique |
| `displayTracks()` / `displayVideos()` | Lines 630-695 | Logique de pagination identique |
| `updatePaginationButton()` | Lines 698-724 | Gestion pagination identique |
| Modal JavaScript | Lines 740-798 | Logique modale identique |
| Recherche | Lines 801-814 | Fonction de recherche identique |
| Footer Thymeleaf | Lines 570-587 | Footer dupliqué partout |

---

### 3. **Commentaires inutiles dans musique.html**

```html
<!-- Line 558: Commentaire évident -->
<!-- Le contenu audio sera injecté ici -->

<!-- Line 564: Commentaire évident -->
<!-- Bouton "Ouvrir dans Spotify/SoundCloud" sera ajouté ici -->
```

---

## 📝 PLAN DE REFACTORING

### PHASE 1: Composants Réutilisables Vue (PRIORITÉ HAUTE)

#### 1.1 Créer `composables/useFeaturedMedia.js`
**Objectif**: Mutualiser la logique de cache et fetch

```javascript
export function useFeaturedMedia(mediaType, apiEndpoint) {
  // Logique commune de cache localStorage
  // Logique commune de fetch API
  // Gestion loading/error
  return { items, loading, error, fetchItems }
}
```

**Impact**: Élimine 100+ lignes dupliquées

---

#### 1.2 Créer `composables/useMediaPlayer.js`
**Objectif**: Mutualiser la logique de lecteur/modal

```javascript
export function useMediaPlayer() {
  // isHtmlEmbed()
  // getPlayerType()
  // getExternalLink()
  // openModal(), closeModal()
  return { ... }
}
```

**Impact**: Élimine 80+ lignes dupliquées

---

#### 1.3 Créer `components/MediaCard.vue`
**Objectif**: Composant carte générique pour tracks/videos

```vue
<MediaCard
  :item="track"
  :thumbnail="getThumbnailUrl(track)"
  :type="getPlayerType(track)"
  @click="openModal(track)"
/>
```

**Impact**: Élimine 50+ lignes dupliquées

---

#### 1.4 Créer `components/MediaModal.vue`
**Objectif**: Composant modal générique

```vue
<MediaModal
  :item="selectedItem"
  :show="showModal"
  @close="closeModal"
/>
```

**Impact**: Élimine 60+ lignes de template dupliqué

---

#### 1.5 Créer fichier CSS commun `styles/media-shared.css`
**Objectif**: Mutualiser les styles

```css
/* Styles communs pour cartes, modales, animations */
/* Variables CSS pour couleurs thématiques */
:root {
  --primary-color-music: #22c55e;
  --primary-color-video: #ef4444;
}
```

**Impact**: Élimine 400+ lignes de CSS dupliqué

---

### PHASE 2: Templates Thymeleaf (PRIORITÉ HAUTE)

#### 2.1 Créer `fragments/media-gallery-layout.html`
**Objectif**: Template fragment réutilisable

```html
<th:block th:fragment="media-gallery(title, subtitle, primaryColor, apiEndpoint)">
  <!-- Structure commune de musique.html et videos.html -->
</th:block>
```

**Impact**: Élimine 600+ lignes dupliquées

---

#### 2.2 Créer `static/js/media-gallery.js`
**Objectif**: JavaScript réutilisable

```javascript
class MediaGallery {
  constructor(apiEndpoint, primaryColor) {
    // Toute la logique commune
  }
}
```

**Impact**: Élimine 250+ lignes de JavaScript dupliqué

---

#### 2.3 Créer `static/css/media-gallery.css`
**Objectif**: Styles réutilisables

**Impact**: Élimine 450+ lignes de CSS dupliqué

---

### PHASE 3: Nettoyage (PRIORITÉ MOYENNE)

#### 3.1 Supprimer les console.log de debug
**Fichiers concernés**:
- `FeaturedTracksSection.vue:112-118`
- `FeaturedTracksSection.vue:26, 34, 52`

#### 3.2 Supprimer les commentaires évidents
**Fichiers concernés**:
- `musique.html:558, 564`
- `videos.html` (similaire)

#### 3.3 Supprimer HelloWorld.vue
**Raison**: Composant non utilisé (template par défaut Vite)

---

## 🎯 RÉSULTAT ATTENDU

### Avant Refactoring:
```
FeaturedTracksSection.vue:    707 lignes
FeaturedVideosSection.vue:    589 lignes
musique.html:                 872 lignes
videos.html:                  784 lignes
TOTAL:                      2,952 lignes
```

### Après Refactoring:
```
FeaturedTracksSection.vue:    120 lignes (utilise composables)
FeaturedVideosSection.vue:    120 lignes (utilise composables)
musique.html:                 200 lignes (utilise fragment)
videos.html:                  200 lignes (utilise fragment)

+ useFeaturedMedia.js:        100 lignes
+ useMediaPlayer.js:           80 lignes
+ MediaCard.vue:               60 lignes
+ MediaModal.vue:             100 lignes
+ media-shared.css:           150 lignes
+ fragments/media-gallery:    250 lignes
+ media-gallery.js:           200 lignes
+ media-gallery.css:          200 lignes

TOTAL NOUVEAU:              1,780 lignes
RÉDUCTION:                  1,172 lignes (40%)
```

---

## 📁 STRUCTURE PROPOSÉE

```
vue-frontend/src/
├── composables/
│   ├── useFeaturedMedia.js    [NOUVEAU]
│   └── useMediaPlayer.js       [NOUVEAU]
├── components/
│   ├── shared/
│   │   ├── MediaCard.vue       [NOUVEAU]
│   │   └── MediaModal.vue      [NOUVEAU]
│   ├── FeaturedTracksSection.vue  [REFACTORÉ]
│   └── FeaturedVideosSection.vue  [REFACTORÉ]
└── styles/
    └── media-shared.css        [NOUVEAU]

src/main/resources/
├── templates/
│   ├── fragments/
│   │   ├── layout.html
│   │   ├── admin-layout.html
│   │   └── media-gallery-layout.html  [NOUVEAU]
│   ├── musique.html            [REFACTORÉ]
│   └── videos.html             [REFACTORÉ]
└── static/
    ├── js/
    │   └── media-gallery.js    [NOUVEAU]
    └── css/
        └── media-gallery.css   [NOUVEAU]
```

---

## ⚠️ DOUBLONS MINEURS (Priorité Basse)

### Navigation dupliquée
- **App.vue**: Navigation hardcodée
- **Fragments/layout.html**: Navigation Thymeleaf

**Recommandation**: Accepter cette duplication (nécessaire pour SPA vs SSR)

### Footer dupliqué
- Présent dans App.vue, fragments/layout.html, musique.html, videos.html

**Recommandation**: Utiliser le fragment Thymeleaf existant partout

---

## 🔍 AUTRES OBSERVATIONS

### Commentaires à garder:
- Commentaires expliquant la logique métier complexe
- Commentaires de configuration (cache TTL, etc.)

### Commentaires à supprimer:
- `// Vérifier le cache localStorage` (évident)
- `// Si moins de 24h se sont écoulées` (évident)
- `// Le contenu audio sera injecté ici` (évident)

---

## 📅 ORDRE D'EXÉCUTION RECOMMANDÉ

### Session 1 (2h):
1. Créer `composables/useFeaturedMedia.js`
2. Créer `composables/useMediaPlayer.js`
3. Refactorer `FeaturedTracksSection.vue`
4. Refactorer `FeaturedVideosSection.vue`

### Session 2 (2h):
5. Créer `components/shared/MediaCard.vue`
6. Créer `components/shared/MediaModal.vue`
7. Créer `styles/media-shared.css`
8. Intégrer dans les composants featured

### Session 3 (2h):
9. Créer `fragments/media-gallery-layout.html`
10. Créer `static/js/media-gallery.js`
11. Créer `static/css/media-gallery.css`
12. Refactorer `musique.html` et `videos.html`

### Session 4 (30min):
13. Nettoyer console.log
14. Supprimer commentaires inutiles
15. Supprimer HelloWorld.vue
16. Tests finaux

---

## ✅ CHECKLIST DE VALIDATION

- [ ] Aucune régression fonctionnelle
- [ ] Cache localStorage fonctionne (24h)
- [ ] Modales s'ouvrent/ferment correctement
- [ ] Liens externes fonctionnent
- [ ] Pagination fonctionne
- [ ] Recherche fonctionne
- [ ] Responsive (mobile/tablet/desktop)
- [ ] Pas de console errors
- [ ] Thème couleur musique (vert) préservé
- [ ] Thème couleur vidéo (rouge) préservé

---

**FIN DU RAPPORT**

Ce plan permet de réduire ~40% du code tout en améliorant la maintenabilité et la réutilisabilité.
