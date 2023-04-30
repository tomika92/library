-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Wersja serwera:               8.0.32 - MySQL Community Server - GPL
-- Serwer OS:                    Win64
-- HeidiSQL Wersja:              12.3.0.6589
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Zrzut struktury bazy danych library
CREATE DATABASE IF NOT EXISTS `library` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `library`;

-- Zrzut struktury tabela library.collection
CREATE TABLE IF NOT EXISTS `collection` (
  `ID_zbior` int unsigned NOT NULL AUTO_INCREMENT,
  `type` enum('BOOK','MAGAZINE','FILM','AUDIOBOOK') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `title` varchar(100) NOT NULL,
  `mag_number` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `author` varchar(100) DEFAULT NULL,
  `year` int DEFAULT NULL,
  `publisher` varchar(100) NOT NULL,
  `genre` varchar(100) DEFAULT NULL,
  `quantity` int NOT NULL DEFAULT '0',
  `time` int DEFAULT NULL,
  PRIMARY KEY (`ID_zbior`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Eksport danych został odznaczony.

-- Zrzut struktury tabela library.rentals
CREATE TABLE IF NOT EXISTS `rentals` (
  `ID_rentals` int unsigned NOT NULL AUTO_INCREMENT,
  `ID_zbior` int unsigned NOT NULL,
  `ID_user` int unsigned NOT NULL,
  `start_date` datetime DEFAULT NULL,
  `order_end` datetime DEFAULT NULL,
  `to_pick_up_end` datetime DEFAULT NULL,
  `rented_end` datetime DEFAULT NULL,
  `returned_date` datetime DEFAULT NULL,
  `status` enum('ordered','to_pick_up','rented','returned','canceled') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'ordered',
  PRIMARY KEY (`ID_rentals`),
  KEY `ID_zbior` (`ID_zbior`),
  KEY `rentals_ko2` (`ID_user`),
  CONSTRAINT `rentals_ko` FOREIGN KEY (`ID_zbior`) REFERENCES `collection` (`ID_zbior`),
  CONSTRAINT `rentals_ko2` FOREIGN KEY (`ID_user`) REFERENCES `users` (`ID_user`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Eksport danych został odznaczony.

-- Zrzut struktury tabela library.users
CREATE TABLE IF NOT EXISTS `users` (
  `ID_user` int unsigned NOT NULL AUTO_INCREMENT,
  `login` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `books_nr` int unsigned NOT NULL DEFAULT '10',
  `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'client',
  PRIMARY KEY (`ID_user`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Eksport danych został odznaczony.

-- Zrzut struktury wyzwalacz library.rentals_insert
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `rentals_insert` BEFORE INSERT ON `rentals` FOR EACH ROW BEGIN
SET NEW.start_date = NOW();
SET NEW.order_end = DATE_ADD(NEW.start_date, INTERVAL 2 DAY);
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
