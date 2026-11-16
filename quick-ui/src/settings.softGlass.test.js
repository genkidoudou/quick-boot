import { describe, expect, it } from 'vitest'
import defaultSettings from '@/settings'

describe('Soft Glass defaults', () => {
  it('enables glass material by default', () => {
    expect(defaultSettings.glassEnabled).toBe(true)
  })

  it('keeps light side theme for Soft Glass shell', () => {
    expect(defaultSettings.sideTheme).toBe('theme-light')
  })
})
