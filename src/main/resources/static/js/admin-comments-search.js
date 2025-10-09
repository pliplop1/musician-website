document.addEventListener('DOMContentLoaded', function() {
    const pendingSection = document.querySelector('section:first-of-type .space-y-4');
    const allSection = document.querySelector('section:last-of-type .space-y-4');

    if (pendingSection) {
        addSearchBarToSection(pendingSection, 'pending');
    }

    if (allSection) {
        addSearchBarToSection(allSection, 'all');
    }
});

function addSearchBarToSection(container, sectionId) {
    // Créer la barre de recherche
    const searchContainer = document.createElement('div');
    searchContainer.className = 'bg-white rounded-lg shadow-md p-4 mb-4';
    searchContainer.innerHTML = `
        <div class="flex items-center gap-3">
            <label for="comment-search-${sectionId}" class="text-sm font-medium">🔍 Recherche :</label>
            <input type="text"
                   id="comment-search-${sectionId}"
                   placeholder="Rechercher par utilisateur, contenu..."
                   class="flex-1 border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500">
            <button type="button"
                    class="clear-search bg-gray-200 hover:bg-gray-300 text-gray-700 px-4 py-2 rounded-md text-sm font-medium transition-colors"
                    data-target="comment-search-${sectionId}">
                Effacer
            </button>
            <select id="sort-select-${sectionId}"
                    class="border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500">
                <option value="">Trier par...</option>
                <option value="date-desc">Date (plus récent)</option>
                <option value="date-asc">Date (plus ancien)</option>
                <option value="user-asc">Utilisateur (A-Z)</option>
                <option value="user-desc">Utilisateur (Z-A)</option>
                <option value="type-asc">Type (A-Z)</option>
                <option value="type-desc">Type (Z-A)</option>
            </select>
        </div>
    `;

    // Insérer avant le conteneur
    container.parentNode.insertBefore(searchContainer, container);

    // Récupérer les éléments
    const searchInput = searchContainer.querySelector(`#comment-search-${sectionId}`);
    const clearBtn = searchContainer.querySelector('.clear-search');
    const sortSelect = searchContainer.querySelector(`#sort-select-${sectionId}`);
    const commentCards = Array.from(container.querySelectorAll('.bg-white.rounded-lg'));

    // Fonction de recherche
    function filterComments() {
        const searchTerm = searchInput.value.toLowerCase().trim();

        commentCards.forEach(card => {
            const username = card.querySelector('.font-semibold')?.textContent.toLowerCase() || '';
            const content = card.querySelector('.whitespace-pre-wrap')?.textContent.toLowerCase() || '';
            const type = card.querySelector('.text-xs.px-2.py-1.rounded-full.bg-gray-100')?.textContent.toLowerCase() || '';

            const matches = username.includes(searchTerm) ||
                          content.includes(searchTerm) ||
                          type.includes(searchTerm);

            card.style.display = matches ? '' : 'none';
        });
    }

    // Fonction de tri
    function sortComments() {
        const sortValue = sortSelect.value;
        if (!sortValue) return;

        const sortedCards = [...commentCards].sort((a, b) => {
            let aValue, bValue;

            if (sortValue.startsWith('date')) {
                // Récupérer la date
                const aDateStr = a.querySelector('.text-xs.text-gray-500')?.textContent.trim() || '';
                const bDateStr = b.querySelector('.text-xs.text-gray-500')?.textContent.trim() || '';

                // Convertir dd/MM/yyyy HH:mm en Date
                const parseDate = (str) => {
                    const match = str.match(/(\d{2})\/(\d{2})\/(\d{4})\s+(\d{2}):(\d{2})/);
                    if (match) {
                        const [, day, month, year, hour, minute] = match;
                        return new Date(year, month - 1, day, hour, minute);
                    }
                    return new Date(0);
                };

                aValue = parseDate(aDateStr);
                bValue = parseDate(bDateStr);

                return sortValue === 'date-asc' ? aValue - bValue : bValue - aValue;
            }
            else if (sortValue.startsWith('user')) {
                aValue = a.querySelector('.font-semibold')?.textContent.toLowerCase() || '';
                bValue = b.querySelector('.font-semibold')?.textContent.toLowerCase() || '';

                return sortValue === 'user-asc'
                    ? aValue.localeCompare(bValue)
                    : bValue.localeCompare(aValue);
            }
            else if (sortValue.startsWith('type')) {
                aValue = a.querySelector('.text-xs.px-2.py-1.rounded-full.bg-gray-100')?.textContent.trim() || '';
                bValue = b.querySelector('.text-xs.px-2.py-1.rounded-full.bg-gray-100')?.textContent.trim() || '';

                return sortValue === 'type-asc'
                    ? aValue.localeCompare(bValue)
                    : bValue.localeCompare(aValue);
            }

            return 0;
        });

        // Réorganiser les cartes
        sortedCards.forEach(card => container.appendChild(card));
    }

    // Event listeners
    searchInput.addEventListener('input', filterComments);

    clearBtn.addEventListener('click', function() {
        searchInput.value = '';
        filterComments();
    });

    sortSelect.addEventListener('change', sortComments);
}
