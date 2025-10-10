<script setup>
// Informations de contact du Duo Black & White
const contactInfo = {
  phone: '06 01 23 45 47',
  email: 'dumoulin.marilyne@gmail.com',
  location: 'Abbeville, Picardie, France',
  serviceArea: 'France & Belgique',
  facebook: 'https://www.facebook.com/DuoBlackandWhiteMP/'
}
</script>

<template>
  <section class="contact-section">
    <div class="container">
      <h2 class="section-title">Contactez-nous</h2>
      <p class="section-subtitle">
        Pour vos événements, mariages, soirées privées ou concerts
      </p>

      <div class="contact-grid">
        <!-- Informations de contact -->
        <div class="contact-info">
          <div class="info-card">
            <div class="icon">📞</div>
            <h3>Téléphone</h3>
            <a :href="`tel:${contactInfo.phone.replace(/\s/g, '')}`" class="contact-link">
              {{ contactInfo.phone }}
            </a>
          </div>

          <div class="info-card">
            <div class="icon">✉️</div>
            <h3>Email</h3>
            <a :href="`mailto:${contactInfo.email}`" class="contact-link">
              {{ contactInfo.email }}
            </a>
          </div>

          <div class="info-card">
            <div class="icon">📍</div>
            <h3>Localisation</h3>
            <p>{{ contactInfo.location }}</p>
            <p class="service-area">Zone de service: {{ contactInfo.serviceArea }}</p>
          </div>

          <div class="info-card">
            <div class="icon">👥</div>
            <h3>Le Duo</h3>
            <p><strong>Marilyne Dumoulin</strong> - Chant</p>
            <p><strong>Philippe Prudhomme</strong> - Guitare & Chant</p>
          </div>
        </div>

        <!-- Réseaux sociaux -->
        <div class="social-card">
          <h3>Suivez-nous</h3>
          <div class="social-links">
            <a :href="contactInfo.facebook" target="_blank" class="social-btn facebook">
              <span class="icon">📘</span>
              Facebook
            </a>
          </div>

          <div class="cta-box">
            <h4>Réservez votre événement</h4>
            <p>Contactez-nous pour un devis personnalisé et découvrez notre répertoire varié!</p>
            <a :href="`tel:${contactInfo.phone.replace(/\s/g, '')}`" class="btn-cta">
              Appelez-nous
            </a>
            <a :href="`mailto:${contactInfo.email}`" class="btn-cta secondary">
              Envoyez un email
            </a>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.contact-section {
  padding: 5rem 2rem;
  background: #0a0a0a;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.section-title {
  font-size: 3rem;
  text-align: center;
  margin-bottom: 1rem;
  text-transform: uppercase;
  letter-spacing: 5px;
}

.section-subtitle {
  text-align: center;
  font-size: 1.2rem;
  color: #888;
  margin-bottom: 3rem;
}

.contact-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 3rem;
}

/* Contact Info Cards */
.contact-info {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
}

.info-card {
  background: #1a1a1a;
  padding: 2rem;
  border-radius: 10px;
  text-align: center;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.info-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(255, 255, 255, 0.1);
}

.icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.info-card h3 {
  font-size: 1.3rem;
  margin-bottom: 1rem;
  color: #4a90e2;
}

.info-card p {
  color: #ccc;
  margin: 0.5rem 0;
}

.contact-link {
  color: #fff;
  text-decoration: none;
  font-size: 1.1rem;
  transition: color 0.3s ease;
}

.contact-link:hover {
  color: #4a90e2;
}

.service-area {
  font-size: 0.9rem;
  color: #888;
  margin-top: 0.5rem;
}

/* Social Card */
.social-card {
  background: #1a1a1a;
  padding: 2rem;
  border-radius: 10px;
}

.social-card h3 {
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
  color: #4a90e2;
  text-align: center;
}

.social-links {
  margin-bottom: 2rem;
}

.social-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  padding: 1rem;
  background: #1877f2;
  color: white;
  text-decoration: none;
  border-radius: 8px;
  font-weight: bold;
  transition: all 0.3s ease;
}

.social-btn:hover {
  background: #145dbf;
  transform: scale(1.05);
}

.social-btn .icon {
  font-size: 1.5rem;
  margin: 0;
}

/* CTA Box */
.cta-box {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 2rem;
  border-radius: 10px;
  text-align: center;
}

.cta-box h4 {
  font-size: 1.3rem;
  margin-bottom: 1rem;
}

.cta-box p {
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 1.5rem;
}

.btn-cta {
  display: block;
  padding: 1rem 2rem;
  background: white;
  color: #667eea;
  text-decoration: none;
  border-radius: 25px;
  font-weight: bold;
  margin: 1rem 0;
  transition: all 0.3s ease;
}

.btn-cta:hover {
  background: #f0f0f0;
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(255, 255, 255, 0.3);
}

.btn-cta.secondary {
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

.btn-cta.secondary:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* Responsive */
@media (max-width: 968px) {
  .contact-grid {
    grid-template-columns: 1fr;
  }

  .contact-info {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .section-title {
    font-size: 2rem;
  }

  .section-subtitle {
    font-size: 1rem;
  }
}
</style>
