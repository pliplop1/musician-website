<template>
  <div class="loading-container">
    <div class="spinner"></div>
    <p v-if="message">{{ message }}</p>
  </div>
</template>

<script setup>
defineProps({
  message: {
    type: String,
    default: 'Chargement...'
  }
})
</script>

<style scoped>
.loading-container {
  padding: 3rem 2rem;
  text-align: center;
}

.spinner {
  width: 50px;
  height: 50px;
  margin: 0 auto 1rem;
  border: 5px solid #333;
  border-top: 5px solid #4a90e2;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-container p {
  color: #888;
  font-size: 1.1rem;
}
</style>
