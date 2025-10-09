/**
 * Gestion des actions AJAX pour les commentaires
 */

// Toast notification system
function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `fixed top-4 right-4 px-6 py-3 rounded-lg shadow-lg text-white z-50 transition-opacity duration-300 ${
        type === 'success' ? 'bg-green-600' : 'bg-red-600'
    }`;
    toast.textContent = message;

    document.body.appendChild(toast);

    // Fade out and remove after 3 seconds
    setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

// Approuver un commentaire
async function approveComment(commentId, button) {
    try {
        // Désactiver le bouton pendant la requête
        button.disabled = true;
        button.classList.add('opacity-50', 'cursor-not-allowed');

        // Récupérer le token CSRF
        const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

        const headers = {
            'Content-Type': 'application/json'
        };

        // Ajouter le token CSRF si disponible
        if (csrfToken && csrfHeader) {
            headers[csrfHeader] = csrfToken;
        }

        const response = await fetch(`/api/comments/${commentId}/approve`, {
            method: 'POST',
            headers: headers
        });

        const data = await response.json();

        if (data.success) {
            // Trouver le conteneur du commentaire (div)
            const commentDiv = button.closest('.bg-white.rounded-lg');

            // Animation de succès - flash vert
            commentDiv.style.backgroundColor = '#10b981';
            commentDiv.style.transition = 'background-color 0.3s ease';

            setTimeout(() => {
                commentDiv.style.backgroundColor = '';
            }, 500);

            // Désactiver le bouton "Approuver" et le remplacer par un indicateur
            button.outerHTML = '<span class="text-green-600 font-medium text-sm">✓ Approuvé</span>';

            showToast(data.message, 'success');
        } else {
            // Réactiver le bouton en cas d'erreur
            button.disabled = false;
            button.classList.remove('opacity-50', 'cursor-not-allowed');
            showToast(data.message, 'error');
        }
    } catch (error) {
        console.error('Erreur lors de l\'approbation:', error);
        button.disabled = false;
        button.classList.remove('opacity-50', 'cursor-not-allowed');
        showToast('Erreur lors de l\'approbation du commentaire', 'error');
    }
}

// Supprimer un commentaire
async function deleteComment(commentId, button) {
    if (!confirm('Êtes-vous sûr de vouloir supprimer ce commentaire ?')) {
        return;
    }

    try {
        // Désactiver le bouton pendant la requête
        button.disabled = true;
        button.classList.add('opacity-50', 'cursor-not-allowed');

        // Récupérer le token CSRF
        const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

        const headers = {
            'Content-Type': 'application/json'
        };

        // Ajouter le token CSRF si disponible
        if (csrfToken && csrfHeader) {
            headers[csrfHeader] = csrfToken;
        }

        const response = await fetch(`/api/comments/${commentId}`, {
            method: 'DELETE',
            headers: headers
        });

        const data = await response.json();

        if (data.success) {
            // Trouver le conteneur du commentaire (div)
            const commentDiv = button.closest('.bg-white.rounded-lg');

            // Animation de suppression - fade out
            commentDiv.style.opacity = '1';
            commentDiv.style.transition = 'opacity 0.3s ease';
            commentDiv.style.opacity = '0';

            setTimeout(() => {
                commentDiv.remove();
            }, 300);

            showToast(data.message, 'success');
        } else {
            // Réactiver le bouton en cas d'erreur
            button.disabled = false;
            button.classList.remove('opacity-50', 'cursor-not-allowed');
            showToast(data.message, 'error');
        }
    } catch (error) {
        console.error('Erreur lors de la suppression:', error);
        button.disabled = false;
        button.classList.remove('opacity-50', 'cursor-not-allowed');
        showToast('Erreur lors de la suppression du commentaire', 'error');
    }
}

// Initialiser les écouteurs d'événements au chargement de la page
document.addEventListener('DOMContentLoaded', function() {
    // Attacher les événements aux boutons d'approbation
    document.querySelectorAll('.approve-comment-btn').forEach(button => {
        button.addEventListener('click', function() {
            const commentId = this.getAttribute('data-comment-id');
            approveComment(commentId, this);
        });
    });

    // Attacher les événements aux boutons de suppression
    document.querySelectorAll('.delete-comment-btn').forEach(button => {
        button.addEventListener('click', function() {
            const commentId = this.getAttribute('data-comment-id');
            deleteComment(commentId, this);
        });
    });
});
