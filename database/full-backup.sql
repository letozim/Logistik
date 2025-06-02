-- MySQL dump 10.13  Distrib 8.0.42, for macos14.7 (x86_64)
--
-- Host: localhost    Database: logistik_crm
-- ------------------------------------------------------
-- Server version	8.0.42

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
-- Table structure for table `auftrag`
--

DROP TABLE IF EXISTS `auftrag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auftrag` (
  `auftrag_id` int NOT NULL AUTO_INCREMENT,
  `auftragsnummer` varchar(50) NOT NULL,
  `kunde_id` int NOT NULL,
  `fahrer_id` int DEFAULT NULL,
  `lieferadresse` text,
  `lieferdatum` date DEFAULT NULL,
  `status` enum('Neu','Bestaetigt','In_Bearbeitung','Unterwegs','Geliefert','Abgeschlossen','Storniert') DEFAULT 'Neu',
  `prioritaet` enum('Niedrig','Normal','Hoch','Dringend') DEFAULT 'Normal',
  `gesamtsumme` decimal(10,2) DEFAULT '0.00',
  `mwst_satz` decimal(5,2) DEFAULT '19.00',
  `bemerkungen` text,
  `interne_notizen` text,
  `erstellt_am` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `geaendert_am` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`auftrag_id`),
  UNIQUE KEY `auftragsnummer` (`auftragsnummer`),
  KEY `idx_auftrag_kunde` (`kunde_id`),
  KEY `idx_auftrag_fahrer` (`fahrer_id`),
  KEY `idx_auftrag_status` (`status`),
  KEY `idx_auftrag_datum` (`lieferdatum`),
  KEY `idx_auftragsnummer` (`auftragsnummer`),
  CONSTRAINT `auftrag_ibfk_1` FOREIGN KEY (`kunde_id`) REFERENCES `person_new` (`person_id`),
  CONSTRAINT `auftrag_ibfk_2` FOREIGN KEY (`fahrer_id`) REFERENCES `person_new` (`person_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auftrag`
--

LOCK TABLES `auftrag` WRITE;
/*!40000 ALTER TABLE `auftrag` DISABLE KEYS */;
INSERT INTO `auftrag` VALUES (1,'A202506-0001',1,NULL,'Leipzig','2025-06-03','Storniert','Normal',25.89,19.00,'','','2025-06-02 15:04:46','2025-06-02 15:05:10'),(2,'A202506-0002',1,13,'Leipzig','2025-06-03','Neu','Hoch',207.99,19.00,'','','2025-06-02 16:16:45','2025-06-02 16:32:28'),(3,'A202506-0003',1,13,'Leipzig','2025-06-03','Neu','Normal',62.70,19.00,'','','2025-06-02 17:03:21','2025-06-02 17:03:21');
/*!40000 ALTER TABLE `auftrag` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_auftrag_nummer` BEFORE INSERT ON `auftrag` FOR EACH ROW BEGIN
    IF NEW.auftragsnummer IS NULL OR NEW.auftragsnummer = '' THEN
        SET NEW.auftragsnummer = CONCAT('A', YEAR(NOW()), LPAD(MONTH(NOW()), 2, '0'), '-', LPAD((
            SELECT COALESCE(MAX(CAST(SUBSTRING(auftragsnummer, -4) AS UNSIGNED)), 0) + 1
            FROM auftrag 
            WHERE auftragsnummer LIKE CONCAT('A', YEAR(NOW()), LPAD(MONTH(NOW()), 2, '0'), '-%')
        ), 4, '0'));
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `auftrag_position`
--

DROP TABLE IF EXISTS `auftrag_position`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auftrag_position` (
  `position_id` int NOT NULL AUTO_INCREMENT,
  `auftrag_id` int NOT NULL,
  `ware_id` int NOT NULL,
  `menge` int NOT NULL DEFAULT '1',
  `einzelpreis` decimal(10,2) NOT NULL,
  `gesamtpreis` decimal(10,2) NOT NULL,
  `bemerkung` text,
  `erstellt_am` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`position_id`),
  KEY `idx_position_auftrag` (`auftrag_id`),
  KEY `idx_position_ware` (`ware_id`),
  CONSTRAINT `auftrag_position_ibfk_1` FOREIGN KEY (`auftrag_id`) REFERENCES `auftrag` (`auftrag_id`) ON DELETE CASCADE,
  CONSTRAINT `auftrag_position_ibfk_2` FOREIGN KEY (`ware_id`) REFERENCES `ware` (`ware_id`),
  CONSTRAINT `chk_position_menge` CHECK ((`menge` > 0)),
  CONSTRAINT `chk_position_preise` CHECK (((`einzelpreis` >= 0) and (`gesamtpreis` >= 0)))
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auftrag_position`
--

LOCK TABLES `auftrag_position` WRITE;
/*!40000 ALTER TABLE `auftrag_position` DISABLE KEYS */;
INSERT INTO `auftrag_position` VALUES (1,1,12,1,19.90,19.90,NULL,'2025-06-02 15:04:46'),(2,1,39,1,5.99,5.99,NULL,'2025-06-02 15:04:46'),(5,2,21,1,199.00,199.00,NULL,'2025-06-02 16:32:28'),(6,2,14,1,8.99,8.99,NULL,'2025-06-02 16:32:28'),(7,3,25,1,12.90,12.90,NULL,'2025-06-02 17:03:21'),(8,3,38,2,24.90,49.80,NULL,'2025-06-02 17:03:21');
/*!40000 ALTER TABLE `auftrag_position` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_position_gesamtpreis` BEFORE INSERT ON `auftrag_position` FOR EACH ROW BEGIN
    SET NEW.gesamtpreis = NEW.menge * NEW.einzelpreis;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_position_gesamtpreis_update` BEFORE UPDATE ON `auftrag_position` FOR EACH ROW BEGIN
    SET NEW.gesamtpreis = NEW.menge * NEW.einzelpreis;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `fahrer_details`
--

DROP TABLE IF EXISTS `fahrer_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fahrer_details` (
  `person_id` int NOT NULL,
  `fuehrerscheinklasse` varchar(10) DEFAULT NULL,
  `fahrzeugtyp` varchar(50) DEFAULT NULL,
  `fuehrerschein_nummer` varchar(50) DEFAULT NULL,
  `fuehrerschein_ausgestellt_am` date DEFAULT NULL,
  `fuehrerschein_ablauf_am` date DEFAULT NULL,
  `medizinische_untersuchung_ablauf` date DEFAULT NULL,
  `verfuegbarkeit` enum('Verfuegbar','Nicht_verfuegbar','Urlaub','Krank') DEFAULT 'Verfuegbar',
  PRIMARY KEY (`person_id`),
  KEY `idx_fahrer_details_verfuegbarkeit` (`verfuegbarkeit`),
  KEY `idx_fahrer_details_ablauf` (`fuehrerschein_ablauf_am`),
  CONSTRAINT `fahrer_details_ibfk_1` FOREIGN KEY (`person_id`) REFERENCES `person_new` (`person_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fahrer_details`
--

LOCK TABLES `fahrer_details` WRITE;
/*!40000 ALTER TABLE `fahrer_details` DISABLE KEYS */;
INSERT INTO `fahrer_details` VALUES (9,'B','PKW','12','2025-05-09','2027-05-01','2025-05-12','Nicht_verfuegbar'),(13,'B','PKW','FVG1234','2025-05-06','2026-06-24','2025-05-21','Verfuegbar');
/*!40000 ALTER TABLE `fahrer_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kunde_details`
--

DROP TABLE IF EXISTS `kunde_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kunde_details` (
  `person_id` int NOT NULL,
  `betreuender_mitarbeiter` varchar(100) DEFAULT NULL,
  `kundennummer` varchar(50) DEFAULT NULL,
  `zahlungsziel` int DEFAULT '30',
  `kreditlimit` decimal(10,2) DEFAULT NULL,
  `rabatt_prozent` decimal(5,2) DEFAULT '0.00',
  PRIMARY KEY (`person_id`),
  UNIQUE KEY `kundennummer` (`kundennummer`),
  CONSTRAINT `kunde_details_ibfk_1` FOREIGN KEY (`person_id`) REFERENCES `person_new` (`person_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kunde_details`
--

LOCK TABLES `kunde_details` WRITE;
/*!40000 ALTER TABLE `kunde_details` DISABLE KEYS */;
INSERT INTO `kunde_details` VALUES (1,'Shura','1',30,NULL,0.00);
/*!40000 ALTER TABLE `kunde_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lieferant_details`
--

DROP TABLE IF EXISTS `lieferant_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lieferant_details` (
  `person_id` int NOT NULL,
  `unternehmensform` varchar(50) DEFAULT NULL,
  `ustid` varchar(20) DEFAULT NULL,
  `lieferantennummer` varchar(50) DEFAULT NULL,
  `handelsregisternummer` varchar(50) DEFAULT NULL,
  `bewertung` enum('A','B','C','D') DEFAULT 'C',
  `zahlungskonditionen` varchar(100) DEFAULT NULL,
  `hauptkategorie` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`person_id`),
  UNIQUE KEY `lieferantennummer` (`lieferantennummer`),
  KEY `idx_lieferant_details_bewertung` (`bewertung`),
  KEY `idx_lieferant_details_kategorie` (`hauptkategorie`),
  CONSTRAINT `lieferant_details_ibfk_1` FOREIGN KEY (`person_id`) REFERENCES `person_new` (`person_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lieferant_details`
--

LOCK TABLES `lieferant_details` WRITE;
/*!40000 ALTER TABLE `lieferant_details` DISABLE KEYS */;
INSERT INTO `lieferant_details` VALUES (10,'KG','19872','1','sdc','C','30 Tage','Büromaterial'),(11,'GmbH & Co. KG','123976','09756','HGB6543','B','14 Tage','Technik'),(12,'OHG','9865390','109','HGB63527','C','7 Tage','Fahrzeuge');
/*!40000 ALTER TABLE `lieferant_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `person_new`
--

DROP TABLE IF EXISTS `person_new`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `person_new` (
  `person_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `adresse` varchar(255) DEFAULT NULL,
  `telefon` varchar(50) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `typ` enum('Unternehmen','Privatperson') DEFAULT 'Privatperson',
  `erstellt_am` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `aktiv` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`person_id`),
  KEY `idx_person_typ` (`typ`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `person_new`
--

LOCK TABLES `person_new` WRITE;
/*!40000 ALTER TABLE `person_new` DISABLE KEYS */;
INSERT INTO `person_new` VALUES (1,'Maria Schirr','Leipzig','+491906583251','xSAC@mail.ru','Privatperson','2025-05-30 06:44:32',1),(9,'Koko Chanel','Münchenweg 1','098762','koko@mail.ru','Unternehmen','2025-05-30 08:04:04',1),(10,'Rehus Logistik','KLK','018763','rhe@mail.ri','Unternehmen','2025-05-30 13:31:00',1),(11,'Axing','France 1','0074319','axing@mail.ru','Unternehmen','2025-05-30 13:56:28',1),(12,'Fahrzeugteile Berlin','Berlin, Leipzigerstr. 1','08753810','fahrberlin@mail.de','Unternehmen','2025-05-31 17:00:15',1),(13,'Kusua Bonn','Berlin, Straßburgerweg 34','049872439','kusb@mail.de','Privatperson','2025-06-02 15:07:42',1);
/*!40000 ALTER TABLE `person_new` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `person_rolle`
--

DROP TABLE IF EXISTS `person_rolle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `person_rolle` (
  `person_id` int NOT NULL,
  `rolle` enum('Kunde','Lieferant','Fahrer','Mitarbeiter') NOT NULL,
  `aktiv` tinyint(1) DEFAULT '1',
  `seit_datum` date DEFAULT (curdate()),
  `bis_datum` date DEFAULT NULL,
  `notizen` text,
  PRIMARY KEY (`person_id`,`rolle`),
  KEY `idx_person_rolle_aktiv` (`rolle`,`aktiv`),
  CONSTRAINT `person_rolle_ibfk_1` FOREIGN KEY (`person_id`) REFERENCES `person_new` (`person_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `person_rolle`
--

LOCK TABLES `person_rolle` WRITE;
/*!40000 ALTER TABLE `person_rolle` DISABLE KEYS */;
INSERT INTO `person_rolle` VALUES (1,'Kunde',1,'2025-05-30',NULL,NULL),(9,'Fahrer',1,'2025-05-30',NULL,NULL),(10,'Lieferant',1,'2025-05-30',NULL,NULL),(11,'Lieferant',1,'2025-05-30',NULL,NULL),(12,'Lieferant',1,'2025-05-31',NULL,NULL),(13,'Fahrer',1,'2025-06-02',NULL,NULL);
/*!40000 ALTER TABLE `person_rolle` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ware`
--

DROP TABLE IF EXISTS `ware`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ware` (
  `ware_id` int NOT NULL AUTO_INCREMENT,
  `artikelnummer` varchar(50) NOT NULL,
  `bezeichnung` varchar(200) NOT NULL,
  `beschreibung` text,
  `kategorie_id` int DEFAULT NULL,
  `einheit` enum('Stueck','kg','Liter','Meter','Quadratmeter','Kubikmeter','Tonne','Gramm','Paket') DEFAULT 'Stueck',
  `einkaufspreis` decimal(10,2) DEFAULT NULL,
  `verkaufspreis` decimal(10,2) DEFAULT NULL,
  `mindestbestand` int DEFAULT '0',
  `aktueller_bestand` int DEFAULT '0',
  `lieferant_id` int DEFAULT NULL,
  `lagerort` varchar(100) DEFAULT NULL,
  `lieferzeit_tage` int DEFAULT '7',
  `aktiv` tinyint(1) DEFAULT '1',
  `erstellt_am` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `geaendert_am` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ware_id`),
  UNIQUE KEY `artikelnummer` (`artikelnummer`),
  KEY `idx_ware_artikelnummer` (`artikelnummer`),
  KEY `idx_ware_bezeichnung` (`bezeichnung`),
  KEY `idx_ware_kategorie` (`kategorie_id`),
  KEY `idx_ware_lieferant` (`lieferant_id`),
  KEY `idx_ware_aktiv` (`aktiv`),
  KEY `idx_ware_mindestbestand` (`mindestbestand`,`aktueller_bestand`),
  CONSTRAINT `ware_ibfk_1` FOREIGN KEY (`kategorie_id`) REFERENCES `ware_kategorie` (`kategorie_id`),
  CONSTRAINT `ware_ibfk_2` FOREIGN KEY (`lieferant_id`) REFERENCES `person_new` (`person_id`),
  CONSTRAINT `chk_bestand` CHECK (((`aktueller_bestand` >= 0) and (`mindestbestand` >= 0))),
  CONSTRAINT `chk_preise` CHECK (((`einkaufspreis` >= 0) and (`verkaufspreis` >= 0)))
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ware`
--

LOCK TABLES `ware` WRITE;
/*!40000 ALTER TABLE `ware` DISABLE KEYS */;
INSERT INTO `ware` VALUES (1,'123','Monitor','32 Zoll',8,'Stueck',190.00,280.00,10,0,10,'Komsa',7,1,'2025-05-30 13:50:27','2025-05-30 13:50:27'),(2,'BM001','Kopierpapier A4','Weißes Kopierpapier 80g 500 Blatt',NULL,'Stueck',0.00,4.99,50,45,NULL,'Bürolager',7,1,'2025-05-30 13:52:13','2025-05-30 13:52:13'),(3,'BM002','Kugelschreiber blau','Kugelschreiber mit blauer Tinte 10er Pack',NULL,'Stueck',0.00,8.50,30,23,NULL,'Bürolager',7,1,'2025-05-30 13:52:13','2025-05-30 13:52:13'),(4,'BM003','Ordner A4 breit','Ordner DIN A4 8cm breit grau',NULL,'Stueck',0.00,3.25,40,67,NULL,'Bürolager',7,1,'2025-05-30 13:52:13','2025-05-30 13:52:13'),(5,'BM004','Haftnotizen gelb','Haftnotizen 75x75mm gelb 100 Blatt',NULL,'Stueck',0.00,2.99,25,12,NULL,'Bürolager',7,1,'2025-05-30 13:52:13','2025-05-30 13:52:13'),(6,'BM005','Taschenrechner','Elektronischer Taschenrechner 12-stellig',NULL,'Stueck',0.00,15.99,10,8,NULL,'Bürolager',7,1,'2025-05-30 13:52:13','2025-05-30 13:52:13'),(7,'FZ001','Motoröl 5W30','Vollsynthetisches Motoröl 5W30 5 Liter',NULL,'Stueck',0.00,45.90,30,28,NULL,'Werkstatt',7,1,'2025-05-30 13:52:13','2025-05-30 13:52:13'),(8,'FZ002','Bremsbeläge vorne','Bremsbeläge Vorderachse Universal',5,'Stueck',0.00,89.99,20,15,11,'Werkstatt',8,1,'2025-05-30 13:52:13','2025-05-30 13:59:08'),(9,'FZ003','Luftfilter','Luftfilter für LKW verschiedene Modelle',NULL,'Stueck',0.00,24.50,25,22,NULL,'Werkstatt',7,1,'2025-05-30 13:52:13','2025-05-30 13:52:13'),(10,'FZ004','Scheibenwischer','Scheibenwischer 65cm Universal',NULL,'Stueck',0.00,12.90,15,18,NULL,'Werkstatt',7,1,'2025-05-30 13:52:13','2025-05-30 13:52:13'),(11,'FZ005','Batterie 12V','LKW-Batterie 12V 180Ah',2,'Stueck',0.00,189.00,8,6,12,'Werkstatt',7,1,'2025-05-30 13:52:13','2025-06-02 14:36:12'),(12,'BS001','Diesel AdBlue','AdBlue Harnstofflösung 10 Liter Kanister',3,'Stueck',0.00,19.90,100,85,NULL,'Tankstelle',7,1,'2025-05-30 13:52:13','2025-05-31 16:55:01'),(13,'BS002','Frostschutz','Kühlerfrostschutz -40°C 5 Liter',3,'Stueck',0.00,12.50,40,34,NULL,'Werkstatt',7,1,'2025-05-30 13:52:13','2025-05-31 16:55:26'),(14,'BS003','WD-40 Spray','Kriechöl und Rostlöser 400ml Spray',NULL,'Stueck',0.00,8.99,50,42,NULL,'Werkstatt',7,1,'2025-05-30 13:52:13','2025-05-30 13:52:13'),(15,'VE001','Kartons 40x30x20','Versandkartons 40x30x20cm braun',NULL,'Stueck',0.00,1.25,200,145,NULL,'Packhalle',7,1,'2025-05-30 13:52:13','2025-05-30 13:52:13'),(16,'VE002','Klebeband transparent','Paketklebeband 50mm x 50m transparent',NULL,'Stueck',0.00,2.80,100,89,NULL,'Packhalle',7,1,'2025-05-30 13:52:13','2025-05-30 13:52:13'),(17,'VE003','Luftpolsterfolie','Luftpolsterfolie 100cm breit 50m Rolle',NULL,'Stueck',0.00,24.90,15,12,NULL,'Packhalle',7,1,'2025-05-30 13:52:13','2025-05-30 13:52:13'),(18,'VE0041','Adressetiketten','Adressetiketten weiß 99x57mm 250 Stück',4,'Stueck',0.00,9.99,80,78,10,'Bürolager',7,1,'2025-05-30 13:52:13','2025-05-31 16:53:45'),(19,'VE005','Füllmaterial','Füllmaterial aus Papier 10kg Sack',4,'Stueck',0.00,18.50,30,23,NULL,'Packhalle',7,1,'2025-05-30 13:52:13','2025-05-31 16:55:47'),(20,'TE001','Barcode Scanner','USB Barcode Scanner für Lagerverwaltung',5,'Stueck',0.00,89.00,5,4,NULL,'IT-Lager',7,1,'2025-05-30 13:52:13','2025-05-31 16:54:27'),(21,'TE002','Tablet 10 Zoll','Android Tablet für Tourenplanung',NULL,'Stueck',0.00,199.00,10,8,NULL,'IT-Lager',7,1,'2025-05-30 13:52:13','2025-05-30 13:52:13'),(22,'TE003','Funkgerät Set','Handfunkgerät 2er Set mit Ladestationen',5,'Stueck',0.00,145.00,8,6,NULL,'IT-Lager',7,1,'2025-05-30 13:52:13','2025-05-31 16:55:55'),(23,'TE004','USB Stick 32GB','USB Stick 32GB USB 3.0 schwarz',NULL,'Stueck',0.00,12.99,30,25,NULL,'IT-Lager',7,1,'2025-05-30 13:52:13','2025-05-30 13:52:13'),(24,'TE005','Ethernet Kabel','Netzwerkkabel Cat6 5m grau',5,'Stueck',3.00,8.50,20,15,NULL,'IT-Lager',7,1,'2025-05-30 13:52:13','2025-06-02 14:37:33'),(25,'RE001','Allzweckreiniger','Allzweckreiniger 5 Liter Kanister',6,'Stueck',0.00,12.90,25,18,11,'Reinigungslager',7,1,'2025-05-30 13:52:13','2025-05-31 16:56:37'),(26,'RE002','Toilettenpapier','Toilettenpapier 3-lagig 24 Rollen',NULL,'Stueck',0.00,18.99,60,56,NULL,'Reinigungslager',7,1,'2025-05-30 13:52:13','2025-05-30 13:52:13'),(27,'RE003','Handdseife','Flüssige Handseife 5 Liter Nachfüllpack',NULL,'Stueck',0.00,15.50,15,9,NULL,'Reinigungslager',7,1,'2025-05-30 13:52:13','2025-05-30 13:52:13'),(28,'RE004','Müllsäcke 120L','Müllsäcke 120 Liter schwarz 25 Stück',NULL,'Stueck',0.00,8.99,70,67,NULL,'Reinigungslager',7,1,'2025-05-30 13:52:13','2025-05-30 13:52:13'),(29,'WZ001','Schraubenschlüssel Set','Schraubenschlüssel Set 8-19mm 6-teilig',NULL,'Stueck',0.00,34.90,15,12,NULL,'Werkstatt',7,1,'2025-05-30 13:52:13','2025-05-30 13:52:13'),(30,'WZ002','Akkuschrauber','Akkuschrauber 18V mit 2 Akkus',7,'Stueck',0.00,89.00,8,5,11,'Werkstatt',7,1,'2025-05-30 13:52:13','2025-05-31 16:54:41'),(31,'WZ003','Zange Seitenschneider','Seitenschneider 160mm isoliert',NULL,'Stueck',0.00,18.50,20,14,NULL,'Werkstatt',7,1,'2025-05-30 13:52:13','2025-05-30 13:52:13'),(32,'WZ004','Hammer 500g','Schlosserhammer 500g mit Holzstiel',NULL,'Stueck',0.00,12.99,25,22,NULL,'Werkstatt',7,1,'2025-05-30 13:52:13','2025-05-30 13:52:13'),(33,'WZ005','Maßband 5m','Maßband 5m mit Stopp-Funktion',NULL,'Stueck',0.00,9.99,20,18,NULL,'Werkstatt',7,1,'2025-05-30 13:52:13','2025-05-30 13:52:13'),(34,'SI001','Warnwesten','Warnwesten EN471 orange Größe L',NULL,'Stueck',0.00,8.50,50,45,NULL,'Sicherheitslager',7,1,'2025-05-30 13:52:13','2025-05-30 13:52:13'),(35,'SI002','Schutzhelm weiß','Bauhelm weiß verstellbar',NULL,'Stueck',0.00,15.90,30,23,NULL,'Sicherheitslager',7,1,'2025-05-30 13:52:13','2025-05-30 13:52:13'),(36,'SI003','Arbeitshandschuhe','Arbeitshandschuhe Größe 9 grau',8,'Stueck',0.00,4.99,80,67,NULL,'Sicherheitslager',7,1,'2025-05-30 13:52:13','2025-05-31 16:54:17'),(37,'SI004','Sicherheitsschuhe','Sicherheitsschuhe S3 schwarz Größe 42',NULL,'Stueck',0.00,69.00,12,8,NULL,'Sicherheitslager',7,1,'2025-05-30 13:52:13','2025-05-30 13:52:13'),(38,'SI005','Erste Hilfe Kasten','KFZ Erste Hilfe Kasten DIN 13164',8,'Stueck',0.00,24.90,20,14,NULL,'Sicherheitslager',7,1,'2025-05-30 13:52:13','2025-05-31 16:55:07'),(39,'SO001','Kabelbinder 200mm','Kabelbinder 200mm schwarz 100 Stück',NULL,'Stueck',0.00,5.99,50,45,NULL,'Elektrolager',7,1,'2025-05-30 13:52:13','2025-05-30 13:52:13'),(40,'SO002','Isolierband','Isolierband schwarz 19mm x 20m',NULL,'Stueck',0.00,2.50,40,34,NULL,'Elektrolager',7,1,'2025-05-30 13:52:13','2025-05-30 13:52:13'),(41,'SO003','Sprühfett','Sprühfett 400ml Spray für Scharniere',NULL,'Stueck',0.00,7.99,35,28,NULL,'Werkstatt',7,1,'2025-05-30 13:52:13','2025-05-30 13:52:13'),(42,'SO004','Gummistiefel','Gummistiefel schwarz Größe 43',9,'Stueck',0.00,24.90,10,6,NULL,'Bekleidungslager',7,1,'2025-05-30 13:52:13','2025-05-31 16:56:08'),(43,'SO005','Taschenlampe LED','LED Taschenlampe wasserdicht mit Batterien',NULL,'Stueck',0.00,18.99,20,16,NULL,'Werkstatt',7,1,'2025-05-30 13:52:13','2025-05-30 13:52:13');
/*!40000 ALTER TABLE `ware` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ware_kategorie`
--

DROP TABLE IF EXISTS `ware_kategorie`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ware_kategorie` (
  `kategorie_id` int NOT NULL AUTO_INCREMENT,
  `kategorie_name` varchar(100) NOT NULL,
  `beschreibung` text,
  `aktiv` tinyint(1) DEFAULT '1',
  `erstellt_am` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`kategorie_id`),
  UNIQUE KEY `kategorie_name` (`kategorie_name`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ware_kategorie`
--

LOCK TABLES `ware_kategorie` WRITE;
/*!40000 ALTER TABLE `ware_kategorie` DISABLE KEYS */;
INSERT INTO `ware_kategorie` VALUES (1,'Büromaterial','Büroartikel und Verwaltungsbedarf',1,'2025-05-30 13:40:27'),(2,'Fahrzeugteile','Ersatzteile und Zubehör für Fahrzeuge',1,'2025-05-30 13:40:27'),(3,'Betriebsstoffe','Kraftstoffe, Öle und Schmiermittel',1,'2025-05-30 13:40:27'),(4,'Verpackung','Kartons, Folien und Verpackungsmaterial',1,'2025-05-30 13:40:27'),(5,'Technik','IT-Hardware und technische Geräte',1,'2025-05-30 13:40:27'),(6,'Reinigung','Reinigungsmittel und Hygieneartikel',1,'2025-05-30 13:40:27'),(7,'Werkzeuge','Handwerkzeuge und Maschinen',1,'2025-05-30 13:40:27'),(8,'Sicherheit','Arbeitsschutz und Sicherheitsausrüstung',1,'2025-05-30 13:40:27'),(9,'Sonstiges','Verschiedene Artikel',1,'2025-05-30 13:40:27');
/*!40000 ALTER TABLE `ware_kategorie` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-02 20:19:13
