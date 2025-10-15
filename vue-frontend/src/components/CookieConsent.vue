<script setup>
import { ref, onMounted } from 'vue'

const STORAGE_KEY = 'cookie-consent-v1'
const visible = ref(false)
const preference = ref(null)

const savePreference = (value) => {
  preference.value = value
  localStorage.setItem(STORAGE_KEY, JSON.stringify({ consent: value, ts: Date.now() }))
  visible.value = false
}

onMounted(() => {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (raw) {
      const parsed = JSON.parse(raw)
      preference.value = parsed?.consent ?? null
    }
  } catch {
    // ignore invalid stored value
  }
  if (preference.value === null) {
    visible.value = true
  }

  // Exposer une fonction globale pour rouvrir les préférences
  if (typeof window !== 'undefined') {
    window.resetCookieConsent = () => {
      visible.value = true
    }
  }
})
</script>

<template>
  <div
    v-if="visible"
    class="cookie-consent"
    role="dialog"
    aria-live="polite"
    aria-modal="true"
    aria-label="Bannière de consentement aux cookies"
  >
    <div class="cookie-content">
      <p>
        Nous utilisons des cookies pour améliorer votre expérience. Vous pouvez accepter
        ou refuser l’utilisation de cookies optionnels.
      </p>
      <div class="cookie-actions">
        <button class="btn accept" @click="savePreference(true)" aria-label="Accepter les cookies">Accepter</button>
        <button class="btn decline" @click="savePreference(false)" aria-label="Refuser les cookies">Refuser</button>
      </div>
      <a class="cookie-link" href="http://localhost:8106/cookies">En savoir plus</a>
    </div>
  </div>
</template>

<style scoped>
.cookie-consent {
  position: fixed;
  bottom: 1rem;
  left: 1rem;
  right: 1rem;
  z-index: 2000;
  background: rgba(0, 0, 0, 0.9);
  color: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.4);
}
.cookie-content {
  padding: 1rem 1.25rem;
  display: flex;
  gap: 0.75rem;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
}
.cookie-actions { display: flex; gap: 0.5rem; }
.btn { cursor: pointer; border: none; padding: 0.6rem 1rem; border-radius: 8px; font-weight: 600; }
.btn.accept { background: #22c55e; color: #04110a; }
.btn.decline { background: #f87171; color: #2b0b0b; }
.cookie-link { color: #93c5fd; text-decoration: underline; }
</style>
