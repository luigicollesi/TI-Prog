-- MySQL dump 10.13  Distrib 8.0.42, for Linux (x86_64)
--
-- Host: localhost    Database: minha_app
-- ------------------------------------------------------
-- Server version	8.0.42-0ubuntu0.24.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `minha_app`
--

/*!40000 DROP DATABASE IF EXISTS `minha_app`*/;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `minha_app` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `minha_app`;

--
-- Table structure for table `historico_partidas`
--

DROP TABLE IF EXISTS `historico_partidas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `historico_partidas` (
  `id` char(36) NOT NULL DEFAULT (uuid()),
  `user_id` char(36) NOT NULL,
  `valor` int NOT NULL,
  `cartas_player` json NOT NULL,
  `cartas_bot` json NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `venceu` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `historico_partidas_ibfk_1` (`user_id`),
  CONSTRAINT `historico_partidas_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `historico_partidas`
--

LOCK TABLES `historico_partidas` WRITE;
/*!40000 ALTER TABLE `historico_partidas` DISABLE KEYS */;
INSERT INTO `historico_partidas` VALUES ('011f4c15-3f54-11f0-8bfb-70c94e61b93f','f520ae48-3c07-11f0-a98d-70c94e61b93f',50,'[[\"A\", \"Espadas\"], [\"9\", \"Espadas\"]]','[[\"6\", \"Espadas\"], [\"Q\", \"Copas\"], [\"5\", \"Ouros\"]]','2025-06-02 01:50:47',0),('079287f9-3e45-11f0-8bfb-70c94e61b93f','f4e8b59f-3e44-11f0-8bfb-70c94e61b93f',200,'[[\"A\", \"Ouros\"], [\"8\", \"Copas\"]]','[[\"6\", \"Espadas\"], [\"Q\", \"Copas\"], [\"2\", \"Espadas\"]]','2025-05-31 17:31:04',1),('0bbf4559-3e6c-11f0-8bfb-70c94e61b93f','f520ae48-3c07-11f0-a98d-70c94e61b93f',50,'[[\"7\", \"Ouros\"], [\"4\", \"Paus\"], [\"6\", \"Paus\"]]','[[\"K\", \"Copas\"], [\"3\", \"Copas\"], [\"8\", \"Ouros\"]]','2025-05-31 22:10:22',0),('137c1ee8-3e45-11f0-8bfb-70c94e61b93f','f4e8b59f-3e44-11f0-8bfb-70c94e61b93f',200,'[[\"3\", \"Paus\"], [\"7\", \"Copas\"], [\"7\", \"Espadas\"]]','[[\"10\", \"Paus\"], [\"5\", \"Ouros\"], [\"5\", \"Paus\"]]','2025-05-31 17:31:24',0),('158ea8ea-3e6c-11f0-8bfb-70c94e61b93f','f520ae48-3c07-11f0-a98d-70c94e61b93f',150,'[[\"4\", \"Paus\"], [\"8\", \"Copas\"], [\"5\", \"Paus\"]]','[[\"3\", \"Ouros\"], [\"2\", \"Copas\"], [\"A\", \"Paus\"], [\"5\", \"Ouros\"]]','2025-05-31 22:10:38',0),('1f0adbf2-3e45-11f0-8bfb-70c94e61b93f','f4e8b59f-3e44-11f0-8bfb-70c94e61b93f',200,'[[\"2\", \"Paus\"], [\"Q\", \"Espadas\"], [\"2\", \"Copas\"], [\"K\", \"Paus\"]]','[[\"6\", \"Espadas\"], [\"A\", \"Copas\"]]','2025-05-31 17:31:44',0),('2289c362-3ff2-11f0-8363-70c94e61b93f','12c30313-3ff2-11f0-8363-70c94e61b93f',500,'[[\"A\", \"Copas\"], [\"Q\", \"Paus\"]]','[[\"K\", \"Ouros\"], [\"8\", \"Espadas\"]]','2025-06-02 20:42:44',1),('26064ac9-3e6c-11f0-8bfb-70c94e61b93f','f520ae48-3c07-11f0-a98d-70c94e61b93f',700,'[[\"9\", \"Espadas\"], [\"5\", \"Paus\"], [\"5\", \"Ouros\"]]','[[\"8\", \"Ouros\"], [\"4\", \"Paus\"], [\"2\", \"Espadas\"], [\"J\", \"Paus\"]]','2025-05-31 22:11:06',1),('2926c5a3-3e45-11f0-8bfb-70c94e61b93f','f4e8b59f-3e44-11f0-8bfb-70c94e61b93f',200,'[[\"J\", \"Paus\"], [\"8\", \"Espadas\"]]','[[\"Q\", \"Ouros\"], [\"10\", \"Paus\"]]','2025-05-31 17:32:01',0),('30584728-3ff2-11f0-8363-70c94e61b93f','12c30313-3ff2-11f0-8363-70c94e61b93f',1600,'[[\"9\", \"Espadas\"], [\"9\", \"Paus\"]]','[[\"3\", \"Ouros\"], [\"10\", \"Paus\"], [\"J\", \"Copas\"]]','2025-06-02 20:43:07',1),('39ddf285-3e6c-11f0-8bfb-70c94e61b93f','f520ae48-3c07-11f0-a98d-70c94e61b93f',100,'[[\"10\", \"Copas\"], [\"4\", \"Copas\"], [\"Q\", \"Espadas\"]]','[[\"9\", \"Copas\"], [\"7\", \"Ouros\"], [\"K\", \"Espadas\"]]','2025-05-31 22:11:39',0),('3b4922f1-3e70-11f0-8bfb-70c94e61b93f','f520ae48-3c07-11f0-a98d-70c94e61b93f',100,'[[\"6\", \"Paus\"], [\"2\", \"Espadas\"], [\"A\", \"Paus\"]]','[[\"5\", \"Ouros\"], [\"5\", \"Espadas\"], [\"2\", \"Copas\"], [\"4\", \"Espadas\"], [\"10\", \"Paus\"]]','2025-05-31 22:40:20',1),('450f961f-3e89-11f0-8bfb-70c94e61b93f','f520ae48-3c07-11f0-a98d-70c94e61b93f',1110,'[[\"8\", \"Ouros\"], [\"8\", \"Copas\"]]','[[\"3\", \"Paus\"], [\"3\", \"Ouros\"], [\"Q\", \"Espadas\"], [\"J\", \"Copas\"]]','2025-06-01 01:39:33',1),('4708bea5-3e6d-11f0-8bfb-70c94e61b93f','f520ae48-3c07-11f0-a98d-70c94e61b93f',25,'[[\"4\", \"Paus\"], [\"7\", \"Copas\"], [\"8\", \"Paus\"]]','[[\"7\", \"Espadas\"], [\"3\", \"Ouros\"], [\"Q\", \"Espadas\"]]','2025-05-31 22:19:11',0),('48616250-3e46-11f0-8bfb-70c94e61b93f','f520ae48-3c07-11f0-a98d-70c94e61b93f',40,'[[\"6\", \"Copas\"], [\"10\", \"Espadas\"], [\"2\", \"Espadas\"]]','[[\"3\", \"Ouros\"], [\"6\", \"Paus\"], [\"J\", \"Copas\"]]','2025-05-31 17:40:03',0),('4c30989c-3e82-11f0-8bfb-70c94e61b93f','f520ae48-3c07-11f0-a98d-70c94e61b93f',62,'[[\"A\", \"Ouros\"], [\"4\", \"Copas\"], [\"6\", \"Copas\"]]','[[\"K\", \"Paus\"], [\"9\", \"Paus\"]]','2025-06-01 00:49:39',1),('52cc399a-3f54-11f0-8bfb-70c94e61b93f','f520ae48-3c07-11f0-a98d-70c94e61b93f',20,'[[\"K\", \"Ouros\"], [\"Q\", \"Paus\"]]','[[\"A\", \"Ouros\"], [\"3\", \"Paus\"], [\"5\", \"Ouros\"]]','2025-06-02 01:53:04',1),('535324ec-3ff2-11f0-8363-70c94e61b93f','12c30313-3ff2-11f0-8363-70c94e61b93f',1600,'[[\"3\", \"Paus\"], [\"4\", \"Ouros\"], [\"Q\", \"Ouros\"]]','[[\"9\", \"Ouros\"], [\"K\", \"Ouros\"]]','2025-06-02 20:44:06',0),('54046757-3e87-11f0-8bfb-70c94e61b93f','f520ae48-3c07-11f0-a98d-70c94e61b93f',200,'[[\"4\", \"Copas\"], [\"6\", \"Espadas\"], [\"3\", \"Espadas\"], [\"4\", \"Ouros\"], [\"3\", \"Ouros\"]]','[[\"9\", \"Copas\"], [\"10\", \"Copas\"]]','2025-06-01 01:25:39',1),('58f67fb4-3e85-11f0-8bfb-70c94e61b93f','f520ae48-3c07-11f0-a98d-70c94e61b93f',0,'[[\"9\", \"Ouros\"], [\"2\", \"Ouros\"], [\"Q\", \"Espadas\"]]','[[\"Q\", \"Ouros\"], [\"2\", \"Espadas\"], [\"3\", \"Espadas\"], [\"J\", \"Paus\"]]','2025-06-01 01:11:29',1),('5febc958-3e87-11f0-8bfb-70c94e61b93f','f520ae48-3c07-11f0-a98d-70c94e61b93f',5,'[[\"7\", \"Paus\"], [\"Q\", \"Paus\"], [\"Q\", \"Espadas\"]]','[[\"5\", \"Paus\"], [\"3\", \"Copas\"], [\"J\", \"Espadas\"]]','2025-06-01 01:25:59',0),('64ae0837-3e80-11f0-8bfb-70c94e61b93f','f520ae48-3c07-11f0-a98d-70c94e61b93f',12,'[[\"8\", \"Copas\"], [\"4\", \"Paus\"], [\"7\", \"Ouros\"], [\"2\", \"Copas\"]]','[[\"5\", \"Espadas\"], [\"J\", \"Paus\"], [\"3\", \"Ouros\"]]','2025-06-01 00:36:01',1),('7ede2fb3-3e78-11f0-8bfb-70c94e61b93f','f520ae48-3c07-11f0-a98d-70c94e61b93f',50,'[[\"9\", \"Espadas\"], [\"3\", \"Espadas\"], [\"4\", \"Ouros\"], [\"6\", \"Paus\"]]','[[\"A\", \"Copas\"], [\"8\", \"Copas\"]]','2025-05-31 23:39:29',0),('82fb6b5b-3f3e-11f0-8bfb-70c94e61b93f','f520ae48-3c07-11f0-a98d-70c94e61b93f',50,'[[\"8\", \"Copas\"], [\"6\", \"Paus\"], [\"4\", \"Paus\"]]','[[\"7\", \"Ouros\"], [\"3\", \"Copas\"], [\"J\", \"Espadas\"]]','2025-06-01 23:16:56',0),('8aeb7385-3ff2-11f0-8363-70c94e61b93f','f520ae48-3c07-11f0-a98d-70c94e61b93f',2550,'[[\"6\", \"Paus\"], [\"3\", \"Ouros\"], [\"2\", \"Copas\"], [\"10\", \"Copas\"]]','[[\"7\", \"Espadas\"], [\"9\", \"Paus\"], [\"2\", \"Paus\"]]','2025-06-02 20:45:39',1),('8f3694e0-3e3c-11f0-8bfb-70c94e61b93f','f520ae48-3c07-11f0-a98d-70c94e61b93f',50,'[[\"6\", \"Espadas\"], [\"6\", \"Ouros\"], [\"6\", \"Paus\"]]','[[\"10\", \"Ouros\"], [\"Q\", \"Copas\"]]','2025-05-31 16:30:27',0),('970cf350-3e7f-11f0-8bfb-70c94e61b93f','f520ae48-3c07-11f0-a98d-70c94e61b93f',50,'[[\"8\", \"Ouros\"], [\"7\", \"Espadas\"], [\"4\", \"Copas\"]]','[[\"Q\", \"Espadas\"], [\"K\", \"Espadas\"]]','2025-06-01 00:30:16',0),('994359eb-3e3c-11f0-8bfb-70c94e61b93f','f520ae48-3c07-11f0-a98d-70c94e61b93f',25,'[[\"A\", \"Espadas\"], [\"K\", \"Copas\"]]','[[\"3\", \"Ouros\"], [\"5\", \"Paus\"], [\"2\", \"Copas\"], [\"9\", \"Copas\"]]','2025-05-31 16:30:43',1),('9b87f2e4-3e70-11f0-8bfb-70c94e61b93f','f520ae48-3c07-11f0-a98d-70c94e61b93f',50,'[[\"2\", \"Copas\"], [\"6\", \"Espadas\"], [\"7\", \"Copas\"], [\"K\", \"Espadas\"]]','[[\"Q\", \"Ouros\"], [\"5\", \"Paus\"], [\"4\", \"Copas\"]]','2025-05-31 22:43:01',0),('9f1f401a-3e7f-11f0-8bfb-70c94e61b93f','f520ae48-3c07-11f0-a98d-70c94e61b93f',125,'[[\"A\", \"Copas\"], [\"10\", \"Ouros\"]]','[[\"6\", \"Ouros\"], [\"4\", \"Paus\"], [\"8\", \"Copas\"]]','2025-06-01 00:30:29',1),('abd0b1df-3e70-11f0-8bfb-70c94e61b93f','f520ae48-3c07-11f0-a98d-70c94e61b93f',50,'[[\"2\", \"Copas\"], [\"6\", \"Espadas\"], [\"7\", \"Copas\"], [\"K\", \"Espadas\"], [\"A\", \"Espadas\"], [\"K\", \"Ouros\"]]','[[\"Q\", \"Ouros\"], [\"5\", \"Paus\"], [\"4\", \"Copas\"], [\"5\", \"Ouros\"], [\"8\", \"Ouros\"], [\"3\", \"Ouros\"], [\"8\", \"Ouros\"]]','2025-05-31 22:43:28',0),('b2e4288c-3e7f-11f0-8bfb-70c94e61b93f','f520ae48-3c07-11f0-a98d-70c94e61b93f',5,'[[\"6\", \"Ouros\"], [\"Q\", \"Ouros\"], [\"K\", \"Espadas\"]]','[[\"J\", \"Espadas\"], [\"A\", \"Copas\"]]','2025-06-01 00:31:03',0),('c023f5ee-3ff8-11f0-abaf-70c94e61b93f','f520ae48-3c07-11f0-a98d-70c94e61b93f',5,'[[\"5\", \"Espadas\"], [\"3\", \"Espadas\"], [\"6\", \"Espadas\"], [\"Q\", \"Ouros\"]]','[[\"10\", \"Copas\"], [\"6\", \"Paus\"], [\"8\", \"Paus\"]]','2025-06-02 21:30:05',0),('d70d0021-3e6f-11f0-8bfb-70c94e61b93f','f520ae48-3c07-11f0-a98d-70c94e61b93f',50,'[[\"3\", \"Copas\"], [\"3\", \"Ouros\"], [\"7\", \"Copas\"], [\"3\", \"Paus\"]]','[[\"Q\", \"Copas\"], [\"Q\", \"Espadas\"]]','2025-05-31 22:37:31',0),('f8432d4e-3e6b-11f0-8bfb-70c94e61b93f','f520ae48-3c07-11f0-a98d-70c94e61b93f',100,'[[\"Q\", \"Espadas\"], [\"4\", \"Paus\"], [\"6\", \"Paus\"]]','[[\"3\", \"Espadas\"], [\"9\", \"Copas\"], [\"6\", \"Copas\"]]','2025-05-31 22:09:49',1);
/*!40000 ALTER TABLE `historico_partidas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` char(36) NOT NULL DEFAULT (uuid()),
  `user_name` varchar(50) NOT NULL,
  `senha` varchar(255) NOT NULL,
  `criado_em` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Money` int NOT NULL DEFAULT '500',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_name` (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES ('12c30313-3ff2-11f0-8363-70c94e61b93f','gays','gays','2025-06-02 20:42:17',0),('a08545c7-3ff1-11f0-8363-70c94e61b93f','jeff','pppp','2025-06-02 20:39:06',500),('c878ee7b-3b11-11f0-a98d-70c94e61b93f','geraldo','gays','2025-05-27 15:46:41',0),('dbd06614-3c15-11f0-a98d-70c94e61b93f','a','asdasd','2025-05-28 22:48:22',0),('f4e8b59f-3e44-11f0-8bfb-70c94e61b93f','User','senha','2025-05-31 17:30:33',0),('f520ae48-3c07-11f0-a98d-70c94e61b93f','Luigi','gays','2025-05-28 21:08:52',2549);
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-02 18:36:40
