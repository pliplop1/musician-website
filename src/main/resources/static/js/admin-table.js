/**
 * Script pour ajouter tri et recherche dans les tableaux admin
 */

document.addEventListener('DOMContentLoaded', function() {
    const table = document.querySelector('table');
    if (!table) return;

    const tbody = table.querySelector('tbody');
    if (!tbody) return;

    // Ajouter la barre de recherche avant le tableau
    addSearchBar(table);

    // Ajouter le tri sur les en-têtes cliquables
    addSortingToHeaders(table, tbody);
});

/**
 * Ajoute une barre de recherche au-dessus du tableau
 */
function addSearchBar(table) {
    const searchContainer = document.createElement('div');
    searchContainer.className = 'bg-white rounded-lg shadow-md p-4 mb-4';
    searchContainer.innerHTML = `
        <div class="flex items-center gap-3">
            <label for="table-search" class="text-sm font-medium text-gray-700">🔍 Recherche :</label>
            <input type="text"
                   id="table-search"
                   placeholder="Rechercher..."
                   class="flex-1 border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500">
            <button onclick="document.getElementById('table-search').value=''; document.getElementById('table-search').dispatchEvent(new Event('input'));"
                    class="bg-gray-500 hover:bg-gray-600 text-white font-medium py-2 px-4 rounded-md text-sm transition-colors">
                Effacer
            </button>
        </div>
    `;

    table.parentNode.insertBefore(searchContainer, table);

    const searchInput = document.getElementById('table-search');
    const tbody = table.querySelector('tbody');

    searchInput.addEventListener('input', function() {
        const searchTerm = this.value.toLowerCase().trim();
        const rows = Array.from(tbody.querySelectorAll('tr'));

        rows.forEach(row => {
            const text = row.textContent.toLowerCase();
            if (text.includes(searchTerm)) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });

        // Afficher un message si aucun résultat
        const visibleRows = rows.filter(row => row.style.display !== 'none');
        const noResultRow = tbody.querySelector('.no-results-row');

        if (visibleRows.length === 0 && searchTerm) {
            if (!noResultRow) {
                const colspan = row.querySelectorAll('td').length || 5;
                const tr = document.createElement('tr');
                tr.className = 'no-results-row';
                tr.innerHTML = `<td colspan="${colspan}" class="px-6 py-8 text-center text-gray-500">
                    Aucun résultat trouvé pour "${searchTerm}"
                </td>`;
                tbody.appendChild(tr);
            }
        } else if (noResultRow) {
            noResultRow.remove();
        }
    });
}

/**
 * Ajoute le tri sur les en-têtes de colonnes
 */
function addSortingToHeaders(table, tbody) {
    const headers = table.querySelectorAll('thead th');

    headers.forEach((header, index) => {
        // Ignorer la colonne "Actions" et autres colonnes non triables
        const headerText = header.textContent.trim().toLowerCase();
        if (headerText === 'actions' || headerText === '') {
            return;
        }

        // Ajouter un style pour indiquer que c'est cliquable
        header.style.cursor = 'pointer';
        header.style.userSelect = 'none';
        header.classList.add('hover:bg-gray-100');
        header.title = 'Cliquer pour trier';

        // Ajouter une icône de tri
        header.innerHTML = `
            <div class="flex items-center justify-between">
                <span>${header.textContent}</span>
                <span class="sort-icon text-gray-400">⇅</span>
            </div>
        `;

        let ascending = true;

        header.addEventListener('click', function() {
            // Retirer les indicateurs de tri des autres colonnes
            headers.forEach(h => {
                const icon = h.querySelector('.sort-icon');
                if (icon && h !== header) {
                    icon.textContent = '⇅';
                    icon.classList.remove('text-indigo-600');
                    icon.classList.add('text-gray-400');
                }
            });

            // Mettre à jour l'icône de tri
            const sortIcon = header.querySelector('.sort-icon');
            sortIcon.textContent = ascending ? '↑' : '↓';
            sortIcon.classList.remove('text-gray-400');
            sortIcon.classList.add('text-indigo-600');

            // Trier les lignes
            const rows = Array.from(tbody.querySelectorAll('tr:not(.no-results-row)'));

            rows.sort((a, b) => {
                const aCell = a.querySelectorAll('td')[index];
                const bCell = b.querySelectorAll('td')[index];

                if (!aCell || !bCell) return 0;

                let aValue = aCell.textContent.trim();
                let bValue = bCell.textContent.trim();

                // Essayer de détecter si ce sont des nombres
                const aNum = parseFloat(aValue.replace(/[^\d.-]/g, ''));
                const bNum = parseFloat(bValue.replace(/[^\d.-]/g, ''));

                if (!isNaN(aNum) && !isNaN(bNum)) {
                    return ascending ? aNum - bNum : bNum - aNum;
                }

                // Essayer de détecter si ce sont des dates (format dd/MM/yyyy)
                const dateRegex = /(\d{2})\/(\d{2})\/(\d{4})/;
                const aMatch = aValue.match(dateRegex);
                const bMatch = bValue.match(dateRegex);

                if (aMatch && bMatch) {
                    const aDate = new Date(aMatch[3], aMatch[2] - 1, aMatch[1]);
                    const bDate = new Date(bMatch[3], bMatch[2] - 1, bMatch[1]);
                    return ascending ? aDate - bDate : bDate - aDate;
                }

                // Tri alphabétique par défaut
                return ascending
                    ? aValue.localeCompare(bValue, 'fr')
                    : bValue.localeCompare(aValue, 'fr');
            });

            // Réinsérer les lignes triées
            rows.forEach(row => tbody.appendChild(row));

            // Inverser le sens pour le prochain clic
            ascending = !ascending;
        });
    });
}
