// Script pour le glisser-déposer de la galerie photo
document.addEventListener('DOMContentLoaded', function() {
    const photoGrid = document.querySelector('.grid');

    if (!photoGrid) {
        return;
    }

    // Initialiser Sortable
    new Sortable(photoGrid, {
        animation: 150,
        ghostClass: 'sortable-ghost',
        chosenClass: 'sortable-chosen',
        dragClass: 'sortable-drag',
        onEnd: function(evt) {
            // Récupérer le nouvel ordre des photos
            const photoCards = photoGrid.querySelectorAll('[data-photo-id]');
            const photoIds = Array.from(photoCards).map(card => parseInt(card.dataset.photoId));

            // Récupérer le token CSRF
            const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

            const headers = {
                'Content-Type': 'application/json'
            };

            if (csrfToken && csrfHeader) {
                headers[csrfHeader] = csrfToken;
            }

            // Envoyer l'ordre au serveur
            fetch('/api/photos/reorder', {
                method: 'POST',
                headers: headers,
                body: JSON.stringify(photoIds)
            })
            .then(response => response.json())
            .then(data => {
                showToast('✓ Ordre des photos enregistré', 'success');
            })
            .catch(error => {
                console.error('Erreur lors de la mise à jour:', error);
                showToast('✗ Erreur lors de la sauvegarde', 'error');
            });
        }
    });

    // Fonction pour afficher un toast (notification)
    function showToast(message, type) {
        const toast = document.createElement('div');
        toast.className = `fixed bottom-4 right-4 px-6 py-3 rounded-lg shadow-lg text-white ${
            type === 'success' ? 'bg-green-500' : 'bg-red-500'
        } transition-opacity duration-300`;
        toast.textContent = message;
        document.body.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = '0';
            setTimeout(() => toast.remove(), 300);
        }, 3000);
    }
});
