-- Script pour corriger les URLs Spotify invalides avec de vrais liens
-- Date: 2025-10-10

-- Canon in D (ID: 13)
UPDATE track
SET embed_code = 'https://open.spotify.com/embed/track/3yxglbUxGmFJcoobdKuUeZ',
    track_type = 'EMBED'
WHERE id = 13;

-- Symphony No. 5 (ID: 14)
UPDATE track
SET embed_code = 'https://open.spotify.com/embed/track/1GEJlRMSJV5ejmZk2sQPa1',
    track_type = 'EMBED'
WHERE id = 14;

-- Moonlight Sonata (ID: 6)
UPDATE track
SET embed_code = 'https://open.spotify.com/embed/track/5D25oVmR2kDYBJ3GUwC7OU',
    track_type = 'EMBED'
WHERE id = 6;

-- Clair de Lune (Debussy) (ID: 7)
UPDATE track
SET embed_code = 'https://open.spotify.com/embed/track/2PiFEWHFrqNTf1iL09HunW',
    track_type = 'EMBED'
WHERE id = 7;

-- Für Elise (ID: 8)
UPDATE track
SET embed_code = 'https://open.spotify.com/embed/track/3K7JMwT2KK5zwRPxwFPBa4',
    track_type = 'EMBED'
WHERE id = 8;

-- Nocturne Op. 9 No. 2 (Chopin) (ID: 9)
UPDATE track
SET embed_code = 'https://open.spotify.com/embed/track/1prRGLfJDdVSPZULjKd1Rz',
    track_type = 'EMBED'
WHERE id = 9;

-- Gymnopédie No. 1 (Satie) (ID: 10)
UPDATE track
SET embed_code = 'https://open.spotify.com/embed/track/3qyyyvxhJKSF0sSTW0r4oK',
    track_type = 'EMBED'
WHERE id = 10;

-- Prelude in C Major (Bach) (ID: 11)
UPDATE track
SET embed_code = 'https://open.spotify.com/embed/track/5ZwzbzC9MuR1lVwJt4H5oE',
    track_type = 'EMBED'
WHERE id = 11;

-- The Four Seasons - Spring (Vivaldi) (ID: 12)
UPDATE track
SET embed_code = 'https://open.spotify.com/embed/track/7MiKXlYMNB9b0TQmYSzLTH',
    track_type = 'EMBED'
WHERE id = 12;

-- Rhapsody in Blue (Gershwin) (ID: 15)
UPDATE track
SET embed_code = 'https://open.spotify.com/embed/track/5p6F8RgKGdqJlWYzIgz2fR',
    track_type = 'EMBED'
WHERE id = 15;

-- Bolero (Ravel) (ID: 16)
UPDATE track
SET embed_code = 'https://open.spotify.com/embed/track/6ZFbXIJkuI1dVNWvzJzown',
    track_type = 'EMBED'
WHERE id = 16;

-- Ave Maria (Schubert) (ID: 17)
UPDATE track
SET embed_code = 'https://open.spotify.com/embed/track/2OGlDfNiPKkPvhMWz4P7FP',
    track_type = 'EMBED'
WHERE id = 17;

-- Vérification
SELECT id, title, track_type, embed_code FROM track WHERE track_type = 'EMBED' ORDER BY id;
