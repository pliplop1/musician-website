/**
 * Focus Trap - Piège le focus clavier dans un élément modal pour RGAA 12.7
 * Permet la navigation au clavier uniquement dans les éléments interactifs du modal
 */

class FocusTrap {
    constructor(element) {
        this.element = element;
        this.focusableElements = null;
        this.firstFocusable = null;
        this.lastFocusable = null;
        this.previouslyFocused = null;
        this.handleKeyDown = this.handleKeyDown.bind(this);
    }

    /**
     * Récupère tous les éléments focusables dans le modal
     */
    getFocusableElements() {
        const focusableSelectors = [
            'a[href]',
            'button:not([disabled])',
            'textarea:not([disabled])',
            'input:not([disabled])',
            'select:not([disabled])',
            '[tabindex]:not([tabindex="-1"])'
        ].join(', ');

        return Array.from(this.element.querySelectorAll(focusableSelectors))
            .filter(el => !el.hasAttribute('disabled') && el.offsetParent !== null);
    }

    /**
     * Active le focus trap
     */
    activate() {
        // Sauvegarder l'élément actuellement focusé
        this.previouslyFocused = document.activeElement;

        // Récupérer les éléments focusables
        this.focusableElements = this.getFocusableElements();

        if (this.focusableElements.length === 0) {
            return;
        }

        this.firstFocusable = this.focusableElements[0];
        this.lastFocusable = this.focusableElements[this.focusableElements.length - 1];

        // Ajouter l'écouteur d'événement
        this.element.addEventListener('keydown', this.handleKeyDown);

        // Focaliser le premier élément
        this.firstFocusable.focus();
    }

    /**
     * Désactive le focus trap
     */
    deactivate() {
        this.element.removeEventListener('keydown', this.handleKeyDown);

        // Restaurer le focus sur l'élément précédent
        if (this.previouslyFocused && this.previouslyFocused.focus) {
            this.previouslyFocused.focus();
        }
    }

    /**
     * Gère la navigation au clavier
     */
    handleKeyDown(e) {
        if (e.key !== 'Tab') {
            return;
        }

        // Si Shift + Tab (navigation arrière)
        if (e.shiftKey) {
            if (document.activeElement === this.firstFocusable) {
                e.preventDefault();
                this.lastFocusable.focus();
            }
        } else {
            // Tab seul (navigation avant)
            if (document.activeElement === this.lastFocusable) {
                e.preventDefault();
                this.firstFocusable.focus();
            }
        }
    }
}

/**
 * Fonction helper pour créer et gérer un focus trap facilement
 */
window.createFocusTrap = function(element) {
    return new FocusTrap(element);
};
