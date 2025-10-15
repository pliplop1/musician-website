import { test, expect } from '@playwright/test'

test.beforeEach(async ({ page }) => {
  await page.route('**/api/public/homepage-settings', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({ heroTitle: 'DUO BW', heroSubtitle: 'Tagline' })
    })
  })
})

test('profile link appears when authenticated', async ({ page, baseURL }) => {
  await page.route('**/api/user/current', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({ authenticated: true, username: 'TestUser', isAdmin: false, roles: [] })
    })
  })
  await page.goto(baseURL!)
  await expect(page.getByRole('link', { name: /TestUser/i })).toBeVisible()
  const profileLink = page.getByRole('link', { name: /TestUser/i })
  await expect(profileLink).toHaveAttribute('href', /\/user\/profile/)
})

