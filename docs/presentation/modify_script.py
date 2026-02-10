#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import re

# Lire le fichier
with open('generate_ultra_final_presentation_backup.py', 'r', encoding='utf-8') as f:
    lines = f.readlines()

# Écrire le nouveau fichier avec les modifications
output = []
i = 0
while i < len(lines):
    line = lines[i]
    
    # Modification 1: En-tête
    if 'PRÉSENTATION ULTRA-FINALE - 64 SLIDES' in line:
        output.append(line.replace('64 SLIDES', '66 SLIDES'))
        i += 1
        continue
    
    # Modification 2: Ajouter ligne Repository JPA dans l'en-tête
    if line.strip() == '- 3 slides CODE Spring Boot':
        output.append(line)
        i += 1
        output.append('- 1 slide CODE Repository JPA\n')
        continue
    
    # Modification 3: Ajouter ligne Diagramme de séquence
    if '- 2 slides CODE Tests (unitaire + intégration)' in line:
        output.append(line.replace(' (unitaire + intégration)', ''))
        output.append('- 1 slide Diagramme de séquence\n')
        i += 1
        continue
    
    # Modification 4: Changer 64 slides en 66 slides
    if 'Génération présentation ULTRA-FINALE - 64 slides' in line:
        output.append(line.replace('64 slides', '66 slides'))
        i += 1
        continue
    
    if 'Generation de la presentation ULTRA-FINALE (64 slides)' in line:
        output.append(line.replace('(64 slides)', '(66 slides)'))
        i += 1
        continue
    
    # Modification 5: Ajouter fonction add_sequence_diagram avant create_api_rest_slide
    if 'def create_api_rest_slide(prs):' in line:
        # Insérer la fonction add_sequence_diagram
        output.append('''def add_sequence_diagram(slide):
    """Diagramme de séquence - Inscription utilisateur"""
    title_box = slide.shapes.add_textbox(Inches(1.5), Inches(0.4), Inches(7), Inches(0.4))
    title_frame = title_box.text_frame
    title_frame.text = "Inscription utilisateur - Diagramme de sequence"
    set_all_paragraphs_black(title_frame, Pt(18), bold=True)

    # Positions des acteurs (boîtes verticales)
    actors_x = [0.5, 2.1, 3.7, 5.3, 6.9]
    actor_width = 1.4
    actor_names = ["Navigateur", "Registration\nController", "UserService", "UserRepository", "Base de\ndonnees"]
    actor_colors = [light_blue, light_green, light_orange, light_purple, light_gray]
    actor_border_colors = [dark_blue, dark_green, dark_orange, dark_purple, dark_gray]

    # Dessiner les boîtes d'acteurs
    for i, (x, name, bg_color, border_color) in enumerate(zip(actors_x, actor_names, actor_colors, actor_border_colors)):
        box = slide.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(x), Inches(1), Inches(actor_width), Inches(0.5))
        box.fill.solid()
        box.fill.fore_color.rgb = bg_color
        box.line.color.rgb = border_color
        box.line.width = Pt(1.5)
        text = box.text_frame
        text.text = name
        for para in text.paragraphs:
            para.font.size = Pt(8)
            para.font.bold = True
            para.font.color.rgb = BLACK
            para.alignment = PP_ALIGN.CENTER

    # Lignes de vie verticales
    for x in actors_x:
        line = slide.shapes.add_connector(1, Inches(x + actor_width/2), Inches(1.5), Inches(x + actor_width/2), Inches(4.2))
        line.line.color.rgb = dark_gray
        line.line.width = Pt(1)
        line.line.dash_style = 2

    # Messages (flèches horizontales)
    messages = [
        (0, 1, "POST /register", 1.7),
        (1, 2, "createUser(userDTO)", 2.0),
        (2, 3, "save(user)", 2.3),
        (3, 4, "INSERT INTO users", 2.6),
        (4, 3, "User saved", 2.9),
        (3, 2, "User entity", 3.2),
        (2, 1, "Success", 3.5),
        (1, 0, "Redirect /login", 3.8),
    ]

    for from_idx, to_idx, message, y_pos in messages:
        from_x = actors_x[from_idx] + actor_width/2
        to_x = actors_x[to_idx] + actor_width/2
        line = slide.shapes.add_connector(1, Inches(from_x), Inches(y_pos), Inches(to_x), Inches(y_pos))
        line.line.color.rgb = BLACK
        line.line.width = Pt(1.5)
        text_x = min(from_x, to_x) + abs(to_x - from_x)/2 - 0.5
        text_box = slide.shapes.add_textbox(Inches(text_x), Inches(y_pos - 0.2), Inches(1), Inches(0.15))
        text_frame = text_box.text_frame
        text_frame.text = message
        for para in text_frame.paragraphs:
            para.font.size = Pt(7)
            para.font.color.rgb = BLACK
            para.alignment = PP_ALIGN.CENTER

''')
        output.append(line)
        i += 1
        continue
    
    output.append(line)
    i += 1

# Sauvegarder
with open('generate_ultra_final_presentation_modified.py', 'w', encoding='utf-8') as f:
    f.writelines(output)

print('[OK] Fichier modifié créé: generate_ultra_final_presentation_modified.py')
