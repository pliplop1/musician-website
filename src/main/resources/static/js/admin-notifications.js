// Script pour gérer les notifications dans l'admin
document.addEventListener('DOMContentLoaded', function() {
    const bellButton = document.getElementById('notificationBell');
    const badge = document.getElementById('notificationBadge');
    const dropdown = document.getElementById('notificationDropdown');
    const notificationList = document.getElementById('notificationList');

    if (!bellButton || !badge || !dropdown || !notificationList) {
        return; // Ne pas exécuter si les éléments n'existent pas
    }

    // Charger les notifications au chargement de la page
    loadNotifications();

    // Recharger les notifications toutes les 30 secondes
    setInterval(loadNotifications, 30000);

    // Toggle du menu déroulant au clic sur la cloche
    bellButton.addEventListener('click', function(e) {
        e.stopPropagation();
        dropdown.classList.toggle('hidden');
    });

    // Fermer le dropdown si on clique ailleurs
    document.addEventListener('click', function(e) {
        if (!dropdown.contains(e.target) && !bellButton.contains(e.target)) {
            dropdown.classList.add('hidden');
        }
    });

    // Fonction pour charger les notifications
    function loadNotifications() {
        fetch('/api/notifications/stats')
            .then(response => response.json())
            .then(data => {
                updateBadge(data.total);
                updateNotificationList(data);
            })
            .catch(error => {
                console.error('Erreur lors du chargement des notifications:', error);
            });
    }

    // Mettre à jour le badge de notifications
    function updateBadge(count) {
        if (count > 0) {
            badge.textContent = count > 99 ? '99+' : count;
            badge.classList.remove('hidden');
        } else {
            badge.classList.add('hidden');
        }
    }

    // Mettre à jour la liste des notifications
    function updateNotificationList(data) {
        if (data.total === 0) {
            notificationList.innerHTML = `
                <div class="p-4 text-center text-gray-500">
                    <svg class="w-12 h-12 mx-auto mb-2 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                              d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                    </svg>
                    <p>Aucune notification</p>
                    <p class="text-xs mt-1">Tout est à jour !</p>
                </div>
            `;
            return;
        }

        let html = '';

        // Messages non lus
        if (data.unreadMessages > 0) {
            html += `
                <a href="/admin/messages" class="block px-4 py-3 hover:bg-gray-50 border-b border-gray-100 transition-colors">
                    <div class="flex items-center justify-between">
                        <div class="flex items-center space-x-3">
                            <div class="flex-shrink-0">
                                <div class="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
                                    <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                              d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"></path>
                                    </svg>
                                </div>
                            </div>
                            <div>
                                <p class="text-sm font-medium text-gray-900">Messages non lus</p>
                                <p class="text-xs text-gray-500">Nouveaux messages de contact</p>
                            </div>
                        </div>
                        <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                            ${data.unreadMessages}
                        </span>
                    </div>
                </a>
            `;
        }

        // Commentaires en attente
        if (data.pendingComments > 0) {
            html += `
                <a href="/admin/comments" class="block px-4 py-3 hover:bg-gray-50 border-b border-gray-100 transition-colors">
                    <div class="flex items-center justify-between">
                        <div class="flex items-center space-x-3">
                            <div class="flex-shrink-0">
                                <div class="w-10 h-10 bg-yellow-100 rounded-full flex items-center justify-center">
                                    <svg class="w-5 h-5 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                              d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z"></path>
                                    </svg>
                                </div>
                            </div>
                            <div>
                                <p class="text-sm font-medium text-gray-900">Commentaires en attente</p>
                                <p class="text-xs text-gray-500">À modérer</p>
                            </div>
                        </div>
                        <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
                            ${data.pendingComments}
                        </span>
                    </div>
                </a>
            `;
        }

        notificationList.innerHTML = html;
    }
});
