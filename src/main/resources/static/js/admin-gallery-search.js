document.addEventListener('DOMContentLoaded', function() {
    const galleryGrid = document.querySelector('.grid.grid-cols-2');

    if (!galleryGrid) return;

    // Créer la barre de recherche
    const searchContainer = document.createElement('div');
    searchContainer.className = 'bg-white rounded-lg shadow-md p-4 mb-4';
    searchContainer.innerHTML = `
        <div class="flex items-center gap-3">
            <label for="gallery-search" class="text-sm font-medium">🔍 Recherche :</label>
            <input type="text"
                   id="gallery-search"
                   placeholder="Rechercher par nom de fichier..."
                   class="flex-1 border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500">
            <button type="button"
                    class="clear-search bg-gray-200 hover:bg-gray-300 text-gray-700 px-4 py-2 rounded-md text-sm font-medium transition-colors">
                Effacer
            </button>
        </div>
    `;

    // Insérer avant la grille
    galleryGrid.parentNode.insertBefore(searchContainer, galleryGrid);

    // Récupérer les éléments
    const searchInput = searchContainer.querySelector('#gallery-search');
    const clearBtn = searchContainer.querySelector('.clear-search');
    const photoCards = Array.from(galleryGrid.querySelectorAll('.relative.group'));

    // Fonction de recherche
    function filterPhotos() {
        const searchTerm = searchInput.value.toLowerCase().trim();

        photoCards.forEach(card => {
            const img = card.querySelector('img');
            const filename = img?.getAttribute('th:src') || img?.src || '';

            // Extraire le nom du fichier de l'URL
            const filenameMatch = filename.match(/([^/\\]+)(?:\))?$/);
            const actualFilename = filenameMatch ? filenameMatch[1].toLowerCase() : '';

            const matches = actualFilename.includes(searchTerm);
            card.style.display = matches ? '' : 'none';
        });
    }

    // Event listeners
    searchInput.addEventListener('input', filterPhotos);

    clearBtn.addEventListener('click', function() {
        searchInput.value = '';
        filterPhotos();
    });
});
