import { ref } from 'vue'

/**
 * Composable pour gérer le chargement et le cache des médias featured (tracks/videos)
 *
 * @param {string} mediaType - Type de média ('tracks' ou 'videos')
 * @param {string} apiEndpoint - URL de l'API (ex: '/api/public/tracks')
 * @param {number} limit - Nombre d'items à sélectionner aléatoirement (défaut: 3)
 * @returns {object} - { items, loading, error, fetchItems }
 */
export function useFeaturedMedia(mediaType, apiEndpoint, limit = 3) {
  const items = ref([])
  const loading = ref(true)
  const error = ref(null)

  const CACHE_KEY = `featured${mediaType}_v2`
  const CACHE_TIMESTAMP_KEY = `featured${mediaType}Timestamp_v2`
  const TWENTY_FOUR_HOURS = 24 * 60 * 60 * 1000

  const fetchItems = async () => {
    try {
      loading.value = true
      error.value = null

      // Vérifier le cache localStorage
      const cachedItems = localStorage.getItem(CACHE_KEY)
      const cachedTimestamp = localStorage.getItem(CACHE_TIMESTAMP_KEY)

      if (cachedItems && cachedTimestamp) {
        const now = Date.now()
        const timeDiff = now - parseInt(cachedTimestamp)

        // Si moins de 24h se sont écoulées, utiliser le cache
        if (timeDiff < TWENTY_FOUR_HOURS) {
          items.value = JSON.parse(cachedItems)
          loading.value = false
          return
        }
      }

      // Récupérer de nouveaux items depuis l'API
      const response = await fetch(apiEndpoint)

      if (!response.ok) {
        throw new Error(`Erreur lors du chargement des ${mediaType}`)
      }

      const allItems = await response.json()

      // Sélectionner N items aléatoires
      const shuffled = [...allItems].sort(() => 0.5 - Math.random())
      const selectedItems = shuffled.slice(0, limit)

      // Stocker dans le cache avec le timestamp actuel
      localStorage.setItem(CACHE_KEY, JSON.stringify(selectedItems))
      localStorage.setItem(CACHE_TIMESTAMP_KEY, Date.now().toString())

      items.value = selectedItems
    } catch (err) {
      error.value = err.message
    } finally {
      loading.value = false
    }
  }

  return {
    items,
    loading,
    error,
    fetchItems
  }
}
