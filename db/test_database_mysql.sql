-- MySQL dump 10.13  Distrib 5.7.17, for osx10.12 (x86_64)
--
-- Host: localhost    Database: ontolobridge_test
-- ------------------------------------------------------
-- Server version	5.7.17

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
-- Table structure for table `maintainers`
--

DROP TABLE IF EXISTS `maintainers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `maintainers` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  `contact_location` varchar(1024) NOT NULL,
  `contact_method` varchar(16) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1004 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `maintainers`
--

LOCK TABLES `maintainers` WRITE;
/*!40000 ALTER TABLE `maintainers` DISABLE KEYS */;
INSERT INTO `maintainers` VALUES (1000,'John Turner','','email'),(1001,'Danniel Cooper','','email'),(1002,'Michael Dorf','mdorf@stanford.edu','email'),(1003,'Hande Küçük-McGinty','','email');
/*!40000 ALTER TABLE `maintainers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notifications` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `notification_method` varchar(16) NOT NULL,
  `address` varchar(1024) NOT NULL,
  `title` varchar(512) DEFAULT NULL,
  `message` text,
  `sent` tinyint(1) NOT NULL DEFAULT '0',
  `created_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `sent_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_sent` (`sent`)
) ENGINE=MyISAM AUTO_INCREMENT=1000 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ontologies`
--

DROP TABLE IF EXISTS `ontologies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ontologies` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `ontology_short` varchar(64) NOT NULL,
  `name` varchar(256) NOT NULL,
  `url` varchar(1024) DEFAULT NULL,
  `created_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1003 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ontologies`
--

LOCK TABLES `ontologies` WRITE;
/*!40000 ALTER TABLE `ontologies` DISABLE KEYS */;
INSERT INTO `ontologies` VALUES (1000,'BAO','BioAssay Ontology',NULL,'2019-10-31 15:09:26','2019-10-31 15:09:26'),(1001,'DTO','Drug Target Ontology',NULL,'2019-10-31 15:09:26','2019-10-31 15:09:26'),(1002,'???','Unknown Ontology',NULL,'2019-10-31 15:09:26','2019-10-31 15:09:26');
/*!40000 ALTER TABLE `ontologies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ontology_to_maintainer`
--

DROP TABLE IF EXISTS `ontology_to_maintainer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ontology_to_maintainer` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `ontology_id` int(11) unsigned NOT NULL,
  `maintainer_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_unique_ont_id_maint_id` (`ontology_id`,`maintainer_id`)
) ENGINE=MyISAM AUTO_INCREMENT=1009 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ontology_to_maintainer`
--

LOCK TABLES `ontology_to_maintainer` WRITE;
/*!40000 ALTER TABLE `ontology_to_maintainer` DISABLE KEYS */;
INSERT INTO `ontology_to_maintainer` VALUES (1000,0,1),(1001,0,2),(1002,0,3),(1003,1,1),(1004,1,2),(1005,1,3),(1006,2,1),(1007,2,2),(1008,2,3);
/*!40000 ALTER TABLE `ontology_to_maintainer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `request_status`
--

DROP TABLE IF EXISTS `request_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `request_status` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `request_id` int(11) unsigned NOT NULL,
  `current_status` varchar(32) NOT NULL,
  `updated_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1004 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `request_status`
--

LOCK TABLES `request_status` WRITE;
/*!40000 ALTER TABLE `request_status` DISABLE KEYS */;
INSERT INTO `request_status` VALUES (1000,1000,'submitted','2019-10-31 15:09:26'),(1001,1001,'submitted','2019-11-13 11:56:46'),(1002,1002,'submitted','2019-11-15 15:20:42'),(1003,1003,'submitted','2019-11-15 15:22:48');
/*!40000 ALTER TABLE `request_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `requests`
--

DROP TABLE IF EXISTS `requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `requests` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `label` varchar(256) NOT NULL,
  `description` text,
  `superclass_ontology` varchar(64) NOT NULL,
  `superclass_id` int(11) unsigned NOT NULL,
  `reference` text,
  `justification` text,
  `submitter` varchar(256) DEFAULT NULL,
  `uri_ontology` varchar(256) DEFAULT NULL,
  `uri_identifier` varchar(256) DEFAULT NULL,
  `current_message` text,
  `created_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `submission_status` varchar(32) NOT NULL DEFAULT 'submitted',
  `full_uri` varchar(1024) DEFAULT NULL,
  `uri_superclass` varchar(256) DEFAULT NULL,
  `submitter_email` varchar(256) DEFAULT NULL,
  `notify` tinyint(1) NOT NULL DEFAULT '0',
  `request_type` varchar(16) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1004 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `requests`
--

LOCK TABLES `requests` WRITE;
/*!40000 ALTER TABLE `requests` DISABLE KEYS */;
INSERT INTO `requests` VALUES (1000,'test','testClass','BAO',1,NULL,NULL,'johgn','???',NULL,NULL,'2019-10-31 15:09:26','2019-10-31 15:09:26','submitted',NULL,NULL,'mdorf@stanford.edu',1,'term'),(1001,'misha term','test term mysql','BAO',3114,'','','','???',NULL,NULL,'2019-11-13 11:56:46','2019-11-15 15:20:00','submitted',NULL,'BAO_0003114','',0,'term'),(1002,'ann_prop_1','annotation property request','BAO',3114,'','','','GO',NULL,NULL,'2019-11-15 15:20:42','2019-11-15 15:20:42','submitted',NULL,'http://www.bioassayontology.org/bao#BAO_0003114','mdorf@stanford.edu',0,'Annotation'),(1003,'ann_prop_2','another annotation prop','BAO',3122,'','','','GO',NULL,NULL,'2019-11-15 15:22:48','2019-11-15 15:22:48','submitted',NULL,'http://www.bioassayontology.org/bao#BAO_0003122','mdorf@stanford.edu',0,'Annotation');
/*!40000 ALTER TABLE `requests` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-11-15 15:44:27
