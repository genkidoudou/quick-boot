import tailwindcss from 'tailwindcss'
import autoprefixer from 'autoprefixer'

/** @type {import('postcss-load-config').Config} */
export default {
  plugins: [
    tailwindcss,
    autoprefixer,
    {
      postcssPlugin: 'internal:charset-removal',
      AtRule: {
        charset(atRule) {
          if (atRule.name === 'charset') {
            atRule.remove()
          }
        }
      }
    }
  ]
}
