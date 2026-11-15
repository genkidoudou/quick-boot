<template>
  <canvas
    v-show="active"
    ref="canvasRef"
    class="qb-glass-optical"
    aria-hidden="true"
  />
</template>

<script setup>
/**
 * 轻量玻璃光学层：高画质时用 Canvas 2D 做柔光折射噪点，不做完整 Three.js 移植。
 */
import useSettingsStore from '@/store/modules/settings'

const settingsStore = useSettingsStore()
const canvasRef = ref(null)
let raf = 0
let running = false

const active = computed(() => {
  return !!settingsStore.glassEnabled && settingsStore.glassQuality === 'high'
})

function resize() {
  const canvas = canvasRef.value
  if (!canvas) return
  const dpr = Math.min(window.devicePixelRatio || 1, 2)
  canvas.width = Math.floor(window.innerWidth * dpr)
  canvas.height = Math.floor(window.innerHeight * dpr)
  canvas.style.width = '100%'
  canvas.style.height = '100%'
}

function paint(ts) {
  const canvas = canvasRef.value
  if (!canvas || !running) return
  const ctx = canvas.getContext('2d')
  if (!ctx) return
  const { width, height } = canvas
  ctx.clearRect(0, 0, width, height)
  const t = ts * 0.00015
  const g = ctx.createRadialGradient(
    width * (0.35 + Math.sin(t) * 0.08),
    height * (0.2 + Math.cos(t * 0.8) * 0.06),
    0,
    width * 0.5,
    height * 0.45,
    Math.max(width, height) * 0.7
  )
  g.addColorStop(0, 'rgba(141, 81, 249, 0.14)')
  g.addColorStop(0.45, 'rgba(0, 122, 255, 0.06)')
  g.addColorStop(1, 'rgba(11, 19, 34, 0)')
  ctx.fillStyle = g
  ctx.fillRect(0, 0, width, height)

  const g2 = ctx.createRadialGradient(
    width * (0.75 + Math.cos(t * 1.1) * 0.05),
    height * (0.7 + Math.sin(t) * 0.05),
    0,
    width * 0.7,
    height * 0.75,
    Math.max(width, height) * 0.55
  )
  g2.addColorStop(0, 'rgba(105, 197, 154, 0.08)')
  g2.addColorStop(1, 'rgba(11, 19, 34, 0)')
  ctx.fillStyle = g2
  ctx.fillRect(0, 0, width, height)

  raf = requestAnimationFrame(paint)
}

function start() {
  if (running) return
  running = true
  resize()
  raf = requestAnimationFrame(paint)
  window.addEventListener('resize', resize)
}

function stop() {
  running = false
  cancelAnimationFrame(raf)
  window.removeEventListener('resize', resize)
  const canvas = canvasRef.value
  if (canvas) {
    const ctx = canvas.getContext('2d')
    ctx?.clearRect(0, 0, canvas.width, canvas.height)
  }
}

watch(active, (on) => {
  if (on) start()
  else stop()
}, { immediate: true })

onBeforeUnmount(() => stop())
</script>

<style scoped>
.qb-glass-optical {
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  mix-blend-mode: screen;
  opacity: 0.85;
}
</style>
