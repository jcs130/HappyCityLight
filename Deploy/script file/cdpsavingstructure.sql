CREATE DATABASE  IF NOT EXISTS `msgsaving` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE `msgsaving`;
-- MySQL dump 10.13  Distrib 5.7.9, for Win64 (x86_64)
--
-- Host: localhost    Database: msgsaving
-- ------------------------------------------------------
-- Server version	5.7.10-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `full_message`
--

DROP TABLE IF EXISTS `full_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `full_message` (
  `num_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `raw_id_str` varchar(45) DEFAULT NULL,
  `user_name` varchar(100) DEFAULT NULL,
  `creat_at` bigint(20) DEFAULT NULL,
  `text` varchar(255) DEFAULT NULL,
  `emotion_text` varchar(255) DEFAULT NULL,
  `media_types` varchar(255) DEFAULT NULL,
  `media_urls` varchar(255) DEFAULT NULL,
  `media_urls_local` varchar(255) DEFAULT NULL,
  `emotion_medias` varchar(255) DEFAULT NULL,
  `emotion_all` varchar(255) DEFAULT NULL,
  `place_type` varchar(45) DEFAULT NULL,
  `place_name` varchar(45) DEFAULT NULL,
  `place_fullname` varchar(100) DEFAULT NULL,
  `country` varchar(45) DEFAULT NULL,
  `province` varchar(45) DEFAULT NULL,
  `city` varchar(45) DEFAULT NULL,
  `query_location_latitude` double DEFAULT NULL,
  `query_location_langtitude` double DEFAULT NULL,
  `is_real_location` varchar(10) DEFAULT '0',
  `hashtags` varchar(256) DEFAULT NULL,
  `replay_to` varchar(45) DEFAULT NULL,
  `lang` varchar(45) DEFAULT NULL,
  `message_from` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`num_id`),
  UNIQUE KEY `record_id_UNIQUE` (`num_id`),
  UNIQUE KEY `raw_id_str_UNIQUE` (`raw_id_str`),
  KEY `times_index` (`creat_at`) USING BTREE,
  KEY `geo_index` (`query_location_latitude`,`query_location_langtitude`) USING BTREE,
  KEY `time_and_geo_index` (`creat_at`,`query_location_latitude`,`query_location_langtitude`) USING BTREE,
  FULLTEXT KEY `text` (`text`)
) ENGINE=InnoDB AUTO_INCREMENT=5839424 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `statiistics_record`
--

DROP TABLE IF EXISTS `statiistics_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `statiistics_record` (
  `record_id` int(11) NOT NULL AUTO_INCREMENT,
  `record_key` varchar(100) NOT NULL,
  `date_timestamp_ms` bigint(20) NOT NULL,
  `local_date` date NOT NULL,
  `place_id` int(11) NOT NULL,
  `place_name` varchar(50) NOT NULL,
  `place_obj` longtext NOT NULL,
  `pulse_value` double NOT NULL,
  `pulse_obj` longtext NOT NULL,
  `rank` int(11) DEFAULT NULL,
  `hot_topics` longtext NOT NULL,
  `message_from` varchar(45) DEFAULT NULL,
  `language` varchar(45) DEFAULT NULL,
  `private` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`record_id`,`record_key`),
  UNIQUE KEY `record_num_UNIQUE` (`record_id`) USING BTREE,
  UNIQUE KEY `record_key_UNIQUE` (`record_key`) USING BTREE,
  KEY `time_imdex` (`date_timestamp_ms`) USING BTREE,
  KEY `local_date_index` (`local_date`),
  KEY `place_index` (`place_id`) USING BTREE,
  KEY `date_index` (`local_date`)
) ENGINE=InnoDB AUTO_INCREMENT=17666 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping events for database 'msgsaving'
--

--
-- Dumping routines for database 'msgsaving'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-05-19 21:59:43
