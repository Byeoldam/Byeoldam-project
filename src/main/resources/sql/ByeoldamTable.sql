-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: ssafy_db
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `bookmark_tag`
--

DROP TABLE IF EXISTS `bookmark_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookmark_tag` (
  `bookmark_id` bigint DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tag_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4ima26acldi5fb4ik9qb9sk6c` (`bookmark_id`),
  KEY `FKhq7j2vott6kem0g51hhgq5nfl` (`tag_id`),
  CONSTRAINT `FK4ima26acldi5fb4ik9qb9sk6c` FOREIGN KEY (`bookmark_id`) REFERENCES `bookmarks` (`id`),
  CONSTRAINT `FKhq7j2vott6kem0g51hhgq5nfl` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookmark_tag`
--

LOCK TABLES `bookmark_tag` WRITE;
/*!40000 ALTER TABLE `bookmark_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `bookmark_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bookmark_url`
--

DROP TABLE IF EXISTS `bookmark_url`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookmark_url` (
  `reading_time` int DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `reference_count` bigint NOT NULL,
  `url` varchar(255) NOT NULL,
  `img` varchar(255) NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookmark_url`
--

LOCK TABLES `bookmark_url` WRITE;
/*!40000 ALTER TABLE `bookmark_url` DISABLE KEYS */;
/*!40000 ALTER TABLE `bookmark_url` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bookmark_url_tag`
--

DROP TABLE IF EXISTS `bookmark_url_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookmark_url_tag` (
  `bookmark_url_id` bigint NOT NULL,
  `tag_id` bigint NOT NULL,
  PRIMARY KEY (`bookmark_url_id`,`tag_id`),
  KEY `FK89dm5f7lfqh3pp5k4stfmtf1c` (`tag_id`),
  CONSTRAINT `FK87eb8uwwjdtmev416yxlc8r7` FOREIGN KEY (`bookmark_url_id`) REFERENCES `bookmark_url` (`id`),
  CONSTRAINT `FK89dm5f7lfqh3pp5k4stfmtf1c` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookmark_url_tag`
--

LOCK TABLES `bookmark_url_tag` WRITE;
/*!40000 ALTER TABLE `bookmark_url_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `bookmark_url_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bookmarks`
--

DROP TABLE IF EXISTS `bookmarks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookmarks` (
  `is_read` bit(1) NOT NULL,
  `priority` bit(1) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `personal_collection_id` bigint DEFAULT NULL,
  `shared_collection_id` bigint DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `url_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbr351s5pxnh4e1wpdwfylxhsp` (`url_id`),
  KEY `FK6h6aj644yvg8c6afhtqqte447` (`personal_collection_id`),
  KEY `FKg8ly42d5j04wlcfckpg3j8nef` (`shared_collection_id`),
  KEY `FKab3b25rlka2y5um0u2a3b7jcu` (`user_id`),
  CONSTRAINT `FK6h6aj644yvg8c6afhtqqte447` FOREIGN KEY (`personal_collection_id`) REFERENCES `personal_collection` (`id`),
  CONSTRAINT `FKab3b25rlka2y5um0u2a3b7jcu` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKbr351s5pxnh4e1wpdwfylxhsp` FOREIGN KEY (`url_id`) REFERENCES `bookmark_url` (`id`),
  CONSTRAINT `FKg8ly42d5j04wlcfckpg3j8nef` FOREIGN KEY (`shared_collection_id`) REFERENCES `shared_collection` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookmarks`
--

LOCK TABLES `bookmarks` WRITE;
/*!40000 ALTER TABLE `bookmarks` DISABLE KEYS */;
/*!40000 ALTER TABLE `bookmarks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `memo`
--

DROP TABLE IF EXISTS `memo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `memo` (
  `bookmark_id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `content` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbq7lt8ju7wa4lq39p4xucyyh7` (`bookmark_id`),
  KEY `FK5vlmhksso19pshk0ac1qo1p82` (`user_id`),
  CONSTRAINT `FK5vlmhksso19pshk0ac1qo1p82` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKbq7lt8ju7wa4lq39p4xucyyh7` FOREIGN KEY (`bookmark_id`) REFERENCES `bookmarks` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `memo`
--

LOCK TABLES `memo` WRITE;
/*!40000 ALTER TABLE `memo` DISABLE KEYS */;
/*!40000 ALTER TABLE `memo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `bookmark_id` bigint DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `shared_collection_id` bigint DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `type` varchar(31) NOT NULL,
  `message` varchar(255) NOT NULL,
  `nickname` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKb0yvoep4h4k92ipon31wmdf7e` (`user_id`),
  KEY `FK8btbera9j8sxy2ksjqshbvkfg` (`bookmark_id`),
  KEY `FK4ala52wxk72wufncrtvqq45we` (`shared_collection_id`),
  CONSTRAINT `FK4ala52wxk72wufncrtvqq45we` FOREIGN KEY (`shared_collection_id`) REFERENCES `shared_collection` (`id`),
  CONSTRAINT `FK8btbera9j8sxy2ksjqshbvkfg` FOREIGN KEY (`bookmark_id`) REFERENCES `bookmarks` (`id`),
  CONSTRAINT `FKb0yvoep4h4k92ipon31wmdf7e` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `personal_collection`
--

DROP TABLE IF EXISTS `personal_collection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `personal_collection` (
  `created_at` datetime(6) DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKroo2w7fjh72wu99ytavoduhas` (`user_id`),
  CONSTRAINT `FKroo2w7fjh72wu99ytavoduhas` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `personal_collection`
--

LOCK TABLES `personal_collection` WRITE;
/*!40000 ALTER TABLE `personal_collection` DISABLE KEYS */;
/*!40000 ALTER TABLE `personal_collection` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rss`
--

DROP TABLE IF EXISTS `rss`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rss` (
  `created_at` datetime(6) DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `rss_url` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rss`
--

LOCK TABLES `rss` WRITE;
/*!40000 ALTER TABLE `rss` DISABLE KEYS */;
/*!40000 ALTER TABLE `rss` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `s3_image`
--

DROP TABLE IF EXISTS `s3_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `s3_image` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `file_key` varchar(300) NOT NULL,
  `file_url` varchar(300) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKfgw38idbsw7oe7s22m3wyecac` (`file_key`),
  UNIQUE KEY `UKdv7ma81yd6jyfdqf16r4apnaf` (`file_url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `s3_image`
--

LOCK TABLES `s3_image` WRITE;
/*!40000 ALTER TABLE `s3_image` DISABLE KEYS */;
/*!40000 ALTER TABLE `s3_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shared_collection`
--

DROP TABLE IF EXISTS `shared_collection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shared_collection` (
  `created_at` datetime(6) DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) DEFAULT NULL,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shared_collection`
--

LOCK TABLES `shared_collection` WRITE;
/*!40000 ALTER TABLE `shared_collection` DISABLE KEYS */;
/*!40000 ALTER TABLE `shared_collection` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shared_user`
--

DROP TABLE IF EXISTS `shared_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shared_user` (
  `created_at` datetime(6) DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `shared_collection_id` bigint NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `role` enum('MEMBER','OWNER') NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKll32xt96y95gh07qkf0ohevrk` (`shared_collection_id`),
  KEY `FKj0aseyosg5np7kowhfqescdlx` (`user_id`),
  CONSTRAINT `FKj0aseyosg5np7kowhfqescdlx` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKll32xt96y95gh07qkf0ohevrk` FOREIGN KEY (`shared_collection_id`) REFERENCES `shared_collection` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shared_user`
--

LOCK TABLES `shared_user` WRITE;
/*!40000 ALTER TABLE `shared_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `shared_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tag` (
  `reference_count` int NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `bolder_color` varchar(255) NOT NULL,
  `color` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK1wdpsed5kna2y38hnbgrnhi5b` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tag`
--

LOCK TABLES `tag` WRITE;
/*!40000 ALTER TABLE `tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `alert_day` int NOT NULL,
  `is_verified` bit(1) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `updated_at` datetime(6) DEFAULT NULL,
  `nickname` varchar(20) NOT NULL,
  `role` varchar(20) NOT NULL,
  `provider_id` varchar(60) DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(256) NOT NULL,
  `profile_url` varchar(500) NOT NULL,
  `access_token` varchar(255) DEFAULT NULL,
  `email_verification_token` varchar(255) DEFAULT NULL,
  `refresh_token` varchar(255) DEFAULT NULL,
  `is_active` enum('PENDING','ACTIVE','INACTIVE','LOCKED') NOT NULL DEFAULT 'PENDING',
  `provider` enum('LOCAL','KAKAO','NAVER','GOOGLE') NOT NULL DEFAULT 'LOCAL',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKob8kqyqqgmefl0aco34akdtpe` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_rss`
--

DROP TABLE IF EXISTS `user_rss`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_rss` (
  `is_read` bit(1) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `rss_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `latest_title` varchar(255) DEFAULT NULL,
  `previous_title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3ukjkuyxu1x28posygm7g2q0r` (`rss_id`),
  KEY `FK59cs7l8b2kkj75ir5ubi4vhko` (`user_id`),
  CONSTRAINT `FK3ukjkuyxu1x28posygm7g2q0r` FOREIGN KEY (`rss_id`) REFERENCES `rss` (`id`),
  CONSTRAINT `FK59cs7l8b2kkj75ir5ubi4vhko` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_rss`
--

LOCK TABLES `user_rss` WRITE;
/*!40000 ALTER TABLE `user_rss` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_rss` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_tag`
--

DROP TABLE IF EXISTS `user_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_tag` (
  `tag_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`tag_id`,`user_id`),
  KEY `FKhqbypqh9kyjp3jcslfg67c6n5` (`user_id`),
  CONSTRAINT `FK9qknt3y115f17660k0qnm9x3g` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`),
  CONSTRAINT `FKhqbypqh9kyjp3jcslfg67c6n5` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_tag`
--

LOCK TABLES `user_tag` WRITE;
/*!40000 ALTER TABLE `user_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_tag` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-02-13 21:42:03
