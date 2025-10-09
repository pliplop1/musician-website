/**
 * Gestion AJAX des commentaires pour éviter le rechargement de page
 */

document.addEventListener('DOMContentLoaded', function() {
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
                // Récupérer le token CSRF si présent
                const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
                const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

                // Préparer les données du formulaire
                const formData = new FormData(form);

                const headers = {};
                if (csrfToken && csrfHeader) {
                    headers[csrfHeader] = csrfToken;
                }

                // Envoyer la requête
                const response = await fetch(form.action, {
                    method: 'POST',
                    headers: headers,
                    body: formData
                });

                if (response.ok) {
                    // Vider le textarea
                    textarea.value = '';

                    // Afficher un message de succès
                    showCommentToast('💬 Commentaire envoyé ! Il sera visible après modération par l\'administrateur.', 'success');

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
                    // Erreur
                    showCommentToast('❌ Une erreur est survenue. Veuillez réessayer.', 'error');
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
});

// Toast notification pour les commentaires
function showCommentToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `fixed top-4 right-4 px-6 py-3 rounded-lg shadow-lg text-white z-50 transition-opacity duration-300 ${
        type === 'success' ? 'bg-green-600' : 'bg-red-600'
    }`;
    toast.textContent = message;

    document.body.appendChild(toast);

    // Fade out and remove after 5 seconds
    setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 300);
    }, 5000);
}
