import { ref } from 'vue'
import { useRotationCache } from './useRotationCache'

/**
 * Composable pour gérer le contenu featured avec rotation automatique ou sélection manuelle
 *
 * Ce composable gère deux modes :
 * 1. Rotation automatique (autoRotationEnabled = true) :
 *    - Sélectionne 3 items aléatoires parmi tous les items disponibles
 *    - Cache la sélection pendant 24h dans localStorage
 *    - Renouvelle automatiquement après expiration du cache
 *
 * 2. Sélection manuelle (autoRotationEnabled = false) :
 *    - Utilise les items sélectionnés manuellement par l'admin
 *    - Pas de cache, toujours à jour avec la base de données
 *
 * @param {Object} config - Configuration du composable
 * @param {string} config.contentType - Type de contenu ('videos', 'tracks', 'gallery')
 * @param {string} config.allItemsEndpoint - URL de l'API pour récupérer tous les items
 * @param {string} config.featuredEndpoint - URL de l'API pour récupérer les items featured manuels
 * @param {string} config.cacheKey - Clé unique pour le cache localStorage
 * @param {string} config.autoRotationFieldName - Nom du champ dans settings API (autoRotationEnabledVideos, autoRotationEnabledTracks, autoRotationEnabledGallery)
 *
 * @returns {Object} État et fonctions pour gérer le contenu featured
 *
 * @example
 * // Dans un composant Vue
 * const { items, loading, error, loadFeaturedContent } = useFeaturedContent({
 *   contentType: 'videos',
 *   allItemsEndpoint: '/api/public/videos',
 *   featuredEndpoint: '/api/public/featured/videos',
 *   cacheKey: 'featuredVideos',
 *   autoRotationFieldName: 'autoRotationEnabledVideos'
 * })
 *
 * onMounted(() => loadFeaturedContent())
 */
export function useFeaturedContent({
  contentType,
  allItemsEndpoint,
  featuredEndpoint,
  cacheKey,
  autoRotationFieldName
}) {
  const items = ref([])
  const loading = ref(true)
  const error = ref(null)
  const isAutoRotationMode = ref(false)

  const { getCachedData, setCachedData } = useRotationCache(cacheKey)

  /**
   * Sélectionne aléatoirement N items d'un tableau
   * @param {Array} array - Tableau d'items
   * @param {number} count - Nombre d'items à sélectionner (par défaut 3)
   * @returns {Array} Tableau de N items aléatoires
   */
  const selectRandomItems = (array, count = 3) => {
    if (!array || array.length === 0) return []

    // Si moins d'items que demandé, retourne tout le tableau
    if (array.length <= count) return array

    // Mélange le tableau et prend les N premiers éléments
    const shuffled = [...array].sort(() => 0.5 - Math.random())
    return shuffled.slice(0, count)
  }

  /**
   * Récupère les paramètres de la page d'accueil pour savoir si rotation auto est activée
   * @returns {Promise<boolean>} true si rotation auto activée, false sinon
   */
  const checkAutoRotationEnabled = async () => {
    try {
      const response = await fetch('/api/public/homepage-settings')
      if (!response.ok) {
        throw new Error(`Erreur HTTP ${response.status}`)
      }
      const settings = await response.json()
      return settings[autoRotationFieldName] || false
    } catch (err) {
      console.error('❌ Erreur récupération settings:', err)
      return false // Par défaut, désactivé en cas d'erreur
    }
  }

  /**
   * Mode rotation automatique : sélection aléatoire avec cache 24h
   * @returns {Promise<Array>} Items sélectionnés aléatoirement
   */
  const loadAutoRotationContent = async () => {
    console.log(`🎲 Mode rotation automatique activé pour ${contentType}`)

    // 1. Vérifier si un cache valide existe
    const cached = getCachedData()
    if (cached && cached.length > 0) {
      console.log(`✅ Utilisation du cache pour ${contentType} (${cached.length} items)`)
      return cached
    }

    // 2. Pas de cache valide : récupérer tous les items et sélectionner aléatoirement
    console.log(`🔄 Génération d'une nouvelle sélection aléatoire pour ${contentType}`)

    const response = await fetch(allItemsEndpoint)
    if (!response.ok) {
      throw new Error(`Erreur HTTP ${response.status}`)
    }

    const allItems = await response.json()
    console.log(`📦 ${allItems.length} ${contentType} disponibles`)

    // 3. Sélectionner 3 items aléatoires
    const selectedItems = selectRandomItems(allItems, 3)
    console.log(`✨ ${selectedItems.length} ${contentType} sélectionnés aléatoirement`)

    // 4. Mettre en cache pour 24h
    setCachedData(selectedItems)

    return selectedItems
  }

  /**
   * Mode sélection manuelle : utilise les items featured choisis par l'admin
   * @returns {Promise<Array>} Items featured manuellement
   */
  const loadManualSelectionContent = async () => {
    console.log(`📌 Mode sélection manuelle activé pour ${contentType}`)

    const response = await fetch(featuredEndpoint)
    if (!response.ok) {
      throw new Error(`Erreur HTTP ${response.status}`)
    }

    const featuredItems = await response.json()
    console.log(`✅ ${featuredItems.length} ${contentType} featured chargés`)

    return featuredItems
  }

  /**
   * Charge le contenu featured selon le mode configuré (auto ou manuel)
   * Cette fonction est la fonction principale à appeler depuis le composant
   */
  const loadFeaturedContent = async () => {
    loading.value = true
    error.value = null

    try {
      // 1. Vérifier si rotation auto est activée
      const autoRotationEnabled = await checkAutoRotationEnabled()
      isAutoRotationMode.value = autoRotationEnabled

      // 2. Charger le contenu selon le mode
      if (autoRotationEnabled) {
        items.value = await loadAutoRotationContent()
      } else {
        items.value = await loadManualSelectionContent()
      }

      console.log(`✅ ${items.value.length} ${contentType} featured chargés avec succès`)
    } catch (err) {
      error.value = `Erreur lors du chargement des ${contentType}: ${err.message}`
      console.error(`❌ ${error.value}`, err)
      items.value = []
    } finally {
      loading.value = false
    }
  }

  return {
    items,
    loading,
    error,
    isAutoRotationMode,
    loadFeaturedContent,
    selectRandomItems // Exposé pour tests unitaires si besoin
  }
}
