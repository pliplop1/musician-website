-- --------------------------------------------------------
-- Hôte:                         127.0.0.1
-- Version du serveur:           11.7.2-MariaDB - mariadb.org binary distribution
-- SE du serveur:                Win64
-- HeidiSQL Version:             12.11.0.7065
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Listage des données de la table musician_db.article : ~1 rows (environ)
INSERT INTO `article` (`id`, `contenu`, `date_publication`, `titre`) VALUES
	(1, 'teste de l\'actu', '2025-10-02 21:56:17.468661', 'actu');

-- Listage de la structure de table musician_db. concert
CREATE TABLE IF NOT EXISTS `concert` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date` date DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Listage des données de la table musician_db.concert : ~2 rows (environ)
INSERT INTO `concert` (`id`, `date`, `description`, `location`) VALUES
	(17, '2025-11-08', 'Le concert a été supprimé avec succès !', 'amiens '),
	(18, '2025-10-31', 'sryksssssssssssssssssssssssssssss', 'abbeville');

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

-- Listage de la structure de table musician_db. users
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Listage des données de la table musician_db.users : ~5 rows (environ)
INSERT INTO `users` (`id`, `email`, `password`, `username`) VALUES
	(6, 'tutu@test.com', '$2a$10$5c0BSrxOpV0bVO818EwLT.mLdUlXvWm8PMk7k7WpIoc4/4jE3yBuG', 'tutu'),
	(7, 'tatatutu@email.com', '$2a$10$Mt36HwpOiMt66a81Cvr8ju015zHJoURANFC85FfqFpJqoC3WFaUnu', 'tatatutu'),
	(9, 'admin@test.com', '$2a$10$wpQaeVHO0/YuFWC/tWl4Ney6a2xCiVM4VccB9y2i2xVt/I.ObqPZS', 'admin'),
	(10, 'testeur@email.com', '$2a$10$iReO459ZDx/xEm8gFia5DenqOZmf7Vz5HheRoI4INI9nq3MvNHbn6', 'testuser'),
	(11, 'amiens@amiens.com', '$2a$10$FL6lRT.XUC74aVq03h0sZuAdh/y2GTGPdRgsoAtf.zJckZBQH6Fay', 'amiens');

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
	(9, 2),
	(10, 1),
	(11, 1);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
