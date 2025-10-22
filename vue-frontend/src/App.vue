<script setup>
import { ref, onMounted, provide } from 'vue'
import HeroSection from './components/HeroSection.vue'
import FeaturedVideosSection from './components/FeaturedVideosSection.vue'
import FeaturedTracksSection from './components/FeaturedTracksSection.vue'
import BiographySection from './components/BiographySection.vue'
import FeaturedPhotosSection from './components/FeaturedPhotosSection.vue'
import ConcertsSection from './components/ConcertsSection.vue'
import CookieConsent from './components/CookieConsent.vue'

// Navigation state
const isScrolled = ref(false)
const mobileMenuOpen = ref(false)
const userDropdownOpen = ref(false)

// Authentication state - partagé avec tous les composants via provide/inject
const authState = ref({
  authenticated: false,
  username: null,
  isAdmin: false,
  roles: []
})

// Fournir l'état d'auth à tous les composants enfants pour éviter les appels API redondants
provide('authState', authState)

// Check authentication status
const checkAuthStatus = async () => {
  try {
    const response = await fetch('/api/user/current', {
      credentials: 'include', // Important pour envoyer les cookies de session
      cache: 'no-store'
    })
    if (response.ok) {
      const data = await response.json()
      authState.value = data
    } else {
      // Si 404 ou autre erreur, l'utilisateur n'est pas connecté (erreur silencieuse)
      authState.value = {
        authenticated: false,
        username: null,
        isAdmin: false,
        roles: []
      }
    }
  } catch (error) {
    // Erreur silencieuse - l'utilisateur n'est simplement pas authentifié
    authState.value = {
      authenticated: false,
      username: null,
      isAdmin: false,
      roles: []
    }
  }
}

// Handle scroll for navbar styling
const handleScroll = () => {
  isScrolled.value = window.scrollY > 50
}

// Toggle mobile menu
const toggleMobileMenu = () => {
  mobileMenuOpen.value = !mobileMenuOpen.value
}

// Toggle user dropdown
const toggleUserDropdown = () => {
  userDropdownOpen.value = !userDropdownOpen.value
}

// Close dropdown when clicking outside
const closeUserDropdown = (event) => {
  const dropdown = document.querySelector('.user-menu-wrapper')
  if (dropdown && !dropdown.contains(event.target)) {
    userDropdownOpen.value = false
  }
}

// Smooth scroll to section
const scrollToSection = (sectionId) => {
  const element = document.getElementById(sectionId)
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' })
    mobileMenuOpen.value = false // Close mobile menu after navigation
  }
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  checkAuthStatus() // Vérifier l'authentification au chargement

  // Add click outside listener for dropdown
  document.addEventListener('click', closeUserDropdown)

  // Handle Escape key to close dropdown
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && userDropdownOpen.value) {
      userDropdownOpen.value = false
    }
  })
})
</script>

<template>
  <div class="app" lang="fr">
    <!-- Skip link pour l'accessibilité -->
    <a href="#main-content" class="skip-link">Aller au contenu principal</a>

    <!-- Navigation Bar -->
    <nav :class="['navbar', { 'scrolled': isScrolled }]" role="navigation" aria-label="Navigation principale">
      <div class="nav-container">
        <div class="nav-logo">DUO BLACK & WHITE</div>

        <!-- Desktop Menu -->
        <ul class="nav-menu desktop-only">
          <li><a href="#hero" @click.prevent="scrollToSection('hero')">Accueil</a></li>
          <li><a href="#videos" @click.prevent="scrollToSection('videos')">Vidéos</a></li>
          <li><a href="/musique">Musique</a></li>
          <li><a href="#biography" @click.prevent="scrollToSection('biography')">Le Duo</a></li>
          <li><a href="#gallery" @click.prevent="scrollToSection('gallery')">Galerie</a></li>
          <li><a href="#concerts" @click.prevent="scrollToSection('concerts')">Événements</a></li>
          <li><a href="/login">Contact</a></li>

        </ul>

        <!-- Auth Section - Menu déroulant utilisateur -->
        <div class="auth-section desktop-only">
          <!-- Bouton Connexion si non authentifié -->
          <a v-if="!authState.authenticated" href="/login" class="auth-btn login-btn" aria-label="Se connecter">
            Connexion
          </a>

          <!-- Menu utilisateur si authentifié -->
          <div v-if="authState.authenticated" class="user-menu-wrapper">
            <button
              class="user-menu-button"
              @click.stop="toggleUserDropdown"
              :aria-expanded="userDropdownOpen"
              aria-haspopup="true"
              aria-label="Menu utilisateur">
              <!-- Avatar -->
              <img v-if="authState.avatarUrl" :src="authState.avatarUrl" :alt="`Avatar de ${authState.username}`" class="user-avatar-img" />
              <span v-else class="user-avatar-initials" aria-hidden="true">
                {{ authState.username ? authState.username.substring(0, 2).toUpperCase() : '?' }}
              </span>
              <span class="username">{{ authState.username }}</span>
              <svg class="dropdown-icon" width="12" height="12" viewBox="0 0 12 12" fill="currentColor" aria-hidden="true">
                <path d="M6 9L1 4h10L6 9z"/>
              </svg>
            </button>

            <!-- Dropdown menu -->
            <div :class="['user-dropdown', { 'active': userDropdownOpen }]" role="menu" aria-labelledby="userMenuButton">
              <a href="/user/profile" class="dropdown-item" role="menuitem">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor" aria-hidden="true">
                  <path d="M8 8a3 3 0 100-6 3 3 0 000 6zm0 1.5c-2.3 0-7 1.15-7 3.5v1.5h14V13c0-2.35-4.7-3.5-7-3.5z"/>
                </svg>
                Mon Profil
              </a>
              <a v-if="authState.isAdmin" href="/admin/dashboard" class="dropdown-item admin-item" role="menuitem">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor" aria-hidden="true">
                  <path d="M13.5 1h-11A1.5 1.5 0 001 2.5v11A1.5 1.5 0 002.5 15h11a1.5 1.5 0 001.5-1.5v-11A1.5 1.5 0 0013.5 1zm-10 12a.5.5 0 01-.5-.5v-9a.5.5 0 01.5-.5h9a.5.5 0 01.5.5v9a.5.5 0 01-.5.5h-9z"/>
                </svg>
                Administration
              </a>
              <div class="dropdown-divider"></div>
              <a href="/logout" class="dropdown-item logout-item" role="menuitem">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor" aria-hidden="true">
                  <path d="M10 12.5a.5.5 0 01-.5.5h-6a.5.5 0 01-.5-.5v-9a.5.5 0 01.5-.5h6a.5.5 0 01.5.5V5a.5.5 0 001 0V3.5A1.5 1.5 0 009.5 2h-6A1.5 1.5 0 002 3.5v9A1.5 1.5 0 003.5 14h6a1.5 1.5 0 001.5-1.5V11a.5.5 0 00-1 0v1.5zm3.854-8.354a.5.5 0 00-.708 0L11 6.293V.5a.5.5 0 00-1 0v5.793L7.854 4.146a.5.5 0 10-.708.708l3 3a.5.5 0 00.708 0l3-3a.5.5 0 000-.708z"/>
                </svg>
                Déconnexion
              </a>
            </div>
          </div>
        </div>

        <!-- Mobile Menu Button -->
        <button class="mobile-menu-btn" @click="toggleMobileMenu" aria-label="Menu" :aria-expanded="mobileMenuOpen">
          <span :class="['burger', { 'open': mobileMenuOpen }]"></span>
        </button>
      </div>

      <!-- Mobile Menu Overlay -->
      <transition name="slide-fade">
        <div v-if="mobileMenuOpen" class="mobile-menu">
          <ul>
            <li><a href="#hero" @click.prevent="scrollToSection('hero')">Accueil</a></li>
            <li><a href="#videos" @click.prevent="scrollToSection('videos')">Vidéos</a></li>
            <li><a href="/musique">Musique</a></li>
            <li><a href="#biography" @click.prevent="scrollToSection('biography')">Le Duo</a></li>
            <li><a href="#gallery" @click.prevent="scrollToSection('gallery')">Galerie</a></li>
            <li><a href="#concerts" @click.prevent="scrollToSection('concerts')">Événements</a></li>
            <li><a href="/login">Contact</a></li>

            <!-- Auth Buttons Mobile -->
            <li v-if="!authState.authenticated" class="mobile-auth">
              <a href="/login">Connexion</a>
            </li>
            <li v-if="authState.authenticated" class="mobile-auth">
              <a href="/user/profile">
                <span class="user-icon">👤</span> {{ authState.username }}
              </a>
            </li>
            <li v-if="authState.isAdmin" class="mobile-auth">
              <a href="/admin/dashboard">Admin</a>
            </li>
            <li v-if="authState.authenticated" class="mobile-auth mobile-logout">
              <a href="/logout">Déconnexion</a>
            </li>
          </ul>
        </div>
      </transition>
    </nav>

    <!-- Sections -->
    <main id="main-content" role="main">
      <CookieConsent />
      <HeroSection id="hero" />
      <FeaturedVideosSection id="videos" />
      <FeaturedTracksSection id="music" />
      <BiographySection id="biography" />
      <FeaturedPhotosSection id="gallery" />
      <ConcertsSection id="concerts" />
    </main>

    <!-- Footer -->
    <footer class="footer" role="contentinfo">
      <div class="footer-content">
        <p>&copy; 2025 Duo Black & White. Tous droits réservés.</p>
        <div class="social-links">
          <a href="https://www.facebook.com/DuoBlackandWhiteMP/" target="_blank" rel="noopener noreferrer" aria-label="Suivez-nous sur Facebook (ouvre dans un nouvel onglet)">Facebook</a>
          <a href="mailto:dumoulin.marilyne@gmail.com" aria-label="Envoyez-nous un email">Email</a>
          <a href="tel:0601234547" aria-label="Appelez-nous au 06 01 23 45 47">Téléphone</a>
        </div>
        <div class="legal-links">
          <a href="/privacy-policy">Politique de confidentialité</a> |
          <a href="/mentions-legales">Mentions légales</a> |
          <a href="/cookies">Cookies</a>
        </div>
      </div>
    </footer>
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-color: #000;
  color: #fff;
  overflow-x: hidden;
}

.legal-links { margin-top: 1.5rem; }
.legal-links a { color: #888; text-decoration: none; margin: 0 0.5rem; }
.legal-links a:focus, .legal-links a:hover { text-decoration: underline; }

.app {
  min-height: 100vh;
}

/* Navigation Bar */
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  padding: 1.5rem 0;
  transition: all 0.3s ease;
  background: transparent;
}

.navbar.scrolled {
  background: rgba(0, 0, 0, 0.95);
  padding: 1rem 0;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.5);
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nav-logo {
  font-size: 1.5rem;
  font-weight: bold;
  letter-spacing: 2px;
  color: #fff;
}

.nav-menu {
  display: flex;
  list-style: none;
  gap: 2rem;
}

.nav-menu a {
  color: #fff;
  text-decoration: none;
  font-size: 0.95rem;
  cursor: pointer;
  transition: color 0.3s ease;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.nav-menu a:hover {
  color: #4a90e2;
}

.nav-menu a:focus {
  outline: 3px solid #4a90e2;
  outline-offset: 4px;
  color: #4a90e2;
}

/* Main Content */
main {
  padding-top: 0;
  min-height: 100vh; /* Prévenir CLS */
}

/* Footer */
.footer {
  background: #111;
  padding: 3rem 2rem;
  text-align: center;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
}

.footer p {
  margin-bottom: 1rem;
  color: #888;
}

.social-links {
  display: flex;
  justify-content: center;
  gap: 2rem;
}

.social-links a {
  color: #fff;
  text-decoration: none;
  transition: color 0.3s ease;
}

.social-links a:hover {
  color: #4a90e2;
}

/* Animations globales */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Mobile Menu Button */
.mobile-menu-btn {
  display: none;
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
  z-index: 1001;
}

.mobile-menu-btn:focus {
  outline: 3px solid #4a90e2;
  outline-offset: 4px;
}

.burger {
  display: block;
  width: 30px;
  height: 3px;
  background: #fff;
  position: relative;
  transition: all 0.3s ease;
}

.burger::before,
.burger::after {
  content: '';
  position: absolute;
  width: 30px;
  height: 3px;
  background: #fff;
  transition: all 0.3s ease;
}

.burger::before {
  top: -10px;
}

.burger::after {
  bottom: -10px;
}

.burger.open {
  background: transparent;
}

.burger.open::before {
  top: 0;
  transform: rotate(45deg);
}

.burger.open::after {
  bottom: 0;
  transform: rotate(-45deg);
}

/* Mobile Menu */
.mobile-menu {
  position: fixed;
  top: 80px;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.98);
  backdrop-filter: blur(10px);
  z-index: 999;
  padding: 2rem 0;
}

.mobile-menu ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.mobile-menu li {
  padding: 1rem 2rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.mobile-menu a {
  color: #fff;
  text-decoration: none;
  font-size: 1.2rem;
  display: block;
  text-transform: uppercase;
  letter-spacing: 2px;
  transition: all 0.3s ease;
}

.mobile-menu a:hover {
  color: #4a90e2;
  transform: translateX(10px);
}

/* Transitions */
.slide-fade-enter-active {
  transition: all 0.3s ease;
}

.slide-fade-leave-active {
  transition: all 0.2s ease;
}

.slide-fade-enter-from {
  transform: translateY(-20px);
  opacity: 0;
}

.slide-fade-leave-to {
  transform: translateY(-10px);
  opacity: 0;
}

/* Responsive */
.desktop-only {
  display: flex;
}

@media (max-width: 768px) {
  .desktop-only {
    display: none !important;
  }

  .mobile-menu-btn {
    display: block;
  }

  .nav-logo {
    font-size: 1.2rem;
  }

  .footer-content {
    padding: 0 1rem;
  }

  .social-links {
    flex-direction: column;
    gap: 1rem;
  }
}

@media (max-width: 480px) {
  .nav-logo {
    font-size: 0.9rem;
    letter-spacing: 1px;
  }

  .mobile-menu a {
    font-size: 0.95rem;
  }

  .navbar {
    padding: 1rem 0;
  }

  .nav-container {
    padding: 0 1rem;
  }
}

/* Smooth scroll behavior */
html {
  scroll-behavior: smooth;
}

/* Skip link (RGAA) */
.skip-link {
  position: absolute;
  top: -40px;
  left: 0;
  background: #000;
  color: #fff;
  padding: 12px 24px;
  text-decoration: none;
  z-index: 10000;
  border-radius: 0 0 8px 0;
  font-weight: bold;
  border: 2px solid #fff;
}

.skip-link:focus {
  top: 0;
}

/* Auth Section Layout */
.auth-section {
  display: flex;
  align-items: center;
  margin-left: 2rem;
}

/* Bouton Connexion */
.login-btn {
  padding: 0.5rem 1.25rem;
  background: linear-gradient(135deg, #4a90e2 0%, #357abd 100%);
  color: #fff !important;
  border-radius: 5px;
  text-decoration: none;
  font-size: 0.9rem;
  transition: all 0.3s ease;
  text-transform: uppercase;
  letter-spacing: 1px;
  white-space: nowrap;
}

.login-btn:hover {
  background: linear-gradient(135deg, #357abd 0%, #2868a8 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(74, 144, 226, 0.4);
}

.login-btn:focus {
  outline: 3px solid #fff;
  outline-offset: 2px;
}

/* Menu utilisateur déroulant */
.user-menu-wrapper {
  position: relative;
}

.user-menu-button {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.5rem 1rem;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(74, 144, 226, 0.3);
  border-radius: 30px;
  color: #fff;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.user-menu-button:hover {
  background: rgba(255, 255, 255, 0.12);
  border-color: rgba(74, 144, 226, 0.5);
}

.user-menu-button:focus {
  outline: 3px solid #fff;
  outline-offset: 2px;
}

.user-avatar-img {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #4a90e2;
}

.user-avatar-initials {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4a90e2 0%, #357abd 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: bold;
  font-size: 0.9rem;
}

.username {
  color: #fff;
  font-weight: 600;
  font-size: 0.95rem;
}

.dropdown-icon {
  transition: transform 0.3s ease;
}

.user-menu-button[aria-expanded="true"] .dropdown-icon {
  transform: rotate(180deg);
}

/* Dropdown menu */
.user-dropdown {
  position: absolute;
  top: calc(100% + 0.5rem);
  right: 0;
  min-width: 200px;
  background: rgba(0, 0, 0, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(74, 144, 226, 0.3);
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.5);
  opacity: 0;
  visibility: hidden;
  transform: translateY(-10px);
  transition: all 0.3s ease;
  z-index: 1002;
}

.user-dropdown.active {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1rem;
  color: #ddd;
  text-decoration: none;
  font-size: 0.95rem;
  transition: all 0.2s ease;
  border: none;
  background: transparent;
  width: 100%;
  text-align: left;
  cursor: pointer;
}

.dropdown-item:first-child {
  border-radius: 8px 8px 0 0;
}

.dropdown-item:last-child {
  border-radius: 0 0 8px 8px;
}

.dropdown-item:hover {
  background: rgba(74, 144, 226, 0.15);
  color: #4a90e2;
}

.dropdown-item:focus {
  outline: 2px solid #4a90e2;
  outline-offset: -2px;
  background: rgba(74, 144, 226, 0.15);
  color: #4a90e2;
}

.dropdown-item.admin-item:hover {
  background: rgba(245, 158, 11, 0.15);
  color: #f59e0b;
}

.dropdown-item.admin-item:focus {
  outline-color: #f59e0b;
  background: rgba(245, 158, 11, 0.15);
  color: #f59e0b;
}

.dropdown-item.logout-item:hover {
  background: rgba(239, 68, 68, 0.15);
  color: #ef4444;
}

.dropdown-item.logout-item:focus {
  outline-color: #ef4444;
  background: rgba(239, 68, 68, 0.15);
  color: #ef4444;
}

.dropdown-divider {
  height: 1px;
  background: rgba(255, 255, 255, 0.1);
  margin: 0.25rem 0;
}

/* Mobile auth buttons */
.mobile-auth {
  border-top: 2px solid rgba(74, 144, 226, 0.3);
  margin-top: 1rem;
  padding-top: 1rem !important;
}

.mobile-auth a {
  background: rgba(74, 144, 226, 0.1);
  padding: 0.75rem 2rem !important;
  border-radius: 5px;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.mobile-auth a:hover {
  background: rgba(74, 144, 226, 0.2);
}

.mobile-logout a {
  background: rgba(239, 68, 68, 0.1) !important;
  color: #f87171 !important;
  border: 1px solid rgba(239, 68, 68, 0.3);
}

.mobile-logout a:hover {
  background: rgba(239, 68, 68, 0.2) !important;
}

/* Responsive adjustments for auth section */
@media (max-width: 1024px) {
  .auth-section {
    margin-left: 1rem;
  }

  .login-btn {
    padding: 0.5rem 1rem;
    font-size: 0.85rem;
  }

  .user-menu-button {
    padding: 0.4rem 0.8rem;
    gap: 0.5rem;
  }

  .username {
    display: none; /* Cache le nom sur tablette */
  }

  .user-avatar-img,
  .user-avatar-initials {
    width: 28px;
    height: 28px;
  }

  .user-dropdown {
    min-width: 180px;
  }
}

@media (max-width: 768px) {
  .auth-section {
    position: absolute;
    top: 1rem;
    right: 4rem;
    margin-left: 0;
  }
}
</style>
