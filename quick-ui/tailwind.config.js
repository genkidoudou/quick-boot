/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        background: 'var(--qb-bg)',
        foreground: 'var(--qb-fg)',
        surface: 'var(--qb-surface)',
        card: {
          DEFAULT: 'var(--qb-card)',
          foreground: 'var(--qb-fg)'
        },
        muted: {
          DEFAULT: 'var(--qb-muted)',
          foreground: 'var(--qb-muted-fg)'
        },
        faint: 'var(--qb-faint)',
        popover: {
          DEFAULT: 'var(--qb-popover)',
          foreground: 'var(--qb-fg)'
        },
        border: 'var(--qb-border)',
        'border-subtle': 'var(--qb-border-subtle)',
        input: 'var(--qb-input)',
        ring: 'var(--qb-ring)',
        'on-media': 'var(--qb-on-media)',
        'on-media-muted': 'var(--qb-on-media-muted)'
      },
      borderRadius: {
        'qb-surface': 'var(--qb-radius)',
        'qb-field': 'var(--qb-field-radius)',
        'qb-control': 'var(--qb-control-radius)',
        'qb-overlay': 'var(--qb-overlay-radius)'
      }
    }
  },
  future: {
    hoverOnlyWhenSupported: true
  },
  corePlugins: {
    preflight: false
  },
  plugins: []
}
