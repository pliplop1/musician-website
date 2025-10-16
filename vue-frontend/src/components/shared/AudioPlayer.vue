<script setup>
import { onMounted, ref } from 'vue'

const props = defineProps({
  src: { type: String, required: true },
  preload: { type: String, default: 'metadata' },
  autoplay: { type: Boolean, default: false }
})

const audioRef = ref(null)
const playing = ref(false)
const muted = ref(false)
const current = ref(0)
const duration = ref(0)
const seek = ref(0)
const volume = ref(1)
const rate = ref(1)

const fmt = (sec) => {
  if (!isFinite(sec)) return '0:00'
  const m = Math.floor(sec / 60)
  const s = Math.floor(sec % 60)
  return `${m}:${s.toString().padStart(2, '0')}`
}

const togglePlay = () => {
  const a = audioRef.value
  if (!a) return
  if (a.paused) {
    a.play()
  } else {
    a.pause()
  }
}

const toggleMute = () => {
  const a = audioRef.value
  if (!a) return
  a.muted = !a.muted
  muted.value = a.muted
}

const onSeekInput = (e) => {
  const a = audioRef.value
  if (!a || !isFinite(a.duration)) return
  const val = parseFloat(e.target.value)
  a.currentTime = (val / 100) * a.duration
}

const onVolInput = (e) => {
  const a = audioRef.value
  if (!a) return
  const val = parseFloat(e.target.value)
  a.volume = val
  volume.value = val
  if (val === 0 && !a.muted) {
    a.muted = true
    muted.value = true
  } else if (val > 0 && a.muted) {
    a.muted = false
    muted.value = false
  }
}

const onRateChange = (e) => {
  const a = audioRef.value
  if (!a) return
  const val = parseFloat(e.target.value)
  if (isFinite(val) && val > 0) {
    rate.value = val
    a.playbackRate = val
  }
}

onMounted(() => {
  const a = audioRef.value
  if (!a) return
  a.preload = props.preload
  a.autoplay = props.autoplay
  a.playbackRate = rate.value
  a.addEventListener('loadedmetadata', () => {
    duration.value = a.duration || 0
    current.value = 0
    seek.value = 0
  })
  a.addEventListener('play', () => (playing.value = true))
  a.addEventListener('pause', () => (playing.value = false))
  a.addEventListener('timeupdate', () => {
    current.value = a.currentTime
    if (isFinite(a.duration) && a.duration > 0) {
      seek.value = (a.currentTime / a.duration) * 100
    }
  })
  // Ne pas auto-lancer; l'utilisateur déclenche via le bouton pour éviter les blocages navigateur
})
</script>

<template>
  <div class="ap" role="group" aria-label="Lecteur audio">
    <audio ref="audioRef">
      <source :src="src" type="audio/mpeg" />
      Votre navigateur ne supporte pas la lecture audio.
    </audio>
    <button class="ap-btn" @click="togglePlay" :aria-label="playing ? 'Pause' : 'Lire'" :title="playing ? 'Pause' : 'Lire'">
      <i :class="playing ? 'fas fa-pause' : 'fas fa-play'"></i>
      <span class="ap-label">{{ playing ? 'Pause' : 'Lecture' }}</span>
    </button>
    <div class="ap-time" aria-live="polite">{{ fmt(current) }} / {{ fmt(duration) }}</div>
    <input class="ap-seek" type="range" min="0" max="100" step="0.1" :value="seek" @input="onSeekInput" aria-label="Position dans le morceau" />
    <button class="ap-btn" @click="toggleMute" :aria-label="muted ? 'Activer le son' : 'Couper le son'" :title="muted ? 'Activer le son' : 'Couper le son'">
      <i :class="muted ? 'fas fa-volume-mute' : 'fas fa-volume-up'"></i>
      <span class="ap-label">{{ muted ? 'Muet' : 'Son' }}</span>
    </button>
    <input class="ap-volume" type="range" min="0" max="1" step="0.01" :value="volume" @input="onVolInput" aria-label="Volume" />
    <label class="sr-only" for="apRate">Vitesse de lecture</label>
    <select id="apRate" class="ap-rate" :value="rate" @change="onRateChange" aria-label="Vitesse de lecture" title="Vitesse de lecture">
      <option value="0.75">x0.75</option>
      <option value="1">x1</option>
      <option value="1.25">x1.25</option>
      <option value="1.5">x1.5</option>
      <option value="2">x2</option>
    </select>
  </div>
</template>

<style scoped>
.ap { display: grid; grid-template-columns: auto auto 1fr auto auto auto; gap: .75rem; align-items: center; background: rgba(255,255,255,.05); border: 1px solid rgba(255,255,255,.1); border-radius: 10px; padding: .75rem 1rem; }
.ap-btn { background: rgba(34,197,94,.15); border: 1px solid rgba(34,197,94,.35); color: #4ade80; padding: .5rem .75rem; border-radius: 8px; cursor: pointer; }
.ap-btn:hover { filter: brightness(1.1); }
.ap-btn .ap-label { margin-left: .4rem; }
.ap-time { color: #9ca3af; font-variant-numeric: tabular-nums; }
.ap-seek { width: 100%; accent-color: #22c55e; }
.ap-volume { width: 100px; accent-color: #22c55e; }
/* Vitesse de lecture */
.ap-rate { width: 90px; background: rgba(255,255,255,0.06); border: 1px solid rgba(255,255,255,0.18); color: #e5e7eb; border-radius: 8px; padding: .45rem .5rem; }
.ap-rate:focus { outline: none; border-color: #22c55e; box-shadow: 0 0 0 2px rgba(34,197,94,0.25); }
audio { display: none; }
/* Utilitaire accessibilité (label masqué visuellement) */
.sr-only { position: absolute; width: 1px; height: 1px; padding: 0; margin: -1px; overflow: hidden; clip: rect(0, 0, 0, 0); white-space: nowrap; border: 0; }
</style>
