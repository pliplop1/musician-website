-- --------------------------------------------------------
-- Hôte:                         127.0.0.1
-- Version du serveur:           11.7.2-MariaDB - mariadb.org binary distribution
-- SE du serveur:                Win64
-- HeidiSQL Version:             12.12.0.7122
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Listage de la structure de la base pour musician_db
CREATE DATABASE IF NOT EXISTS `musician_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;
USE `musician_db`;

-- Listage de la structure de table musician_db. article
CREATE TABLE IF NOT EXISTS `article` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `contenu` text DEFAULT NULL,
  `date_publication` datetime(6) DEFAULT NULL,
  `titre` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Listage des données de la table musician_db.article : ~2 rows (environ)
INSERT INTO `article` (`id`, `contenu`, `date_publication`, `titre`) VALUES
	(1, 'teste de l\'actu', '2025-10-02 21:56:17.468661', 'actu'),
	(3, 'tribunal•Ce lundi, l’amant de Delphine, avec qui elle prévoyait de refaire sa vie, a été longuement entendu. Les avocats de Cédric Jubillar, qui poursuivent leur travail de sape de l’enquête, l’ont accusé de s’être trouvé à Cagnac-les-Mines la nuit fatidique\r\n', '2025-10-06 19:13:37.933866', 'nouvelle actualisté');

-- Listage de la structure de table musician_db. biography
CREATE TABLE IF NOT EXISTS `biography` (
  `id` bigint(20) NOT NULL,
  `content` text DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Listage des données de la table musician_db.biography : ~1 rows (environ)
INSERT INTO `biography` (`id`, `content`) VALUES
	(1, 'L\'essentiel\r\nJusqu’au 17 octobre, la cour d’assises du Tarn juge Cédric Jubillar pour le meurtre de sa femme Delphine, disparue sans laisser de trace dans la nuit du 15 au 16 décembre 2020.\r\nCe lundi, Donat-Jean M., l’homme pour lequel Delphine voulait quitter son mari, a vécu une audition difficile, pilonné par les avocats de Cédric Jubillar.\r\nLa défense n’insinue plus simplement que la piste de « l’amant de Montauban » n’a pas été assez explorée, elle accuse, sur la base de données de téléphonies complexes, Donat-Jean M. de s’être trouvé à Cagnac-les-Mines la nuit de la disparition. Vrai rebondissement ou stratégie du chaos ? La cour pourrait demander à des gendarmes de revenir à la barre, voire demander une nouvelle expertise sur la téléphonie.');

-- Listage de la structure de table musician_db. concert
CREATE TABLE IF NOT EXISTS `concert` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date` date DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Listage des données de la table musician_db.concert : ~3 rows (environ)
INSERT INTO `concert` (`id`, `date`, `description`, `location`) VALUES
	(17, '2025-11-08', 'Le concert a été supprimé avec succès !', 'amiens '),
	(18, '2025-10-31', 'sryksssssssssssssssssssssssssssss', 'abbeville'),
	(19, '2025-10-30', 'famille et se frotte les mains. Il sait déjà que son « rival », celui ', 'BRBqrbeerb');

-- Listage de la structure de table musician_db. message
CREATE TABLE IF NOT EXISTS `message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `timestamp` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Listage des données de la table musician_db.message : ~2 rows (environ)
INSERT INTO `message` (`id`, `content`, `email`, `name`, `timestamp`) VALUES
	(6, 'bouo csdomv vsdmov', 'tiyir@email.com', 'g,sfg,', '2025-09-30 21:26:13.746095'),
	(7, 'Le concert a été supprimé avec succès !', 'testeur@email.com', 'minet', '2025-10-02 20:53:15.552704');

-- Listage de la structure de table musician_db. photo
CREATE TABLE IF NOT EXISTS `photo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `filename` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Listage des données de la table musician_db.photo : ~1 rows (environ)
INSERT INTO `photo` (`id`, `filename`) VALUES
	(1, 'd4ece11c-4166-4eb2-b02c-b82c1f8697b9.png');

-- Listage de la structure de table musician_db. roles
CREATE TABLE IF NOT EXISTS `roles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Listage des données de la table musician_db.roles : ~2 rows (environ)
INSERT INTO `roles` (`id`, `name`) VALUES
	(1, 'ROLE_USER'),
	(2, 'ROLE_ADMIN');

-- Listage de la structure de table musician_db. track
CREATE TABLE IF NOT EXISTS `track` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `embed_code` text DEFAULT NULL,
  `filename` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `track_type` enum('EMBED','UPLOADED_FILE') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Listage des données de la table musician_db.track : ~2 rows (environ)
INSERT INTO `track` (`id`, `embed_code`, `filename`, `title`, `track_type`) VALUES
	(3, NULL, '6ff58a1e-84c1-4386-9fc3-6cf359b0efb5_SoundHelix-Song-1.mp3', 'test', 'UPLOADED_FILE'),
	(4, '<iframe width="100%" height="166" scrolling="no" frameborder="no" allow="autoplay" src="https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/1118333332&color=%23ff5500&auto_play=false&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true"></iframe>', NULL, 'son1', 'EMBED');

-- Listage de la structure de table musician_db. users
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Listage des données de la table musician_db.users : ~5 rows (environ)
INSERT INTO `users` (`id`, `email`, `password`, `username`) VALUES
	(6, 'tutu@test.com', '$2a$10$5c0BSrxOpV0bVO818EwLT.mLdUlXvWm8PMk7k7WpIoc4/4jE3yBuG', 'tutu'),
	(7, 'tatatutu@email.com', '$2a$10$Mt36HwpOiMt66a81Cvr8ju015zHJoURANFC85FfqFpJqoC3WFaUnu', 'tatatutu'),
	(10, 'testeur@email.com', '$2a$10$iReO459ZDx/xEm8gFia5DenqOZmf7Vz5HheRoI4INI9nq3MvNHbn6', 'testuser'),
	(11, 'amiens@amiens.com', '$2a$10$GsaC8oGFgccDJdbFI3/ch.hQE8DK36JO4D8FnARk7NmsDex6MwBr2', 'amiens'),
	(14, 'admin@test.com', '$2a$10$GaSvGydaVW5RKa5.bfj3aulFc0dBzdh9K.b8ZvRgOnDUiPj8r4gwu', 'admin');

-- Listage de la structure de table musician_db. user_favorite_concerts
CREATE TABLE IF NOT EXISTS `user_favorite_concerts` (
  `user_id` bigint(20) NOT NULL,
  `concert_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`concert_id`),
  KEY `FKhoqeg9igj7h6y6shqaqr3tnf1` (`concert_id`),
  CONSTRAINT `FKhoqeg9igj7h6y6shqaqr3tnf1` FOREIGN KEY (`concert_id`) REFERENCES `concert` (`id`),
  CONSTRAINT `FKm7i6bql05127htqmjw6uprm7p` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Listage des données de la table musician_db.user_favorite_concerts : ~0 rows (environ)

-- Listage de la structure de table musician_db. user_roles
CREATE TABLE IF NOT EXISTS `user_roles` (
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `FKrhfovtciq1l558cw6udg0h0d3` (`role_id`),
  CONSTRAINT `FKh8ciramu9cc9q3qcqiv4ue8a6` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`),
  CONSTRAINT `FKhfh9dx7w3ubf1co1vdev94g3f` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Listage des données de la table musician_db.user_roles : ~5 rows (environ)
INSERT INTO `user_roles` (`user_id`, `role_id`) VALUES
	(6, 1),
	(7, 1),
	(10, 1),
	(11, 1),
	(14, 2);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
