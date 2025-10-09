/**
 * Gestion AJAX des commentaires pour éviter le rechargement de page
 */

const TOAST_DISPLAY_DURATION = 5000;
const TOAST_FADE_DURATION = 300;

function initCommentForms() {
    const commentForms = document.querySelectorAll('form[data-comment-form="true"]');

    commentForms.forEach(form => {
        form.addEventListener('submit', async function(event) {
            event.preventDefault();

            const button = form.querySelector('button[type="submit"]');
            const textarea = form.querySelector('textarea[name="content"]');
            const originalButtonText = button.textContent;

            if (!textarea.value.trim()) {
                alert('Veuillez saisir un commentaire.');
                return;
            }

            disableForm(button, textarea);

            try {
                const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
                const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

                const urlParams = new URLSearchParams();
                urlParams.append('content', textarea.value);

                const response = await fetch(form.action, {
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded',
                        [csrfHeader]: csrfToken
                    },
                    body: urlParams.toString()
                });

                if (response.status === 401 || response.status === 403) {
                    showCommentToast('❌ Vous devez être connecté pour poster un commentaire. Veuillez vous <a href="/login" class="underline">connecter</a>.', 'error');
                    return;
                }

                const contentType = response.headers.get('content-type');

                if (contentType && contentType.includes('application/json')) {
                    const data = await response.json();

                    if (data.success) {
                        textarea.value = '';
                        showCommentToast(data.message || '💬 Commentaire envoyé ! Il sera visible après modération par l\'administrateur.', 'success');
                        incrementCommentCounter(form);
                    } else {
                        showCommentToast(data.message || '❌ Une erreur est survenue. Veuillez réessayer.', 'error');
                    }
                } else {
                    const errorMessage = response.ok
                        ? '❌ Erreur: Le serveur n\'a pas renvoyé la bonne réponse.'
                        : '❌ Erreur: Le serveur a renvoyé une erreur (code ' + response.status + ').';
                    showCommentToast(errorMessage, 'error');
                }

            } catch (error) {
                showCommentToast('❌ Une erreur est survenue. Veuillez réessayer.', 'error');
            } finally {
                enableForm(button, textarea, originalButtonText);
            }
        });
    });
}

function disableForm(button, textarea) {
    button.disabled = true;
    textarea.disabled = true;
    button.textContent = 'Publication...';
    button.style.opacity = '0.5';
}

function enableForm(button, textarea, originalButtonText) {
    button.disabled = false;
    textarea.disabled = false;
    button.textContent = originalButtonText;
    button.style.opacity = '1';
}

function incrementCommentCounter(form) {
    const commentSection = form.closest('.border-t');
    if (commentSection) {
        const counterSpan = commentSection.querySelector('h4 span');
        if (counterSpan) {
            const currentCount = parseInt(counterSpan.textContent) || 0;
            counterSpan.textContent = currentCount + 1;
        }
    }
}

function showCommentToast(message, type = 'success') {
    const toast = document.createElement('div');
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

    toast.innerHTML = message;
    document.body.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), TOAST_FADE_DURATION);
    }, TOAST_DISPLAY_DURATION);
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initCommentForms);
} else {
    initCommentForms();
}
