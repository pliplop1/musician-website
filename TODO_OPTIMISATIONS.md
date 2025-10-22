# 🎯 TODO - Optimisations prioritaires Duo Black & White

Date de création : 22 Octobre 2025
Score actuel : 82/100
Objectif : 95/100

---

## 🔴 URGENT - À faire cette semaine (7h estimées)

### 1. ⚡ Externaliser JavaScript inline (2h) - **PRIORITÉ #1**
**Impact : Performance +15 points**

**Fichiers concernés :**
- `src/main/resources/templates/musique.html` (lignes 86-662)
- `src/main/resources/templates/galerie.html` (lignes 90-462)
- `src/main/resources/templates/videos.html` (lignes 84-491)

**À créer :**
- `src/main/resources/static/js/music-page.js`
- `src/main/resources/static/js/gallery-page.js`
- `src/main/resources/static/js/video-page.js`

**Action :**
```html
<!-- Remplacer dans musique.html -->
<script th:src="@{/js/music-page.js}" defer></script>
```

---

### 2. 📐 Ajouter width/height sur toutes les images (1h)
**Impact : CLS = 0, Accessibilité RGAA**

**Fichiers concernés :**
- `src/main/resources/templates/galerie.html:127`
- `src/main/resources/templates/videos.html:139`
- `src/main/resources/templates/index.html:91-94`

**Exemple de correction :**
```html
<!-- AVANT -->
<img src="..." alt="..." loading="lazy">

<!-- APRÈS -->
<img src="..." alt="..." width="800" height="600" loading="lazy">
```

---

### 3. 😀 Corriger emojis avec aria-hidden (30min)
**Impact : Accessibilité RGAA**

**Fichiers concernés :**
- Tous les `<h1>`, `<h3>` avec emojis/icônes Font Awesome
- `src/main/resources/templates/musique.html:20`
- `src/main/resources/templates/index.html:57`

**Exemple de correction :**
```html
<!-- AVANT -->
<h1 class="page-title">
    <i class="fas fa-music"></i> Toute notre musique
</h1>

<!-- APRÈS -->
<h1 class="page-title">
    <i class="fas fa-music" aria-hidden="true"></i> Toute notre musique
</h1>

<!-- AVANT -->
<h3>💬 Commentaires</h3>

<!-- APRÈS -->
<h3><span aria-hidden="true">💬</span> Commentaires</h3>
```

---

### 4. 🍪 Cookie consent pour embeds YouTube/Spotify (3h)
**Impact : RGPD 100%**

**Problème actuel :**
- `musique.html:283`, `videos.html:254`
- Les iframes YouTube/Spotify se chargent sans consentement

**Solution à implémenter :**
```javascript
// Dans musique.html et videos.html
function loadEmbed(embedCode, containerId) {
    const consent = JSON.parse(localStorage.getItem('cookieConsent'));
    const container = document.getElementById(containerId);

    if (!consent || !consent.performance) {
        // Afficher placeholder
        container.innerHTML = `
            <div class="embed-placeholder" style="background: #1a1a1a; padding: 60px; text-align: center; border-radius: 8px;">
                <p style="color: #fff; margin-bottom: 20px;">
                    Pour charger ce contenu, vous devez accepter les cookies de performance.
                </p>
                <button onclick="showCookieSettings()" class="btn btn-primary">
                    Gérer les cookies
                </button>
            </div>
        `;
    } else {
        // Charger l'iframe
        container.innerHTML = embedCode;
    }
}
```

**Fichiers à modifier :**
- `src/main/resources/templates/musique.html` (fonction `openModal`)
- `src/main/resources/templates/videos.html` (fonction `openModal`)

---

### 5. 🐛 Supprimer console.log() en production (30min)

**Fichiers concernés :**
- `musique.html:352`
- `galerie.html:251`
- `videos.html:281`

**Solution :**
Créer `src/main/resources/static/js/logger.js` :
```javascript
// Logger conditionnel
const isDev = window.location.hostname === 'localhost' ||
              window.location.hostname === '127.0.0.1';

window.logger = {
    log: isDev ? console.log.bind(console) : () => {},
    warn: isDev ? console.warn.bind(console) : () => {},
    error: console.error.bind(console) // Toujours logger les erreurs
};
```

Puis remplacer tous les `console.log()` par `logger.log()`

---

## 🟠 IMPORTANT - À faire ce mois-ci (8h estimées)

### 6. 🔍 Schema.org JSON-LD (2h)
**Impact : SEO rich snippets**

**À ajouter dans `fragments/head.html` :**
```html
<script type="application/ld+json" th:inline="javascript">
{
  "@context": "https://schema.org",
  "@type": "MusicGroup",
  "name": "Duo Black & White",
  "genre": ["Jazz", "Musique classique", "Piano"],
  "url": "https://duoblackandwhite.com",
  "description": "Duo piano classique jazz à Mulhouse",
  "sameAs": [
    "https://www.facebook.com/DuoBlackandWhiteMP/",
    "https://www.youtube.com/@duoblackandwhite"
  ],
  "member": [
    {
      "@type": "Person",
      "name": "Marie-Paule"
    },
    {
      "@type": "Person",
      "name": "[Nom du second membre]"
    }
  ]
}
</script>
```

---

### 7. 🔒 Focus trap dans modals (1h)
**Impact : Accessibilité RGAA 12.7**

**Fichiers concernés :**
- `musique.html:66-81`
- `galerie.html:72-84`
- `videos.html:66-80`

**Code à ajouter :**
```javascript
function trapFocus(modal) {
    const focusableElements = modal.querySelectorAll(
        'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
    );
    const firstElement = focusableElements[0];
    const lastElement = focusableElements[focusableElements.length - 1];

    modal.addEventListener('keydown', function(e) {
        if (e.key === 'Tab') {
            if (e.shiftKey) { // Shift + Tab
                if (document.activeElement === firstElement) {
                    e.preventDefault();
                    lastElement.focus();
                }
            } else { // Tab
                if (document.activeElement === lastElement) {
                    e.preventDefault();
                    firstElement.focus();
                }
            }
        }
    });

    firstElement.focus();
}

// Appeler dans openModal()
function openModal(id) {
    const modal = document.getElementById(id);
    modal.classList.remove('hidden');
    trapFocus(modal);
}
```

---

### 8. 🏷️ Labels sur formulaires (1h)

**Fichiers concernés :**
- `actualites.html:40-42`

**Correction :**
```html
<!-- AVANT -->
<textarea name="content" placeholder="Ajouter un commentaire..." required></textarea>

<!-- APRÈS -->
<label for="comment-content" class="sr-only">Votre commentaire</label>
<textarea id="comment-content" name="content" placeholder="Ajouter un commentaire..." required></textarea>
```

**Ajouter dans `style.css` si pas déjà présent :**
```css
.sr-only {
    position: absolute;
    width: 1px;
    height: 1px;
    padding: 0;
    margin: -1px;
    overflow: hidden;
    clip: rect(0, 0, 0, 0);
    white-space: nowrap;
    border-width: 0;
}
```

---

### 9. 🎨 Optimiser Font Awesome (2h)
**Impact : Performance +10 points**

**Problème actuel :**
- Fichier CDN 900KB (all.min.css)
- Blocking render

**Solution : Self-host avec subset**

1. Identifier les icônes utilisées (fas fa-music, fas fa-image, etc.)
2. Générer un subset sur https://fontawesome.com/download
3. Placer dans `src/main/resources/static/fonts/fontawesome/`
4. Remplacer dans templates :

```html
<!-- AVANT -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

<!-- APRÈS -->
<link rel="stylesheet" th:href="@{/fonts/fontawesome/fontawesome.min.css}">
```

---

### 10. 🖼️ Optimiser thumbnails YouTube (15min)
**Impact : Performance +5 points**

**Fichier :** `videos.html` (ligne 99-107)

```javascript
// AVANT
return `https://img.youtube.com/vi/${videoId}/maxresdefault.jpg`; // 1280x720, ~200KB

// APRÈS
return `https://img.youtube.com/vi/${videoId}/mqdefault.jpg`; // 320x180, ~20KB
// OU
return `https://img.youtube.com/vi/${videoId}/hqdefault.jpg`; // 480x360, ~50KB
```

---

### 11. 📊 API batch pour likes (2h)
**Impact : Performance +5 points, N+1 résolu**

**Problème actuel :** `galerie.html:385-401`
```javascript
for (const photo of allPhotos) {
    const response = await fetch(`/api/photos/${photo.id}/like-status`);
}
```

**Solution :**

1. Créer nouveau endpoint dans `PhotoApiController.java` :
```java
@GetMapping("/like-status/bulk")
public ResponseEntity<Map<Long, Boolean>> getLikeStatusBulk(
    @RequestParam List<Long> ids,
    Authentication authentication
) {
    // Récupérer tous les statuts en une requête SQL
    Map<Long, Boolean> statuses = photoService.getLikeStatusBulk(ids, username);
    return ResponseEntity.ok(statuses);
}
```

2. Modifier `galerie.html` :
```javascript
const photoIds = allPhotos.map(p => p.id).join(',');
const response = await fetch(`/api/photos/like-status/bulk?ids=${photoIds}`);
const statuses = await response.json();

allPhotos.forEach(photo => {
    photo.liked = statuses[photo.id] || false;
});
```

---

## 🟢 NICE TO HAVE - Prochains mois

### 12. 📱 Service Worker / PWA (1 jour)
- Workbox ou Service Worker manuel
- Cache-First pour assets statiques
- Offline fallback

### 13. 🧪 Tests E2E Playwright (2 jours)
- Parcours critiques : login, like, comment
- CI/CD integration

### 14. 🔄 CI/CD Pipeline (1 jour)
- GitHub Actions ou GitLab CI
- Build automatique + tests + deploy

### 15. 📈 Monitoring Grafana + Prometheus (2 jours)
- Métriques Spring Boot Actuator
- Dashboards custom

---

## ✅ DÉJÀ FAIT (À ne pas refaire)

✅ Sitemap.xml dynamique
✅ robots.txt
✅ Meta tags Open Graph / Twitter Cards
✅ Skip link
✅ ARIA labels exhaustifs
✅ Navigation clavier (Escape, Tab)
✅ Banner cookies granulaire
✅ Politique de confidentialité RGPD
✅ Headers HTTP sécurisés (HSTS, CSP, X-Frame-Options, etc.)
✅ Protection brute force (5 tentatives, blocage 15min)
✅ BCrypt pour mots de passe
✅ Protection CSRF
✅ Dockerfile multi-stage sécurisé
✅ JaCoCo couverture tests (50% minimum)
✅ OWASP Dependency Check
✅ Lazy loading images
✅ Pagination côté client
✅ Cache LocalStorage (24h)
✅ Resource Hints DNS/Preconnect

---

## 📊 Estimation temps total

**URGENT (cette semaine) :** 7h
**IMPORTANT (ce mois-ci) :** 8h
**NICE TO HAVE :** 6 jours

**Total prioritaires (URGENT + IMPORTANT) :** 15h → 2 jours de développement

---

## 🎯 Score cible après corrections

| Catégorie          | Actuel | Cible | Gain |
|--------------------|--------|-------|------|
| Performance        | 70/100 | 88/100| +18  |
| SEO                | 90/100 | 98/100| +8   |
| Accessibilité RGAA | 80/100 | 95/100| +15  |
| RGPD               | 95/100 | 100/100| +5  |
| Sécurité           | 95/100 | 98/100| +3   |
| Best Practices     | 85/100 | 92/100| +7   |
| **GLOBAL**         | **82/100** | **95/100** | **+13** |
