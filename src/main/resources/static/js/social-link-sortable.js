// Script pour le glisser-déposer des liens sociaux
document.addEventListener('DOMContentLoaded', function() {
    const socialLinksContainer = document.querySelector('.grid.gap-6');

    if (!socialLinksContainer) return;

    // Initialiser Sortable
    new Sortable(socialLinksContainer, {
        animation: 150,
        ghostClass: 'sortable-ghost',
        chosenClass: 'sortable-chosen',
        dragClass: 'sortable-drag',
        handle: '.social-link-card', // Toute la carte est draggable
        onEnd: function(evt) {
            // Récupérer le nouvel ordre des liens
            const linkCards = socialLinksContainer.querySelectorAll('[data-link-id]');
            const linkIds = Array.from(linkCards).map(card => parseInt(card.dataset.linkId));

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
            fetch('/api/social-links/reorder', {
                method: 'POST',
                headers: headers,
                body: JSON.stringify(linkIds)
            })
            .then(response => response.json())
            .then(data => {
                console.log('Ordre des liens sociaux mis à jour:', data);
                // Optionnel : afficher un message de succès
                showToast('✓ Ordre des liens enregistré', 'success');
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
