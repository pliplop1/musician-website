import { test, expect } from '@playwright/test'

test.beforeEach(async ({ page }) => {
  // Mock API endpoints used by the app to avoid backend dependency
  await page.route('**/api/public/homepage-settings', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        heroTitle: 'DUO BLACK & WHITE',
        heroSubtitle: 'La musique qui vous transporte',
        heroBackgroundVideoUrl: '',
        welcomeMessage: 'Bienvenue sur notre site officiel',
      }),
    })
  })
  await page.route('**/api/**', (route) => route.fulfill({ status: 200, body: '{}' }))
})

test('home page loads with correct title and meta description', async ({ page, baseURL }) => {
  await page.goto(baseURL!)
  await expect(page).toHaveTitle(/Duo Black & White/i)
  const description = page.locator('meta[name="description"]')
  await expect(description).toHaveAttribute('content', /Site officiel du Duo Black & White/i)
})

test('skip link exists and points to main content', async ({ page, baseURL }) => {
  await page.goto(baseURL!)
  const skip = page.getByRole('link', { name: /Aller au contenu principal/i })
  await expect(skip).toBeVisible()
  await expect(skip).toHaveAttribute('href', '#main-content')
})

test('cookie consent can be accepted or declined', async ({ page, baseURL }) => {
  await page.goto(baseURL!)
  const dialog = page.getByRole('dialog', { name: /Bannière de consentement/i })
  await expect(dialog).toBeVisible()
  await page.getByRole('button', { name: /Accepter les cookies/i }).click()
  await expect(dialog).toBeHidden()
})

test('key sections render (hero and biography at least)', async ({ page, baseURL }) => {
  await page.goto(baseURL!)
  await expect(page.locator('.hero-title')).toBeVisible()
  await expect(page.locator('#biography')).toBeVisible()
})
