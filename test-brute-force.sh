#!/bin/bash

echo "=========================================="
echo "TEST SYSTÈME ANTI-BRUTE FORCE"
echo "=========================================="
echo ""

BASE_URL="http://localhost:8106"
USERNAME="testuser"
WRONG_PASSWORD="wrongpassword123"
CORRECT_PASSWORD="password"

# Fonction pour faire une tentative de connexion
attempt_login() {
    local attempt_num=$1
    local password=$2
    local description=$3

    echo "----------------------------------------"
    echo "Tentative #$attempt_num: $description"
    echo "----------------------------------------"

    # Récupérer le token CSRF et les cookies
    CSRF_TOKEN=$(curl -s $BASE_URL/login -c cookies_$attempt_num.txt | grep '_csrf' | grep -oP 'value="\K[^"]+' | head -1)

    if [ -z "$CSRF_TOKEN" ]; then
        echo "❌ Erreur: Impossible de récupérer le token CSRF"
        return 1
    fi

    echo "📝 CSRF Token: ${CSRF_TOKEN:0:20}..."

    # Tentative de connexion
    RESPONSE=$(curl -s -L -b cookies_$attempt_num.txt -c cookies_$attempt_num.txt \
        -X POST "$BASE_URL/login" \
        -d "username=$USERNAME" \
        -d "password=$password" \
        -d "_csrf=$CSRF_TOKEN" \
        -w "\nHTTP_CODE:%{http_code}\nREDIRECT:%{url_effective}")

    HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d':' -f2)
    REDIRECT_URL=$(echo "$RESPONSE" | grep "REDIRECT:" | cut -d':' -f2-)

    echo "🔍 Code HTTP: $HTTP_CODE"
    echo "🔗 Redirection: $REDIRECT_URL"

    # Analyser le résultat
    if echo "$REDIRECT_URL" | grep -q "error=true"; then
        echo "❌ Échec de connexion (identifiants invalides)"
    elif echo "$REDIRECT_URL" | grep -q "blocked=true"; then
        echo "🚫 COMPTE BLOQUÉ - Protection anti-brute force activée!"
    elif echo "$REDIRECT_URL" | grep -q "/user/profile\|/admin/dashboard"; then
        echo "✅ Connexion RÉUSSIE!"
    else
        echo "❓ Statut inconnu"
    fi

    echo ""
    sleep 1
}

# Test 1-5: Tentatives échouées
echo "🔴 PHASE 1: Simulation d'attaque par force brute"
echo "================================================"
echo ""

for i in {1..5}; do
    attempt_login $i "$WRONG_PASSWORD" "Mot de passe incorrect"
done

echo ""
echo "🔴 PHASE 2: Tentative après blocage"
echo "================================================"
echo ""

attempt_login 6 "$WRONG_PASSWORD" "Tentative sur compte bloqué"

echo ""
echo "=========================================="
echo "VÉRIFICATION DES LOGS SERVEUR"
echo "=========================================="
echo ""
echo "Consultez les logs Spring Boot pour voir:"
echo "- ✅ Enregistrement des tentatives échouées"
echo "- ⚠️  Messages d'alerte de sécurité"
echo "- 🚫 Blocage du compte après 5 tentatives"
echo ""

# Nettoyage
rm -f cookies_*.txt

echo "Test terminé. Le compte restera bloqué pendant 15 minutes."
echo "Pour tester une connexion réussie, attendez 15 minutes ou supprimez les entrées de la table login_attempts."
