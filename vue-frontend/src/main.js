import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import axios from 'axios'

// Configuration globale d'axios pour inclure le token CSRF dans toutes les requêtes
const setupAxiosCsrf = async () => {
  try {
    // Récupérer le token CSRF depuis l'API
    const response = await axios.get('/api/public/csrf')
    const csrfToken = response.data.token
    const csrfHeaderName = response.data.headerName

    // Configurer axios pour inclure automatiquement le token CSRF dans les requêtes POST/PUT/DELETE
    axios.defaults.headers.common[csrfHeaderName] = csrfToken
  } catch (error) {
    // Error handling
  }
}

// Initialiser le token CSRF puis monter l'application
setupAxiosCsrf().then(() => {
  createApp(App).mount('#app')
})
