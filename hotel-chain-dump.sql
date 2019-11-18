CREATE DATABASE  IF NOT EXISTS `hotel_chain` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `hotel_chain`;
-- MySQL dump 10.13  Distrib 8.0.17, for Win64 (x86_64)
--
-- Host: localhost    Database: hotel_chain
-- ------------------------------------------------------
-- Server version	8.0.17

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
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `surname` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_irnrrncatp2fvw52vp45j7rlw` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (1,'Federico','pwd','Verdi','federico'),(2,'Alessio','pwd','Rossi','alessio'),(3,'Chiara','pwd','Azzurri','chiara'),(4,'Marco','pwd','Bianchi','marco'),(5,'Luca','pwd','Marroni','luca'),(6,'Sara','pwd','Violi','sara'),(7,'Ettore','pwd','Amaranti','ettore'),(8,'James','pwd','Blue','james'),(9,'Nathan','pwd','Black','nathan'),(10,'Chloe','pwd','Red','chloe'),(11,'Ellie','pwd','Green','ellie'),(12,'Ellie','pwd','Pink','ellie2'),(13,'Sarah','pwd','Yellow','sarah'),(14,'Max','pwd','Brown','max'),(15,'Julia','pwd','White','julia'),(16,'John','pwd','Orange','john'),(17,'Luke','pwd','Tan','luke'),(18,'Kevin','pwd','Purple','kevin'),(19,'Piergiorgio','pwd','Neri','piergiorgio');
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hotel`
--

DROP TABLE IF EXISTS `hotel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_mq91c5gn4g1l11nmk4hba7nu6` (`address`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hotel`
--

LOCK TABLES `hotel` WRITE;
/*!40000 ALTER TABLE `hotel` DISABLE KEYS */;
INSERT INTO `hotel` VALUES (3,'Via Bologna 28, Bologna'),(4,'Via Firenze 29, Firenze'),(2,'Via Milano 27, Milano'),(5,'Via Pisa 28, Pisa'),(1,'Via Roma 26, Roma');
/*!40000 ALTER TABLE `hotel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `receptionist`
--

DROP TABLE IF EXISTS `receptionist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `receptionist` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `surname` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `hotel_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_cxeagq67ogkb9j9yig7we5mpf` (`username`),
  KEY `FKslpqyef17533ky5ty3kebotj3` (`hotel_id`),
  CONSTRAINT `FKslpqyef17533ky5ty3kebotj3` FOREIGN KEY (`hotel_id`) REFERENCES `hotel` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `receptionist`
--

LOCK TABLES `receptionist` WRITE;
/*!40000 ALTER TABLE `receptionist` DISABLE KEYS */;
INSERT INTO `receptionist` VALUES (1,'Laura','pwd','Romani','r1',1),(2,'Francesco','pwd','Bolognesi','r2',3),(3,'Mirco','pwd','Rossi','r3',3),(4,'Luisa','pwd','Milanelli','r4',2),(5,'Benedetta','pwd','Vinci','r5',2),(6,'Marco','pwd','Duomo','r6',4),(7,'Benedetta','pwd','Uffizi','r7',4),(8,'Lorena','pwd','Duomo','r8',5),(9,'Federico','pwd','Lungarno','r9',5);
/*!40000 ALTER TABLE `receptionist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `check_in` date DEFAULT NULL,
  `check_out` date DEFAULT NULL,
  `customer_id` bigint(20) DEFAULT NULL,
  `room_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_room_checkIn` (`room_id`,`check_in`),
  KEY `FK41v6ueo0hiran65w8y1cta2c2` (`customer_id`),
  CONSTRAINT `FK41v6ueo0hiran65w8y1cta2c2` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `FKm8xumi0g23038cw32oiva2ymw` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservation`
--

LOCK TABLES `reservation` WRITE;
/*!40000 ALTER TABLE `reservation` DISABLE KEYS */;
INSERT INTO `reservation` VALUES (1,'2019-11-15','2019-11-19',19,19),(2,'2018-11-15','2018-11-19',19,19),(3,'2019-01-15','2019-01-16',14,15),(4,'2019-02-26','2019-03-01',11,15),(5,'2020-02-26','2020-03-01',11,4),(6,'2020-02-12','2020-02-13',16,4),(7,'2019-12-20','2019-12-23',16,4),(8,'2019-12-20','2019-12-23',18,17),(9,'2020-09-28','2020-10-02',11,17),(10,'2019-10-01','2019-10-02',8,4),(11,'2019-10-14','2019-10-17',8,17),(12,'2020-06-04','2020-06-07',18,15),(13,'2020-07-04','2020-07-07',15,15),(14,'2020-07-11','2020-07-21',15,17),(15,'2020-07-23','2020-07-27',15,4),(16,'2020-07-24','2020-07-27',18,12),(17,'2020-01-11','2020-01-14',15,12),(18,'2019-08-11','2019-08-14',15,1),(19,'2019-08-23','2019-09-02',18,1),(20,'2020-09-02','2020-09-03',18,1),(21,'2020-09-07','2020-09-09',2,9),(22,'2018-10-25','2018-11-01',2,9),(23,'2019-06-07','2019-06-10',2,9);
/*!40000 ALTER TABLE `reservation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room`
--

DROP TABLE IF EXISTS `room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `available` bit(1) NOT NULL,
  `capacity` int(11) DEFAULT NULL,
  `number` int(11) DEFAULT NULL,
  `hotel_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_hotel_number` (`hotel_id`,`number`),
  CONSTRAINT `FKdosq3ww4h9m2osim6o0lugng8` FOREIGN KEY (`hotel_id`) REFERENCES `hotel` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room`
--

LOCK TABLES `room` WRITE;
/*!40000 ALTER TABLE `room` DISABLE KEYS */;
INSERT INTO `room` VALUES (1,_binary '',2,101,1),(2,_binary '',3,102,1),(3,_binary '',2,103,1),(4,_binary '',2,101,2),(5,_binary '',3,102,2),(6,_binary '',4,201,2),(7,_binary '',4,101,3),(8,_binary '',3,201,3),(9,_binary '',2,301,3),(10,_binary '\0',2,302,3),(11,_binary '',4,101,4),(12,_binary '',3,102,4),(13,_binary '',2,103,4),(14,_binary '\0',2,104,4),(15,_binary '',4,101,5),(16,_binary '',3,201,5),(17,_binary '',2,202,5),(18,_binary '',2,301,5),(19,_binary '',5,401,3);
/*!40000 ALTER TABLE `room` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-11-18 14:55:20
