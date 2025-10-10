<template>
  <div class="error-container">
    <div class="error-icon">⚠️</div>
    <h3>{{ title }}</h3>
    <p>{{ message }}</p>
    <button v-if="retry" @click="$emit('retry')" class="retry-btn">
      Réessayer
    </button>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    default: 'Une erreur est survenue'
  },
  message: {
    type: String,
    default: 'Impossible de charger les données. Veuillez réessayer plus tard.'
  },
  retry: {
    type: Boolean,
    default: true
  }
})

defineEmits(['retry'])
</script>

<style scoped>
.error-container {
  padding: 3rem 2rem;
  text-align: center;
  background: #1a1a1a;
  border-radius: 10px;
  border: 2px solid #ff4444;
  margin: 2rem 0;
}

.error-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.error-container h3 {
  color: #ff4444;
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
}

.error-container p {
  color: #ccc;
  margin-bottom: 1.5rem;
}

.retry-btn {
  padding: 0.75rem 2rem;
  background: #4a90e2;
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.retry-btn:hover {
  background: #357abd;
  transform: translateY(-2px);
}
</style>
