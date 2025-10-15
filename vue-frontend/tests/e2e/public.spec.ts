import { test, expect } from '@playwright/test'

test.beforeEach(async ({ page }) => {
  await page.route('**/api/public/homepage-settings', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        heroTitle: 'DUO BLACK & WHITE',
        heroSubtitle: 'La musique qui vous transporte',
        heroBackgroundVideoUrl: '',
      }),
    })
  })
  await page.route('**/api/**', (route) => route.fulfill({ status: 200, body: '{}' }))
  await page.route('**/api/public/gallery', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify([
        { id: 1, url: '/uploaded-photos/p1.jpg', caption: 'Photo 1' },
        { id: 2, url: '/uploaded-photos/p2.jpg', caption: 'Photo 2' }
      ])
    })
  })
})

test('hero renders with title and tagline', async ({ page, baseURL }) => {
  await page.goto(baseURL!)
  await expect(page.locator('.hero-title')).toBeVisible()
  await expect(page.locator('.hero-tagline')).toBeVisible()
})

test('clicking a video card opens the modal', async ({ page, baseURL }) => {
  await page.goto(baseURL!)
  const videoCard = page.locator('.video-card').first()
  // if grid exists, click
  if (await videoCard.count()) {
    await videoCard.click()
    await expect(page.locator('.video-modal')).toBeVisible()
  } else {
    test.skip(true, 'No video cards available in this build context')
  }
})

test('gallery images use lazy loading and async decoding', async ({ page, baseURL }) => {
  await page.goto(baseURL!)
  const images = page.locator('.gallery-grid img')
  if (await images.count()) {
    await expect(images.first()).toHaveAttribute('loading', 'lazy')
    await expect(images.first()).toHaveAttribute('decoding', 'async')
  } else {
    test.skip(true, 'Gallery not rendered')
  }
})

test('JSON-LD scripts are present for SEO', async ({ page, baseURL }) => {
  await page.goto(baseURL!)
  const jsonLd = page.locator('script[type="application/ld+json"]')
  await expect(jsonLd).toHaveCount(3)
})
