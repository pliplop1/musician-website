import { describe, it, expect, beforeEach } from 'vitest'
import { useRotationCache } from './useRotationCache'

describe('useRotationCache', () => {
  beforeEach(() => {
    localStorage.clear()
  })

  it('stores and retrieves cached data within TTL', () => {
    const { setCachedData, getCachedData, getCacheInfo } = useRotationCache('featureX')
    expect(getCachedData()).toBeNull()
    setCachedData([{ id: 1 }])
    const cached = getCachedData()
    expect(Array.isArray(cached)).toBe(true)
    expect(cached[0].id).toBe(1)
    const info = getCacheInfo()
    expect(info.exists).toBe(true)
    expect(info.expired).toBe(false)
  })

  it('clears cache', () => {
    const { setCachedData, clearCache, getCachedData } = useRotationCache('featureY')
    setCachedData([{ id: 2 }])
    clearCache()
    expect(getCachedData()).toBeNull()
  })
})

