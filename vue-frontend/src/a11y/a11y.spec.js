import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import axe from 'axe-core'
import HeroSection from '../components/HeroSection.vue'

async function runAxeOnDocument() {
  return await new Promise((resolve) => {
    axe.run(document, { rules: { 'color-contrast': { enabled: false }, 'document-title': { enabled: false }, 'html-has-lang': { enabled: false } } }, (err, results) => {
      if (err) throw err
      resolve(results)
    })
  })
}

describe('Accessibility (axe-core)', () => {
  it('HeroSection has no critical accessibility violations', async () => {
    mount(HeroSection)
    await new Promise((r) => setTimeout(r, 0))
    // Provide minimal document metadata expected by axe
    document.title = 'Test Document'
    document.documentElement.setAttribute('lang', 'fr')
    const results = await runAxeOnDocument()
    const serious = results.violations.filter((v) => v.impact === 'serious' || v.impact === 'critical')
    expect(serious).toEqual([])
  })
})
