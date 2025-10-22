/**
 * Configuration de l'API
 * En production, l'API est servie depuis le même serveur que le frontend
 */

// Déterminer l'URL de base selon l'environnement
const getBaseURL = () => {
  // En développement (avec Vite dev server sur 5173), utiliser le proxy
  if (import.meta.env.DEV) {
    return ''  // Le proxy Vite s'occupera de rediriger vers 8106
  }

  // En production, l'API est sur le même serveur
  return window.location.origin
}

export const API_BASE_URL = getBaseURL()
export const API_URL = `${API_BASE_URL}/api`
