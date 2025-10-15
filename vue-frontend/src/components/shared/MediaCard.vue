<script setup>
defineProps({
  item: {
    type: Object,
    required: true
  },
  mediaType: {
    type: String,
    required: true,
    validator: (value) => ['track', 'video'].includes(value)
  },
  thumbnailUrl: {
    type: String,
    default: null
  },
  playerTypeInfo: {
    type: Object,
    required: true
  }
})

defineEmits(['click'])
</script>

<template>
  <div
    :class="`${mediaType}-card media-card`"
    @click="$emit('click', item)">
    <div :class="`${mediaType}-thumbnail media-thumbnail`">
      <!-- Thumbnail pour vidéos (avec image) -->
      <img
        v-if="mediaType === 'video' && thumbnailUrl"
        :src="thumbnailUrl"
        :alt="item.title"
        loading="lazy"
        decoding="async" />

      <!-- Thumbnail pour tracks (icône musique) -->
      <div v-if="mediaType === 'track'" class="music-icon-overlay">
        <i class="fas fa-music"></i>
      </div>

      <!-- Play overlay (commun) -->
      <div class="play-overlay">
        <i class="fas fa-play-circle"></i>
      </div>
    </div>

    <div :class="`${mediaType}-card-footer media-card-footer`">
      <h3>{{ item.title }}</h3>
      <div class="media-meta">
        <span :class="`${mediaType}-type-badge media-type-badge`">
          <i :class="playerTypeInfo.icon"></i>
          {{ playerTypeInfo.label }}
        </span>
        <div class="media-stats">
          <!-- Compteur de vues (vidéos uniquement) -->
          <span v-if="mediaType === 'video'" class="view-count-badge" :title="`${item.viewCount || 0} vues`">
            <i class="fas fa-eye"></i>
            <span>{{ item.viewCount || 0 }}</span>
          </span>
          <!-- Compteur de lectures (tracks uniquement) -->
          <span v-if="mediaType === 'track'" class="play-count-badge" :title="`${item.playCount || 0} écoutes`">
            <i class="fas fa-headphones"></i>
            <span>{{ item.playCount || 0 }}</span>
          </span>
          <!-- Compteur de likes (commun) -->
          <span class="like-count-badge" :title="`${item.likeCount || 0} likes`">
            <i class="fas fa-heart"></i>
            <span>{{ item.likeCount || 0 }}</span>
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Styles de base communs */
.media-card {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  overflow: hidden;
  transition: all 0.4s ease;
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  cursor: pointer;
  animation: fadeInUp 0.6s ease-out;
}

.media-card:hover {
  transform: translateY(-10px) scale(1.05);
}

.media-card:hover .play-overlay {
  opacity: 1;
  transform: translate(-50%, -50%) scale(1.2);
}

.media-thumbnail {
  position: relative;
  width: 100%;
  aspect-ratio: 16/9;
  overflow: hidden;
}

.media-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.media-card:hover .media-thumbnail img {
  transform: scale(1.15);
}

.play-overlay {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 4rem;
  opacity: 0.8;
  transition: all 0.3s ease;
  pointer-events: none;
  text-shadow: 0 0 20px rgba(0, 0, 0, 0.5);
  z-index: 2;
}

.music-icon-overlay {
  font-size: 5rem;
  color: rgba(255, 255, 255, 0.1);
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1;
}

.media-card-footer {
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.media-card-footer h3 {
  font-size: 1.1rem;
  margin: 0;
  color: #fff;
  font-weight: 600;
  line-height: 1.4;
}

.media-type-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.4rem 0.8rem;
  border-radius: 15px;
  font-size: 0.8rem;
  width: fit-content;
}

/* Styles spécifiques tracks (vert) */
.track-card {
  composes: media-card;
}

.track-card:hover {
  box-shadow: 0 12px 40px rgba(34, 197, 94, 0.3);
  border-color: rgba(34, 197, 94, 0.3);
}

.track-thumbnail {
  background: linear-gradient(135deg, #1a1a2e 0%, #22c55e 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.track-card .play-overlay {
  color: #22c55e;
}

.track-type-badge {
  background: rgba(34, 197, 94, 0.15);
  border: 1px solid rgba(34, 197, 94, 0.3);
  color: #4ade80;
}

/* Styles spécifiques videos (rouge) */
.video-card {
  composes: media-card;
}

.video-card:hover {
  box-shadow: 0 12px 40px rgba(239, 68, 68, 0.3);
  border-color: rgba(239, 68, 68, 0.3);
}

.video-thumbnail {
  background: #000;
}

.video-card .play-overlay {
  color: #ef4444;
}

.video-type-badge {
  background: rgba(239, 68, 68, 0.15);
  border: 1px solid rgba(239, 68, 68, 0.3);
  color: #f87171;
}

/* Styles pour les statistiques */
.media-meta {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.media-stats {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex-wrap: wrap;
}

/* Badges de statistiques communs */
.view-count-badge,
.play-count-badge,
.like-count-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.3rem 0.7rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 500;
  transition: all 0.3s ease;
}

/* Badge de vues (bleu) */
.view-count-badge {
  background: rgba(59, 130, 246, 0.15);
  border: 1px solid rgba(59, 130, 246, 0.3);
  color: #60a5fa;
}

.view-count-badge:hover {
  background: rgba(59, 130, 246, 0.25);
  transform: scale(1.05);
}

/* Badge de lectures (violet) */
.play-count-badge {
  background: rgba(168, 85, 247, 0.15);
  border: 1px solid rgba(168, 85, 247, 0.3);
  color: #c084fc;
}

.play-count-badge:hover {
  background: rgba(168, 85, 247, 0.25);
  transform: scale(1.05);
}

/* Badge de likes (rose/rouge) */
.like-count-badge {
  background: rgba(236, 72, 153, 0.15);
  border: 1px solid rgba(236, 72, 153, 0.3);
  color: #f472b6;
}

.like-count-badge:hover {
  background: rgba(236, 72, 153, 0.25);
  transform: scale(1.05);
}

/* Icônes dans les badges */
.view-count-badge i,
.play-count-badge i,
.like-count-badge i {
  font-size: 0.9rem;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Animation en cascade */
.media-card:nth-child(1) {
  animation-delay: 0.1s;
}

.media-card:nth-child(2) {
  animation-delay: 0.2s;
}

.media-card:nth-child(3) {
  animation-delay: 0.3s;
}

@media (max-width: 768px) {
  .media-type-badge {
    font-size: 0.75rem;
    padding: 0.4rem 0.8rem;
  }

  .media-stats {
    gap: 0.5rem;
  }

  .view-count-badge,
  .play-count-badge,
  .like-count-badge {
    font-size: 0.7rem;
    padding: 0.25rem 0.6rem;
    gap: 0.3rem;
  }

  .view-count-badge i,
  .play-count-badge i,
  .like-count-badge i {
    font-size: 0.8rem;
  }
}
</style>
