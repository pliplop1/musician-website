/**
 * Gestion AJAX des commentaires pour éviter le rechargement de page
 * VERSION: 2.0.0 - CSRF fix + inline CSS toast
 */

console.log('📝 comments.js VERSION 2.0.0 chargé');

// Fonction d'initialisation des formulaires de commentaires
function initCommentForms() {
    // Intercepter tous les formulaires de commentaires
    const commentForms = document.querySelectorAll('form[action*="/comments/"]');

    commentForms.forEach(form => {
        form.addEventListener('submit', async function(event) {
            event.preventDefault(); // Empêcher le rechargement de page

            const button = form.querySelector('button[type="submit"]');
            const textarea = form.querySelector('textarea[name="content"]');
            const originalButtonText = button.textContent;

            // Vérifier que le textarea n'est pas vide
            if (!textarea.value.trim()) {
                alert('Veuillez saisir un commentaire.');
                return;
            }

            // Désactiver le bouton et le textarea pendant la requête
            button.disabled = true;
            textarea.disabled = true;
            button.textContent = 'Publication...';
            button.style.opacity = '0.5';

            try {
                // Préparer les données du formulaire
                // Le FormData récupère automatiquement tous les champs, y compris le token CSRF s'il existe
                const formData = new FormData(form);

                // Envoyer la requête
                const response = await fetch(form.action, {
                    method: 'POST',
                    body: formData
                });

                // Gérer les erreurs d'authentification
                if (response.status === 401 || response.status === 403) {
                    showCommentToast('❌ Vous devez être connecté pour poster un commentaire. Veuillez vous <a href="/login" class="underline">connecter</a>.', 'error');
                    return;
                }

                // Vérifier si la réponse est JSON
                const contentType = response.headers.get('content-type');
                if (contentType && contentType.includes('application/json')) {
                    const data = await response.json();

                    if (data.success) {
                        // Vider le textarea
                        textarea.value = '';

                        // Afficher un message de succès
                        showCommentToast(data.message || '💬 Commentaire envoyé ! Il sera visible après modération par l\'administrateur.', 'success');

                        // Incrémenter le compteur de commentaires si présent
                        const commentSection = form.closest('.border-t');
                        if (commentSection) {
                            const counterSpan = commentSection.querySelector('h4 span');
                            if (counterSpan) {
                                const currentCount = parseInt(counterSpan.textContent) || 0;
                                counterSpan.textContent = currentCount + 1;
                            }
                        }
                    } else {
                        // Erreur du serveur
                        showCommentToast(data.message || '❌ Une erreur est survenue. Veuillez réessayer.', 'error');
                    }
                } else {
                    // Pas de JSON, probablement un redirect ou une erreur
                    if (!response.ok) {
                        showCommentToast('❌ Erreur: Le serveur a renvoyé une erreur (code ' + response.status + ').', 'error');
                    } else {
                        showCommentToast('❌ Erreur: Le serveur n\'a pas renvoyé la bonne réponse.', 'error');
                    }
                }

            } catch (error) {
                console.error('Erreur:', error);
                showCommentToast('❌ Une erreur est survenue. Veuillez réessayer.', 'error');
            } finally {
                // Réactiver le bouton et le textarea
                button.disabled = false;
                textarea.disabled = false;
                button.textContent = originalButtonText;
                button.style.opacity = '1';
            }
        });
    });
}

// Initialiser immédiatement si le DOM est déjà chargé, sinon attendre
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initCommentForms);
} else {
    // Le DOM est déjà chargé, exécuter immédiatement
    initCommentForms();
}

// Toast notification pour les commentaires
function showCommentToast(message, type = 'success') {
    const toast = document.createElement('div');

    // Utiliser du CSS inline au lieu de classes Tailwind pour les éléments dynamiques
    const bgColor = type === 'success' ? 'rgb(22, 163, 74)' : 'rgb(220, 38, 38)';
    toast.style.cssText = `
        position: fixed;
        top: 1rem;
        right: 1rem;
        padding: 0.75rem 1.5rem;
        border-radius: 0.5rem;
        box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
        color: white;
        background-color: ${bgColor};
        z-index: 9999;
        transition: opacity 0.3s ease-in-out;
        font-size: 0.875rem;
        line-height: 1.25rem;
    `;

    toast.innerHTML = message; // Utiliser innerHTML au lieu de textContent pour supporter les liens HTML

    document.body.appendChild(toast);

    // Fade out and remove after 5 seconds
    setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 300);
    }, 5000);
}
