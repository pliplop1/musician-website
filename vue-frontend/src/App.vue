<script setup>
import { ref, onMounted } from 'vue'
import HeroSection from './components/HeroSection.vue'
import FeaturedVideosSection from './components/FeaturedVideosSection.vue'
import FeaturedTracksSection from './components/FeaturedTracksSection.vue'
import BiographySection from './components/BiographySection.vue'
import FeaturedPhotosSection from './components/FeaturedPhotosSection.vue'
import ConcertsSection from './components/ConcertsSection.vue'

// Navigation state
const isScrolled = ref(false)
const mobileMenuOpen = ref(false)

// Authentication state
const authState = ref({
  authenticated: false,
  username: null,
  isAdmin: false,
  roles: []
})

// Check authentication status
const checkAuthStatus = async () => {
  try {
    const response = await fetch('/api/public/auth/status', {
      credentials: 'include' // Important pour envoyer les cookies de session
    })
    if (response.ok) {
      const data = await response.json()
      authState.value = data
    }
  } catch (error) {
    console.error('Error checking auth status:', error)
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
})
</script>

<template>
  <div class="app">
    <!-- Navigation Bar -->
    <nav :class="['navbar', { 'scrolled': isScrolled }]">
      <div class="nav-container">
        <div class="nav-logo">DUO BLACK & WHITE</div>

        <!-- Desktop Menu -->
        <ul class="nav-menu desktop-only">
          <li><a @click="scrollToSection('hero')">Accueil</a></li>
          <li><a @click="scrollToSection('videos')">Vidéos</a></li>
          <li><a href="http://localhost:8106/musique">Musique</a></li>
          <li><a @click="scrollToSection('biography')">Le Duo</a></li>
          <li><a @click="scrollToSection('gallery')">Galerie</a></li>
          <li><a @click="scrollToSection('concerts')">Événements</a></li>
          <li><a href="http://localhost:8106/login">Contact</a></li>

          <!-- Auth Buttons -->
          <li v-if="!authState.authenticated">
            <a href="http://localhost:8106/login" class="auth-btn login-btn">Connexion</a>
          </li>
          <li v-if="authState.authenticated">
            <a href="http://localhost:8106/user/profile" class="auth-btn profile-btn">
              <span class="user-icon">👤</span> {{ authState.username }}
            </a>
          </li>
          <li v-if="authState.isAdmin">
            <a href="http://localhost:8106/admin/dashboard" class="auth-btn admin-btn">Admin</a>
          </li>
        </ul>

        <!-- Mobile Menu Button -->
        <button class="mobile-menu-btn" @click="toggleMobileMenu" aria-label="Toggle menu">
          <span :class="['burger', { 'open': mobileMenuOpen }]"></span>
        </button>
      </div>

      <!-- Mobile Menu Overlay -->
      <transition name="slide-fade">
        <div v-if="mobileMenuOpen" class="mobile-menu">
          <ul>
            <li><a @click="scrollToSection('hero')">Accueil</a></li>
            <li><a @click="scrollToSection('videos')">Vidéos</a></li>
            <li><a href="http://localhost:8106/musique">Musique</a></li>
            <li><a @click="scrollToSection('biography')">Le Duo</a></li>
            <li><a @click="scrollToSection('gallery')">Galerie</a></li>
            <li><a @click="scrollToSection('concerts')">Événements</a></li>
            <li><a href="http://localhost:8106/login">Contact</a></li>

            <!-- Auth Buttons Mobile -->
            <li v-if="!authState.authenticated" class="mobile-auth">
              <a href="http://localhost:8106/login">Connexion</a>
            </li>
            <li v-if="authState.authenticated" class="mobile-auth">
              <a href="http://localhost:8106/user/profile">
                <span class="user-icon">👤</span> {{ authState.username }}
              </a>
            </li>
            <li v-if="authState.isAdmin" class="mobile-auth">
              <a href="http://localhost:8106/admin/dashboard">Admin</a>
            </li>
          </ul>
        </div>
      </transition>
    </nav>

    <!-- Sections -->
    <main>
      <HeroSection id="hero" />
      <FeaturedVideosSection id="videos" />
      <FeaturedTracksSection id="music" />
      <BiographySection id="biography" />
      <FeaturedPhotosSection id="gallery" />
      <ConcertsSection id="concerts" />
    </main>

    <!-- Footer -->
    <footer class="footer">
      <div class="footer-content">
        <p>&copy; 2025 Duo Black & White. Tous droits réservés.</p>
        <div class="social-links">
          <a href="https://www.facebook.com/DuoBlackandWhiteMP/" target="_blank">Facebook</a>
          <a href="mailto:dumoulin.marilyne@gmail.com">Email</a>
          <a href="tel:0601234547">Téléphone</a>
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

/* Main Content */
main {
  padding-top: 0;
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
    font-size: 1rem;
    letter-spacing: 1px;
  }

  .mobile-menu a {
    font-size: 1rem;
  }
}

/* Smooth scroll behavior */
html {
  scroll-behavior: smooth;
}

/* Auth Buttons Styling */
.auth-btn {
  padding: 0.5rem 1rem;
  border-radius: 5px;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}

.login-btn {
  background: linear-gradient(135deg, #4a90e2 0%, #357abd 100%);
  color: #fff !important;
}

.login-btn:hover {
  background: linear-gradient(135deg, #357abd 0%, #2868a8 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(74, 144, 226, 0.4);
}

.profile-btn {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: #fff !important;
}

.profile-btn:hover {
  background: rgba(255, 255, 255, 0.15);
  border-color: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
}

.admin-btn {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: #fff !important;
  font-weight: bold;
}

.admin-btn:hover {
  background: linear-gradient(135deg, #d97706 0%, #b45309 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.4);
}

.user-icon {
  font-size: 1rem;
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
</style>
