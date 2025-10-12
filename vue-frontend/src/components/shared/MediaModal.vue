<script setup>
defineProps({
  show: {
    type: Boolean,
    required: true
  },
  item: {
    type: Object,
    default: null
  },
  mediaType: {
    type: String,
    required: true,
    validator: (value) => ['track', 'video'].includes(value)
  },
  accentColor: {
    type: String,
    default: '#22c55e'
  }
})

defineEmits(['close'])
</script>

<template>
  <Teleport to="body">
    <div
      v-if="show"
      :class="`${mediaType}-modal media-modal`"
      @click.self="$emit('close')">
      <div class="modal-content">
        <button
          class="modal-close"
          :style="{ borderColor: accentColor, background: `${accentColor}33` }"
          @click="$emit('close')">
          <i class="fas fa-times"></i>
        </button>

        <div :class="`modal-${mediaType}-wrapper modal-player-wrapper`">
          <!-- Slot pour le contenu du player (audio/video) -->
          <slot name="player"></slot>
        </div>

        <div class="modal-title">
          <h2>{{ item?.title }}</h2>
        </div>

        <!-- Slot optionnel pour les actions (bouton externe, etc.) -->
        <div v-if="$slots.actions" class="modal-actions">
          <slot name="actions"></slot>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<style scoped>
.media-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.95);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.modal-content {
  width: 100%;
  max-width: 1200px;
  position: relative;
  animation: slideUp 0.3s ease;
}

/* Taille réduite pour les tracks */
.track-modal .modal-content {
  max-width: 800px;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-close {
  position: absolute;
  top: -3rem;
  right: 0;
  border: 2px solid;
  color: #fff;
  font-size: 1.5rem;
  width: 3rem;
  height: 3rem;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10001;
}

.modal-close:hover {
  transform: rotate(90deg);
}

.track-modal .modal-close:hover {
  background: #22c55e !important;
}

.video-modal .modal-close:hover {
  background: #ef4444 !important;
}

.modal-player-wrapper {
  position: relative;
  width: 100%;
  background: #000;
  border-radius: 10px;
  overflow: hidden;
}

/* Styles pour embed HTML (SoundCloud, YouTube) */
.modal-player-wrapper :deep(.embed-html-container) {
  width: 100%;
  min-height: 166px;
}

.modal-player-wrapper :deep(.embed-html-container iframe) {
  width: 100%;
  border: none;
  border-radius: 10px;
}

/* Styles pour Spotify */
.modal-player-wrapper :deep(.spotify-container) {
  width: 100%;
  min-height: 352px;
}

.modal-player-wrapper :deep(.spotify-container iframe) {
  width: 100%;
  height: 352px;
  border: none;
  border-radius: 10px;
}

/* Styles pour lecteur audio */
.modal-player-wrapper :deep(.audio-player) {
  width: 100%;
  display: block;
  padding: 2rem;
}

/* Styles pour embed vidéo (YouTube/Vimeo) */
.modal-player-wrapper :deep(.embed-container) {
  position: relative;
  padding-bottom: 56.25%;
  height: 0;
  overflow: hidden;
}

.modal-player-wrapper :deep(.embed-container iframe) {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border: none;
}

/* Styles pour lecteur vidéo */
.modal-player-wrapper :deep(.video-player) {
  width: 100%;
  display: block;
}

.modal-title {
  margin-top: 1.5rem;
  text-align: center;
}

.modal-title h2 {
  color: #fff;
  font-size: 1.5rem;
  margin: 0;
}

.modal-actions {
  margin-top: 2rem;
  text-align: center;
}

.modal-actions :deep(.btn-open-external) {
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem 2rem;
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
  color: #fff;
  text-decoration: none;
  border-radius: 50px;
  font-size: 1.1rem;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(34, 197, 94, 0.3);
}

.modal-actions :deep(.btn-open-external:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(34, 197, 94, 0.5);
  background: linear-gradient(135deg, #16a34a 0%, #15803d 100%);
}

.modal-actions :deep(.btn-open-external i:first-child) {
  font-size: 1.3rem;
}

.modal-actions :deep(.btn-open-external i:last-child) {
  font-size: 0.9rem;
  opacity: 0.8;
}

.modal-actions :deep(.help-text) {
  margin-top: 0.75rem;
  color: #9ca3af;
  font-size: 0.9rem;
  font-style: italic;
}
</style>
