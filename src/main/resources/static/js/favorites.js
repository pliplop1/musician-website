/**
 * Gestion AJAX des favoris pour éviter le rechargement de page
 */

document.addEventListener('DOMContentLoaded', function() {
    // Intercepter tous les formulaires de favoris
    const favoriteForms = document.querySelectorAll('form[action*="/user/favorites/"]');

    favoriteForms.forEach(form => {
        form.addEventListener('submit', async function(event) {
            event.preventDefault(); // Empêcher le rechargement de page

            const button = form.querySelector('button[type="submit"]');
            const originalText = button.textContent;

            // Désactiver le bouton pendant la requête
            button.disabled = true;
            button.style.opacity = '0.5';

            try {
                // Récupérer le token CSRF si présent
                const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
                const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

                const headers = {
                    'Content-Type': 'application/x-www-form-urlencoded'
                };

                if (csrfToken && csrfHeader) {
                    headers[csrfHeader] = csrfToken;
                }

                // Envoyer la requête
                const response = await fetch(form.action, {
                    method: 'POST',
                    headers: headers
                });

                if (response.ok) {
                    // Déterminer si c'est un ajout ou un retrait
                    const isAdd = form.action.includes('/add/');

                    // Changer le bouton
                    if (isAdd) {
                        // Passer de "Ça m'intéresse" à "Ne m'intéresse plus"
                        button.textContent = "Ne m'intéresse plus";
                        button.classList.remove('bg-green-600', 'hover:bg-green-700');
                        button.classList.add('bg-gray-600', 'hover:bg-gray-700');
                        form.action = form.action.replace('/add/', '/remove/');
                    } else {
                        // Passer de "Ne m'intéresse plus" à "Ça m'intéresse"
                        button.textContent = "Ça m'intéresse";
                        button.classList.remove('bg-gray-600', 'hover:bg-gray-700');
                        button.classList.add('bg-green-600', 'hover:bg-green-700');
                        form.action = form.action.replace('/remove/', '/add/');
                    }

                    // Animation de succès
                    button.style.transform = 'scale(1.05)';
                    setTimeout(() => {
                        button.style.transform = 'scale(1)';
                    }, 200);

                } else {
                    // Erreur
                    alert('Une erreur est survenue. Veuillez réessayer.');
                }

            } catch (error) {
                console.error('Erreur:', error);
                alert('Une erreur est survenue. Veuillez réessayer.');
            } finally {
                // Réactiver le bouton
                button.disabled = false;
                button.style.opacity = '1';
            }
        });
    });
});
