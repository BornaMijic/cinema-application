/*
SQLyog Community v13.1.9 (64 bit)
MySQL - 8.0.31 : Database - cinema
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`cinema` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `cinema`;

/*Table structure for table `cinema_halls` */

DROP TABLE IF EXISTS `cinema_halls`;

CREATE TABLE `cinema_halls` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `hall_name` varchar(100) NOT NULL,
  `grid_rows_number` bigint NOT NULL,
  `grid_columns_number` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `hall_name` (`hall_name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `cinema_halls` */

insert  into `cinema_halls`(`id`,`hall_name`,`grid_rows_number`,`grid_columns_number`) values 
(1,'DVORANA-A',5,8),
(2,'DVORANA-B',5,7),
(3,'DVORANA-C',1,10),
(5,'DVORANA-D',5,7);

/*Table structure for table `events` */

DROP TABLE IF EXISTS `events`;

CREATE TABLE `events` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `start_date` timestamp NOT NULL,
  `price` double NOT NULL,
  `movie_id` bigint NOT NULL,
  `cinema_hall_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `cinema_hall_id` (`cinema_hall_id`),
  KEY `movie_id` (`movie_id`),
  CONSTRAINT `events_ibfk_1` FOREIGN KEY (`cinema_hall_id`) REFERENCES `cinema_halls` (`id`) ON DELETE CASCADE,
  CONSTRAINT `events_ibfk_2` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `events` */

insert  into `events`(`id`,`start_date`,`price`,`movie_id`,`cinema_hall_id`) values 
(1,'2023-02-15 18:06:00',2,11,1),
(2,'2023-02-09 07:11:00',3,10,2),
(3,'2023-02-17 08:11:00',3,9,3),
(4,'2023-01-19 05:08:00',3,8,5),
(5,'2023-07-13 03:13:00',2,7,5),
(6,'2023-02-24 06:07:00',3,7,2),
(7,'2023-02-09 14:11:00',2,5,2),
(8,'2023-02-08 00:00:00',2,2,5),
(9,'2023-02-23 03:12:00',2,1,5);

/*Table structure for table `movies` */

DROP TABLE IF EXISTS `movies`;

CREATE TABLE `movies` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL,
  `poster_name` varchar(255) NOT NULL,
  `summary` text NOT NULL,
  `duration` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `movies` */

insert  into `movies`(`id`,`title`,`poster_name`,`summary`,`duration`) values 
(1,'Avatar: Put vode','Avatar Put vode.jpg','Jake Sully živi s svojom obitelji na planetu Pandora. Gdje će se poznata prijetnja vratiti.',192),
(2,'Nož u leđa','Nož u leđa.jpg','Detektiv  istražuje ubojicu između članova obitelji.',130),
(3,'Harry Potter i relikvije smrti: 2. dio','Harry Potter i relikvije smrti 2. dio.jpg','Harry Potter-ov završni okršaj protiv Voldemorta.',130),
(4,'Ace Ventura: Detektiv kućnih ljubimaca','Ace Ventura Detektiv kućnih ljubimaca.jpg','Detektiv koji istražuje nestanak ljubimaca, dobiva bitan slučaj u kojem mora naći maskotu nogometnog tima.',86),
(5,'Dobri momci','Dobri momci.jpg','U filmu se priča o Henryju Hillu, njegovom životu, gdje pokriva radnju gdje uđe u mafiju.',145),
(6,'Osvetnici: Kraj igre','Osvetnici Kraj igre.jpg','Dolazi do ukupljanja osvetnika kako bi pobjedili Thanosa.',181),
(7,'Čuvari galaksije','Čuvari galaksije.jpg','Galaktički kriminalci se ukupljaju kako bi spasili svemir',105),
(8,'Fabelmanovi','Fabelmanovi.jpg','Priča o dječaku koji želi postati filmski redatelj',151),
(9,'Harry Potter i kamen mudraca','Harry Potter i kamen mudraca.jpg','Harry Potter je dječak koji je izgubio roditelje u ranoj dobi saznaje da su mu roditelji bili čarobnjaci, te upisuje se u školu čarobnjaštva',152),
(10,'Fantastične zvijeri i gdje ih pronaći','Fantastične zvijeri i gdje ih pronaći.jpg','Priča smještena 70 godina prije Harry Pottera priča priču o Newtu Scamanderu',132),
(11,'Dobri Will Hunting','Dobri Will Hunting.jpg','Mladi domar ima dar za matematiku, koju prepozna psiholog, te ga pomaže usmjeriti',126);

/*Table structure for table `reservation_seats` */

DROP TABLE IF EXISTS `reservation_seats`;

CREATE TABLE `reservation_seats` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `seat_id` bigint NOT NULL,
  `reservation_id` bigint NOT NULL,
  `event_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `UC_unique_reservation` (`seat_id`,`event_id`),
  KEY `reservation_id` (`reservation_id`),
  KEY `event_id` (`event_id`),
  CONSTRAINT `reservation_seats_ibfk_1` FOREIGN KEY (`reservation_id`) REFERENCES `reservations` (`id`) ON DELETE CASCADE,
  CONSTRAINT `reservation_seats_ibfk_2` FOREIGN KEY (`seat_id`) REFERENCES `seats` (`id`) ON DELETE CASCADE,
  CONSTRAINT `reservation_seats_ibfk_3` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `reservation_seats` */

insert  into `reservation_seats`(`id`,`seat_id`,`reservation_id`,`event_id`) values 
(1,65,1,9),
(2,66,1,9),
(3,63,2,8),
(4,64,2,8),
(5,65,2,8),
(6,66,2,8),
(7,78,3,8),
(8,79,3,8),
(9,56,4,3),
(10,57,4,3),
(11,58,4,3),
(12,44,5,7),
(13,45,5,7),
(14,52,6,3),
(15,53,6,3),
(16,42,7,7);

/*Table structure for table `reservations` */

DROP TABLE IF EXISTS `reservations`;

CREATE TABLE `reservations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `reservation_time` timestamp NOT NULL,
  `user_id` bigint DEFAULT NULL,
  `event_id` bigint NOT NULL,
  `complete` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `user_id` (`user_id`),
  KEY `event_id` (`event_id`),
  CONSTRAINT `reservations_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `reservations_ibfk_2` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `reservations` */

insert  into `reservations`(`id`,`reservation_time`,`user_id`,`event_id`,`complete`) values 
(1,'2023-02-03 07:51:35',1,9,1),
(2,'2023-02-03 07:53:34',1,8,1),
(3,'2023-02-03 07:54:31',1,8,1),
(4,'2023-02-03 07:54:52',1,3,1),
(5,'2023-02-03 07:55:21',2,7,1),
(6,'2023-02-03 07:55:50',2,3,1),
(7,'2023-02-03 07:56:06',2,7,1);

/*Table structure for table `roles` */

DROP TABLE IF EXISTS `roles`;

CREATE TABLE `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `roles` */

insert  into `roles`(`id`,`name`) values 
(2,'ROLE_ADMIN'),
(1,'ROLE_USER');

/*Table structure for table `seats` */

DROP TABLE IF EXISTS `seats`;

CREATE TABLE `seats` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `seat_number` bigint NOT NULL,
  `hall_row_number` bigint NOT NULL,
  `grid_position` bigint NOT NULL,
  `cinema_hall_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `UC_grid` (`grid_position`,`cinema_hall_id`),
  UNIQUE KEY `UC_seat_grid` (`seat_number`,`hall_row_number`,`grid_position`,`cinema_hall_id`),
  UNIQUE KEY `UC_seat_row_number` (`seat_number`,`hall_row_number`,`cinema_hall_id`),
  KEY `cinema_hall_id` (`cinema_hall_id`),
  CONSTRAINT `seats_ibfk_1` FOREIGN KEY (`cinema_hall_id`) REFERENCES `cinema_halls` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `seats` */

insert  into `seats`(`id`,`seat_number`,`hall_row_number`,`grid_position`,`cinema_hall_id`) values 
(52,1,1,1,3),
(1,1,1,2,1),
(62,1,1,8,5),
(37,1,1,9,2),
(7,1,2,9,1),
(69,1,2,15,5),
(42,1,2,23,2),
(15,1,3,17,1),
(76,1,3,22,5),
(47,1,3,30,2),
(23,1,4,25,1),
(31,1,5,34,1),
(53,2,1,2,3),
(2,2,1,3,1),
(63,2,1,9,5),
(38,2,1,10,2),
(8,2,2,10,1),
(70,2,2,16,5),
(43,2,2,24,2),
(16,2,3,18,1),
(77,2,3,23,5),
(48,2,3,31,2),
(24,2,4,26,1),
(32,2,5,35,1),
(54,3,1,3,3),
(3,3,1,4,1),
(64,3,1,10,5),
(39,3,1,11,2),
(9,3,2,11,1),
(71,3,2,17,5),
(44,3,2,25,2),
(17,3,3,19,1),
(78,3,3,24,5),
(49,3,3,32,2),
(25,3,4,27,1),
(33,3,5,36,1),
(55,4,1,4,3),
(4,4,1,5,1),
(65,4,1,11,5),
(40,4,1,12,2),
(10,4,2,12,1),
(72,4,2,18,5),
(45,4,2,26,2),
(18,4,3,20,1),
(79,4,3,25,5),
(50,4,3,33,2),
(26,4,4,28,1),
(34,4,5,37,1),
(56,5,1,5,3),
(5,5,1,6,1),
(66,5,1,12,5),
(41,5,1,13,2),
(11,5,2,13,1),
(73,5,2,19,5),
(46,5,2,27,2),
(19,5,3,21,1),
(80,5,3,26,5),
(51,5,3,34,2),
(27,5,4,29,1),
(35,5,5,38,1),
(57,6,1,6,3),
(6,6,1,7,1),
(67,6,1,13,5),
(12,6,2,14,1),
(74,6,2,20,5),
(20,6,3,22,1),
(81,6,3,27,5),
(28,6,4,30,1),
(36,6,5,39,1),
(58,7,1,7,3),
(68,7,1,14,5),
(13,7,2,15,1),
(75,7,2,21,5),
(21,7,3,23,1),
(82,7,3,28,5),
(29,7,4,31,1),
(59,8,1,8,3),
(14,8,2,16,1),
(22,8,3,24,1),
(30,8,4,32,1),
(60,9,1,9,3),
(61,10,1,10,3);

/*Table structure for table `users` */

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(30) NOT NULL,
  `name` varchar(60) NOT NULL,
  `surname` varchar(60) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(60) NOT NULL,
  `code_to_verify` varchar(255) NOT NULL,
  `verified` tinyint(1) NOT NULL,
  `active` tinyint(1) NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `UC_user` (`username`,`code_to_verify`),
  KEY `role_id` (`role_id`),
  CONSTRAINT `users_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `users` */

insert  into `users`(`id`,`username`,`name`,`surname`,`password`,`email`,`code_to_verify`,`verified`,`active`,`role_id`) values 
(1,'admin12','Luka','Lukic','$2a$12$uSiwEIHa4M2RXHAFBn68teb9UZU/onOucOSxYWTCVgeoBfB3eVExC','moderator.kinoapp2023@gmail.com','r0e25AwOxtulXv1D1nqp4yU9fwlQaQ2vtNNu3xPpApvWkyco6nwONMoTViSRxHWXdMSynJ0kYHWmXdkESJjgqCV9ZPmYxmRfvJol8A6MQDnSzLg3r4zcgMOnP81JuUyp2yOkGiXQySI1DBe34lR3gavPJ2fu00ToIRyLIpbfomIxiBQvVZcHLM74RQWpmoGxat6PdxFZGRARnPpbYq0des61ZkmEYsOJJ5OP2NZKgJSmonH3BTxYmeKrNJzD8zo',1,1,2),
(2,'user12','Jakov','Jakic','$2a$12$2fyNO6vCtN35kZXrwmRTv.wdfs4buG/5.7cY7NXtKacI2gnAcuw0e','korisnik.kinoapp2023@gmail.com','Xk1Or2l44XTKJ4mXgmN9C5inFJGmHcoBqNCh2qlMFjpOG9g8c5PfcQlUe6h9OBAx21uISu63bkkHhSx7ZdfbQpncxnNYyDE14Cy8bFCcro6zc48Mqm00YNtY1yYQw2J9lkN363GUaE4RKrbpJ2SOnTSQke8nk4CSyH7xFvnhtSbJMnxCv91vFUUv9hBF4XRlOs4gL0ea3eiJ4dAV2YlFfGxKB3d6IqYsUxzucxvYQMyZC42mqFlqfsoxFKyUnDc',1,1,1),
(3,'potvrdaemaila12','Bozo','Bozic','$2a$12$mLAY2RNklXNO4/csBstyKesvcMXOUhAXnOkfOS63mkkq3ZDWd0b2i','potvrdaemaila.kinoapp2023@gmail.com','B2yHPghWyjAJ82Gc8cxoiuSnTQNNou76BEjEAZXT59fop7v5YSIuc3jw0c11rh6WvVEVznSBKaNaMxGyw09G9JCOqrYTZSqE6Imhoaz4KUxhvODls4uicBiiVdiZfHdzkwV9HF3tyInJlicdgQvF3Uc7mPaWpByR6jL6ieIArMJx8EZTBujXaAG1F9ZHwK0IpVpOQusV0oSrHUHvuaiUppHHJVTR0WZyMRhTWedgD9A5iP3SWx8GgLQ6Joiu1Zs',0,1,1),
(4,'deaktiviran12','Ivan','Ivanic','$2a$12$0lwWrDoQVYlobR7G7elcfe5xrjifILeDUX2B0uiGkCcEuw6qM0Dku','nepostojeci.email1232314567@gmail.com','H9YBhsyPlQUwBC2LI6QFtttXzTctbEkmeMFuJ1wPKzazsMHi7wuHxREvbvKMVBgiAfCqJTXJiq6wvwcn1UJR8duN14znHl78FrLhxp4P8NqnqpvJWM85tucn2WGJ73GZB5kMKMMsaMDyNUaxzCGDDYDPE752JUGKD82IY6Pu0OLywLmviGPBURHmYke6Dlc0g9dGQ4p19rWavRpH7mVRHDrKfj5dBODlbKwkSH13X61aAzK7rDrQhPI3iSfAdEL',0,0,1);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
