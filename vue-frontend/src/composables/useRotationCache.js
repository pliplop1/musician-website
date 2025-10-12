/**
 * Composable pour gérer le cache de rotation automatique avec TTL de 24h
 *
 * Permet de stocker des données dans localStorage avec une expiration de 24 heures.
 * Utilisé pour la rotation automatique des contenus featured (vidéos, musiques, galerie).
 *
 * @example
 * const { getCachedData, setCachedData, clearCache } = useRotationCache('myFeature')
 *
 * // Vérifier si des données en cache existent et sont valides
 * const cached = getCachedData()
 * if (cached) {
 *   console.log('Données en cache valides', cached)
 * }
 *
 * // Stocker de nouvelles données avec timestamp
 * setCachedData([{ id: 1, title: 'Item 1' }])
 */
export function useRotationCache(cacheKey) {
  const TWENTY_FOUR_HOURS = 24 * 60 * 60 * 1000 // 24h en millisecondes
  const DATA_KEY = `${cacheKey}_data`
  const TIMESTAMP_KEY = `${cacheKey}_timestamp`

  /**
   * Récupère les données en cache si elles sont encore valides (< 24h)
   * @returns {Array|null} Les données en cache ou null si expirées/inexistantes
   */
  const getCachedData = () => {
    try {
      const cachedData = localStorage.getItem(DATA_KEY)
      const cachedTimestamp = localStorage.getItem(TIMESTAMP_KEY)

      if (!cachedData || !cachedTimestamp) {
        console.log(`📭 Aucun cache trouvé pour ${cacheKey}`)
        return null
      }

      const timeDiff = Date.now() - parseInt(cachedTimestamp)

      if (timeDiff >= TWENTY_FOUR_HOURS) {
        console.log(`⏰ Cache expiré pour ${cacheKey} (${Math.round(timeDiff / 1000 / 60 / 60)}h)`)
        clearCache()
        return null
      }

      const remainingHours = Math.round((TWENTY_FOUR_HOURS - timeDiff) / 1000 / 60 / 60)
      console.log(`✅ Cache valide pour ${cacheKey} (expire dans ${remainingHours}h)`)

      return JSON.parse(cachedData)
    } catch (error) {
      console.error(`❌ Erreur lecture cache ${cacheKey}:`, error)
      clearCache()
      return null
    }
  }

  /**
   * Stocke des données dans le cache avec un timestamp actuel
   * @param {Array} data - Les données à mettre en cache
   */
  const setCachedData = (data) => {
    try {
      localStorage.setItem(DATA_KEY, JSON.stringify(data))
      localStorage.setItem(TIMESTAMP_KEY, Date.now().toString())
      console.log(`💾 Cache sauvegardé pour ${cacheKey} (expire dans 24h)`)
    } catch (error) {
      console.error(`❌ Erreur sauvegarde cache ${cacheKey}:`, error)
    }
  }

  /**
   * Supprime le cache (données + timestamp)
   */
  const clearCache = () => {
    localStorage.removeItem(DATA_KEY)
    localStorage.removeItem(TIMESTAMP_KEY)
    console.log(`🗑️ Cache supprimé pour ${cacheKey}`)
  }

  /**
   * Obtient des informations sur l'état du cache
   * @returns {Object} État du cache (existe, expiré, temps restant)
   */
  const getCacheInfo = () => {
    const cachedTimestamp = localStorage.getItem(TIMESTAMP_KEY)

    if (!cachedTimestamp) {
      return { exists: false, expired: true, remainingHours: 0 }
    }

    const timeDiff = Date.now() - parseInt(cachedTimestamp)
    const expired = timeDiff >= TWENTY_FOUR_HOURS
    const remainingHours = Math.max(0, Math.round((TWENTY_FOUR_HOURS - timeDiff) / 1000 / 60 / 60))

    return {
      exists: true,
      expired,
      remainingHours
    }
  }

  return {
    getCachedData,
    setCachedData,
    clearCache,
    getCacheInfo
  }
}
