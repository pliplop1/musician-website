<script setup>
import { ref, onMounted } from 'vue'

const formData = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const loading = ref(false)
const error = ref(null)
const success = ref(false)
const registrationEnabled = ref(true)
const registrationMessage = ref('')

// Charger les paramètres d'inscription
const loadRegistrationSettings = async () => {
  try {
    const response = await fetch('/api/public/homepage-settings')
    if (response.ok) {
      const settings = await response.json()
      registrationEnabled.value = settings.registrationEnabled
      registrationMessage.value = settings.registrationMessage || 'Rejoignez notre communauté !'
    }
  } catch (err) {
    // Error handling
  }
}

const handleSubmit = async () => {
  error.value = null

  // Validation côté client
  if (formData.value.password !== formData.value.confirmPassword) {
    error.value = "Les mots de passe ne correspondent pas"
    return
  }

  if (formData.value.password.length < 8) {
    error.value = "Le mot de passe doit contenir au moins 8 caractères"
    return
  }

  loading.value = true

  try {
    const response = await fetch('/api/public/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        username: formData.value.username,
        email: formData.value.email,
        password: formData.value.password
      })
    })

    const data = await response.json()

    if (!response.ok) {
      throw new Error(data.message || 'Erreur lors de l\'inscription')
    }

    success.value = true

    // Rediriger vers /login après 2 secondes
    setTimeout(() => {
      window.location.href = 'http://localhost:8106/login'
    }, 2000)

  } catch (err) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadRegistrationSettings()
})
</script>

<template>
  <section v-if="registrationEnabled" class="register-section" id="register">
    <div class="container">
      <h2>Créer un Compte</h2>
      <p class="subtitle">{{ registrationMessage }}</p>

      <div v-if="success" class="alert alert-success">
        <i class="fas fa-check-circle"></i>
        Inscription réussie ! Redirection vers la page de connexion...
      </div>

      <div v-else class="register-form">
        <div v-if="error" class="alert alert-error">
          <i class="fas fa-exclamation-triangle"></i>
          {{ error }}
        </div>

        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label for="username">
              <i class="fas fa-user"></i> Nom d'utilisateur *
            </label>
            <input
              type="text"
              id="username"
              v-model="formData.username"
              required
              minlength="3"
              maxlength="50"
              class="form-control"
              placeholder="Minimum 3 caractères">
          </div>

          <div class="form-group">
            <label for="email">
              <i class="fas fa-envelope"></i> Email *
            </label>
            <input
              type="email"
              id="email"
              v-model="formData.email"
              required
              class="form-control"
              placeholder="votre@email.com">
          </div>

          <div class="form-group">
            <label for="password">
              <i class="fas fa-lock"></i> Mot de passe *
            </label>
            <input
              type="password"
              id="password"
              v-model="formData.password"
              required
              minlength="8"
              class="form-control"
              placeholder="Minimum 8 caractères">
            <small class="hint">Au moins 8 caractères</small>
          </div>

          <div class="form-group">
            <label for="confirmPassword">
              <i class="fas fa-lock"></i> Confirmer le mot de passe *
            </label>
            <input
              type="password"
              id="confirmPassword"
              v-model="formData.confirmPassword"
              required
              class="form-control"
              placeholder="Retapez votre mot de passe">
          </div>

          <button
            type="submit"
            :disabled="loading"
            class="btn btn-primary">
            <span v-if="loading">
              <i class="fas fa-spinner fa-spin"></i> Inscription en cours...
            </span>
            <span v-else>
              <i class="fas fa-user-plus"></i> S'inscrire
            </span>
          </button>

          <p class="login-link">
            Déjà un compte ?
            <a href="http://localhost:8106/login">Se connecter</a>
          </p>
        </form>
      </div>
    </div>
  </section>
</template>

<style scoped>
.register-section {
  padding: 5rem 2rem;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  min-height: 100vh;
  display: flex;
  align-items: center;
}

.container {
  max-width: 500px;
  margin: 0 auto;
  width: 100%;
}

h2 {
  text-align: center;
  margin-bottom: 1rem;
  font-size: 2.5rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  text-align: center;
  color: #ccc;
  margin-bottom: 2rem;
  font-size: 1.1rem;
}

.register-form {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  padding: 2.5rem;
  border-radius: 15px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.form-group {
  margin-bottom: 1.5rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  color: #e0e0e0;
  font-weight: 500;
  font-size: 0.95rem;
}

label i {
  margin-right: 0.5rem;
  color: #667eea;
}

.form-control {
  width: 100%;
  padding: 0.875rem 1rem;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.08);
  color: #fff;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.form-control:focus {
  outline: none;
  border-color: #667eea;
  background: rgba(255, 255, 255, 0.12);
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-control::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.hint {
  display: block;
  margin-top: 0.5rem;
  color: rgba(255, 255, 255, 0.5);
  font-size: 0.85rem;
}

.btn {
  width: 100%;
  padding: 1rem;
  border: none;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 1rem;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.alert {
  padding: 1rem 1.25rem;
  border-radius: 8px;
  margin-bottom: 1.5rem;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 0.95rem;
}

.alert i {
  font-size: 1.25rem;
}

.alert-success {
  background: rgba(34, 197, 94, 0.15);
  border: 1px solid rgba(34, 197, 94, 0.3);
  color: #4ade80;
}

.alert-error {
  background: rgba(239, 68, 68, 0.15);
  border: 1px solid rgba(239, 68, 68, 0.3);
  color: #f87171;
}

.login-link {
  text-align: center;
  margin-top: 1.5rem;
  color: #ccc;
  font-size: 0.95rem;
}

.login-link a {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s ease;
}

.login-link a:hover {
  color: #764ba2;
  text-decoration: underline;
}

/* Animations */
.register-form {
  animation: fadeInUp 0.6s ease-out;
}

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

/* Responsive */
@media (max-width: 768px) {
  .register-section {
    padding: 3rem 1rem;
  }

  h2 {
    font-size: 2rem;
  }

  .register-form {
    padding: 1.5rem;
  }

  .form-control {
    padding: 0.75rem;
  }
}
</style>
