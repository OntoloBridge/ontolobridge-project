/*
Navicat PGSQL Data Transfer

Source Server         : BenderTest
Source Server Version : 90223
Source Host           : bender.ccs.miami.edu:5433
Source Database       : ontolobridge_test
Source Schema         : public

Target Server Type    : PGSQL
Target Server Version : 90223
File Encoding         : 65001

Date: 2019-10-21 15:21:42
*/


-- ----------------------------
-- Sequence structure for maintainers_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."maintainers_id_seq";
CREATE SEQUENCE "public"."maintainers_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 3
 CACHE 1;
SELECT setval('"public"."maintainers_id_seq"', 3, true);

-- ----------------------------
-- Sequence structure for notifications_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."notifications_id_seq";
CREATE SEQUENCE "public"."notifications_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 70
 CACHE 1;
SELECT setval('"public"."notifications_id_seq"', 70, true);

-- ----------------------------
-- Sequence structure for ontologies_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ontologies_id_seq";
CREATE SEQUENCE "public"."ontologies_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 2
 CACHE 1;
SELECT setval('"public"."ontologies_id_seq"', 2, true);

-- ----------------------------
-- Sequence structure for ontology_to_maintainer_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."ontology_to_maintainer_id_seq";
CREATE SEQUENCE "public"."ontology_to_maintainer_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 10
 CACHE 1;
SELECT setval('"public"."ontology_to_maintainer_id_seq"', 10, true);

-- ----------------------------
-- Sequence structure for requests_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."requests_id_seq";
CREATE SEQUENCE "public"."requests_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 141
 CACHE 1;
SELECT setval('"public"."requests_id_seq"', 141, true);

-- ----------------------------
-- Table structure for maintainers
-- ----------------------------
DROP TABLE IF EXISTS "public"."maintainers";
CREATE TABLE "public"."maintainers" (
"id" int4 DEFAULT nextval('maintainers_id_seq'::regclass) NOT NULL,
"name" varchar(255) COLLATE "default",
"contact_location" varchar(255) COLLATE "default",
"contact_method" varchar(255) COLLATE "default"
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of maintainers
-- ----------------------------
INSERT INTO "public"."maintainers" VALUES ('1', 'John Turner', '', 'email');
INSERT INTO "public"."maintainers" VALUES ('2', 'Danniel Cooper', '', 'email');
INSERT INTO "public"."maintainers" VALUES ('3', 'Hande ', '', 'email');

-- ----------------------------
-- Table structure for notifications
-- ----------------------------
DROP TABLE IF EXISTS "public"."notifications";
CREATE TABLE "public"."notifications" (
"id" int8 DEFAULT nextval('notifications_id_seq'::regclass) NOT NULL,
"type" varchar(255) COLLATE "default",
"address" varchar(255) COLLATE "default",
"message" text COLLATE "default",
"sent" int2 DEFAULT 0 NOT NULL,
"createDate" date DEFAULT now(),
"sentDate" date,
"title" varchar(255) COLLATE "default"
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of notifications
-- ----------------------------

-- ----------------------------
-- Table structure for ontologies
-- ----------------------------
DROP TABLE IF EXISTS "public"."ontologies";
CREATE TABLE "public"."ontologies" (
"id" int4 DEFAULT nextval('ontologies_id_seq'::regclass) NOT NULL,
"name" varchar(255) COLLATE "default",
"url" varchar(255) COLLATE "default",
"ontology_short" varchar(255) COLLATE "default" NOT NULL,
"last_updated" date DEFAULT ('now'::text)::date
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of ontologies
-- ----------------------------
INSERT INTO "public"."ontologies" VALUES ('0', 'Unknown Ontology', null, '???', '2019-03-12');
INSERT INTO "public"."ontologies" VALUES ('1', 'BioAssay Ontology', null, 'BAO', '2019-03-12');
INSERT INTO "public"."ontologies" VALUES ('2', 'Drug Target Ontology', null, 'DTO', '2019-03-12');

-- ----------------------------
-- Table structure for ontology_to_maintainer
-- ----------------------------
DROP TABLE IF EXISTS "public"."ontology_to_maintainer";
CREATE TABLE "public"."ontology_to_maintainer" (
"id" int4 DEFAULT nextval('ontology_to_maintainer_id_seq'::regclass) NOT NULL,
"ontology_id" int4 NOT NULL,
"maintainer_id" int4 NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of ontology_to_maintainer
-- ----------------------------
INSERT INTO "public"."ontology_to_maintainer" VALUES ('1', '0', '1');
INSERT INTO "public"."ontology_to_maintainer" VALUES ('2', '0', '2');
INSERT INTO "public"."ontology_to_maintainer" VALUES ('3', '0', '3');
INSERT INTO "public"."ontology_to_maintainer" VALUES ('4', '1', '1');
INSERT INTO "public"."ontology_to_maintainer" VALUES ('5', '1', '2');
INSERT INTO "public"."ontology_to_maintainer" VALUES ('6', '1', '3');
INSERT INTO "public"."ontology_to_maintainer" VALUES ('7', '2', '1');
INSERT INTO "public"."ontology_to_maintainer" VALUES ('8', '2', '2');
INSERT INTO "public"."ontology_to_maintainer" VALUES ('9', '2', '3');

-- ----------------------------
-- Table structure for requests
-- ----------------------------
DROP TABLE IF EXISTS "public"."requests";
CREATE TABLE "public"."requests" (
"id" int2 DEFAULT nextval('requests_id_seq'::regclass) NOT NULL,
"label" varchar(255) COLLATE "default",
"description" text COLLATE "default",
"superclass_ontology" varchar(8) COLLATE "default",
"superclass_id" int4,
"references" text COLLATE "default",
"justification" text COLLATE "default",
"submitter" varchar(255) COLLATE "default",
"uri_ontology" varchar(8) COLLATE "default",
"uri_identifier" varchar(255) COLLATE "default",
"current_message" text COLLATE "default",
"date" timestamp(6) DEFAULT now(),
"last_updated" timestamp(6) DEFAULT now(),
"status" "public"."status" DEFAULT 'submitted'::status,
"uri" varchar(255) COLLATE "default",
"superclass_uri" varchar(255) COLLATE "default",
"submitter_email" varchar(255) COLLATE "default",
"notify" int2,
"type" varchar(255) COLLATE "default"
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of requests
-- ----------------------------
INSERT INTO "public"."requests" VALUES ('21', 'test', 'testClass', null, '45', null, '', 'johgn', '???', null, null, '2017-12-12 11:16:56.079845', '2017-12-12 11:16:56.079845', 'submitted', null, null, '', null, 'term');
INSERT INTO "public"."requests" VALUES ('22', 'test', 'test', 'est', '1', 'test', 'test', 'test', '???', null, null, '2018-01-08 11:29:49.457237', '2018-01-08 11:29:49.457237', 'submitted', null, null, '', null, 'term');
INSERT INTO "public"."requests" VALUES ('23', 'test', 'test', 'est', '2', 'test', 'test', 'test', '???', null, '', '2018-01-08 11:34:00.710691', '2018-01-08 11:34:00.710691', 'submitted', null, null, '', null, 'term');
INSERT INTO "public"."requests" VALUES ('24', 'test', 'test', 'est', '1', 'test', 'test', 'test', '???', null, '', '2018-01-08 11:34:10.06363', '2018-01-08 11:34:10.06363', 'accepted', null, null, '', null, 'term');
INSERT INTO "public"."requests" VALUES ('25', 'test2', 'test', 'est', '1', 'test', 'test', 'test', '???', null, '', '2018-01-08 11:34:19.414569', '2018-01-08 11:34:19.414569', 'rejected', null, null, '', null, 'term');
INSERT INTO "public"."requests" VALUES ('26', 'Test Term', 'This is a Test', 'est', '1000', 'none', 'N/A', 'John', '???', null, 'test', '2018-01-18 12:11:49.138629', '2018-01-18 12:11:49.138629', 'submitted', null, null, '', null, 'term');
INSERT INTO "public"."requests" VALUES ('27', 'Test BAO Term', 'Some description', 'AO', '1', '', '', 'John', '???', null, null, '2018-01-26 15:08:12.526242', '2018-01-26 15:08:12.526242', 'submitted', null, '', '', null, 'term');
INSERT INTO "public"."requests" VALUES ('28', 'Test Bao Term 2', 'Another Description', 'AO', '3112', '', '', '', '???', null, null, '2018-01-26 15:09:28.63297', '2018-01-26 15:09:28.63297', 'submitted', null, 'http://www.bioassayontology.org/bao#BAO_0003112', '', null, 'term');
INSERT INTO "public"."requests" VALUES ('29', 'test ter m3', 'why ', 'BAO', '3112', '', '', 'john', 'BAO', null, null, '2018-01-26 15:18:45.952151', '2018-01-26 15:18:45.952151', 'submitted', null, 'http://www.bioassayontology.org/bao#BAO_0003112', '', null, 'term');
INSERT INTO "public"."requests" VALUES ('30', 'mish test term', 'great new term', null, '20', null, null, null, '???', null, null, '2018-01-31 20:29:41.331944', '2018-01-31 20:29:41.331944', 'submitted', null, 'http://dev3.ccs.miami.edu:8080/ontolobridge#ONTB_000000020', '', null, 'term');
INSERT INTO "public"."requests" VALUES ('31', 'Misha Test Term', 'This is a new term requested by misha', null, '3', '', '', '', '???', null, null, '2018-02-01 19:54:17.40911', '2018-02-01 19:54:17.40911', 'submitted', null, 'http://dev3.ccs.miami.edu:8080/ontolobridge#ONTB_000000003', '', null, 'term');
INSERT INTO "public"."requests" VALUES ('40', 'Misha Term 6', 'This is a term 6 description', null, '22', 'www.yahoo.com', 'this term is required for our new project', '', '???', null, null, '2018-02-08 12:34:50.156131', '2018-02-08 12:34:50.156131', 'submitted', null, 'http://dev3.ccs.miami.edu:8080/ontolobridge#ONTB_000000022', '', '1', 'term');
INSERT INTO "public"."requests" VALUES ('41', 'Misha', 'test term', null, '5', 'yahoo.com', '', '', '???', null, null, '2018-02-08 16:53:40.505469', '2018-02-08 16:53:40.505469', 'submitted', null, 'http://dev3.ccs.miami.edu:8080/ontolobridge#ONTB_000000005', '', '1', 'term');
INSERT INTO "public"."requests" VALUES ('42', '11', 'test term', null, '5', 'yahoo.com', '', '', '???', null, null, '2018-02-08 17:00:28.436835', '2018-02-08 17:00:28.436835', 'submitted', null, 'http://dev3.ccs.miami.edu:8080/ontolobridge#ONTB_000000005', '', '1', 'term');
INSERT INTO "public"."requests" VALUES ('43', 'Misha''s new term', 'this is my new term', null, '3', 'google.com', 'need this for my projecdt', '', '???', null, '', '2018-02-09 13:21:42.305877', '2018-02-09 13:21:42.305877', 'rejected', null, 'http://dev3.ccs.miami.edu:8080/ontolobridge#ONTB_000000003', '', '1', 'term');
INSERT INTO "public"."requests" VALUES ('44', 'has positive control', 'The positive control for an assay.', 'bao:BAO', '740', '', '', 'Alex Clark', '???', null, null, '2018-03-09 15:50:09.875016', '2018-03-09 15:50:09.875016', 'submitted', null, '', '', '0', 'term');
INSERT INTO "public"."requests" VALUES ('45', 'has negative control', 'The negative control for an assay.', 'bao', '740', '', '', 'Alex Clark', 'BAO', null, null, '2018-03-09 15:51:06.107176', '2018-03-09 15:51:06.107176', 'submitted', null, null, '', '0', 'term');
INSERT INTO "public"."requests" VALUES ('46', 'Test Class 2', 'test', 'BAO', '740', 'test', 'test', 'test', 'BAO', null, null, '2018-05-31 14:48:25.279981', '2018-05-31 14:48:25.279981', 'submitted', null, null, '', '0', 'term');
INSERT INTO "public"."requests" VALUES ('70', 'apr17 term', 'Term of April 17', null, '0', '', '', '', '', null, null, '2019-04-17 19:22:11.40765', '2019-04-17 19:22:11.40765', 'submitted', null, 'http://www.yahoo.com', '', '0', 'term');
INSERT INTO "public"."requests" VALUES ('71', 'apr17 20term', 'Term of April 17', null, '0', '', '', '', '', null, null, '2019-04-17 19:22:53.182586', '2019-04-17 19:22:53.182586', 'submitted', null, 'http://www.yahoo.com', '', '0', 'term');
INSERT INTO "public"."requests" VALUES ('72', 'apr17 20term', 'Term 20of 20Apri 2017', null, '0', '', '', '', '', null, null, '2019-04-17 19:23:17.803182', '2019-04-17 19:23:17.803182', 'submitted', null, 'http://www.yahoo.com', '', '0', 'term');
INSERT INTO "public"."requests" VALUES ('73', 'apr17 20term', 'Term 20of 20Apri 2017', null, '0', '', '', '', '', null, null, '2019-04-17 19:23:44.394167', '2019-04-17 19:23:44.394167', 'submitted', null, 'http://www.yahoo.com', '', '0', 'term');
INSERT INTO "public"."requests" VALUES ('74', 'apr17 20term', 'Term 20of 20Apri 2017', null, '0', '', '', '', '', null, null, '2019-04-17 20:23:16.243623', '2019-04-17 20:23:16.243623', 'submitted', null, 'http://www.yahoo.com', '', '0', 'term');
INSERT INTO "public"."requests" VALUES ('75', 'apr17 20term', 'Term 20of 20Apri 2017', null, '0', '', '', '', '', null, null, '2019-04-17 20:27:09.397955', '2019-04-17 20:27:09.397955', 'submitted', null, '', '', '0', 'term');
INSERT INTO "public"."requests" VALUES ('76', 'term apr 14 15:30', 'term apr 14 15:30', null, '0', '', '', '', '', null, null, '2019-04-18 17:55:04.208383', '2019-04-18 17:55:04.208383', 'submitted', null, 'http://www.yahoo.com', '', '0', 'term');
INSERT INTO "public"."requests" VALUES ('77', 'term apr 14 15:30', 'term apr 14 15:30', null, '0', '', '', '', '', null, null, '2019-04-18 17:55:16.660597', '2019-04-18 17:55:16.660597', 'submitted', null, 'http://www.yahoo.com', '', '0', 'term');
INSERT INTO "public"."requests" VALUES ('89', 'dd', '', null, '0', '', '', 'Michael Dorf', 'OPTIMAL', null, null, '2019-04-19 16:39:29.02824', '2019-04-19 16:39:29.02824', 'submitted', null, 'http://webprotege.stanford.edu/RB7q67Gf4JtjKTY4hLsKWOU', '', '0', 'term');
INSERT INTO "public"."requests" VALUES ('90', '', '', null, '0', '', '', 'Michael Dorf', 'OPTIMAL', null, null, '2019-04-19 16:43:05.058351', '2019-04-19 16:43:05.058351', 'submitted', null, 'http://webprotege.stanford.edu/RB7q67Gf4JtjKTY4hLsKWOU', '', '0', 'term');
INSERT INTO "public"."requests" VALUES ('91', 'dd', '', null, '0', '', '', 'Michael Dorf', 'OPTIMAL', null, null, '2019-04-19 16:50:41.486485', '2019-04-19 16:50:41.486485', 'submitted', null, 'http://webprotege.stanford.edu/RB7q67Gf4JtjKTY4hLsKWOU', '', '0', 'term');
INSERT INTO "public"."requests" VALUES ('92', '', '', null, '0', '', '', 'Michael Dorf', 'OPTIMAL', null, null, '2019-04-19 16:52:50.992632', '2019-04-19 16:52:50.992632', 'submitted', null, 'http://webprotege.stanford.edu/RB7q67Gf4JtjKTY4hLsKWOU', '', '0', 'term');
INSERT INTO "public"."requests" VALUES ('93', 'ss', '', null, '0', '', '', 'Michael Dorf', 'OPTIMAL', null, null, '2019-04-19 16:52:54.133975', '2019-04-19 16:52:54.133975', 'submitted', null, 'http://webprotege.stanford.edu/RB7q67Gf4JtjKTY4hLsKWOU', '', '0', 'term');
INSERT INTO "public"."requests" VALUES ('94', '', '', null, '0', '', '', 'Michael Dorf', 'OPTIMAL', null, null, '2019-04-19 16:59:25.524996', '2019-04-19 16:59:25.524996', 'submitted', null, 'http://webprotege.stanford.edu/RB7q67Gf4JtjKTY4hLsKWOU', '', '0', 'term');
INSERT INTO "public"."requests" VALUES ('95', '', '', null, '0', '', '', 'Michael Dorf', 'OPTIMAL', null, null, '2019-04-19 16:59:42.348236', '2019-04-19 16:59:42.348236', 'submitted', null, 'http://webprotege.stanford.edu/RB7q67Gf4JtjKTY4hLsKWOU', '', '0', 'term');
INSERT INTO "public"."requests" VALUES ('96', 'eee', '', null, '0', '', '', 'Michael Dorf', 'OPTIMAL', null, null, '2019-04-19 16:59:48.323819', '2019-04-19 16:59:48.323819', 'submitted', null, 'http://webprotege.stanford.edu/RB7q67Gf4JtjKTY4hLsKWOU', '', '0', 'term');
INSERT INTO "public"."requests" VALUES ('97', '', '', null, '0', '', '', 'Michael Dorf', 'OPTIMAL', null, null, '2019-04-19 17:00:39.614335', '2019-04-19 17:00:39.614335', 'submitted', null, 'http://webprotege.stanford.edu/RB7q67Gf4JtjKTY4hLsKWOU', '', '0', 'term');
INSERT INTO "public"."requests" VALUES ('107', 'ÑÐ´ÑÐ³', 'Ð´ÑÑ', null, '0', '', '', 'Michael Dorf', 'OPTIMAL', null, null, '2019-04-23 18:35:45.489955', '2019-04-23 18:35:45.489955', 'submitted', null, '', '', '0', 'term');
INSERT INTO "public"."requests" VALUES ('108', 'qwe', 'qrqr', null, '0', '', '', 'Michael Dorf', 'OPTIMAL', null, null, '2019-04-23 18:40:49.447171', '2019-04-23 18:40:49.447171', 'submitted', null, '', '', '0', 'term');
INSERT INTO "public"."requests" VALUES ('109', 'asfd', 'asf', null, '0', '', '', 'Michael Dorf', 'OPTIMAL', null, null, '2019-04-23 18:44:18.484043', '2019-04-23 18:44:18.484043', 'submitted', null, '', '', '0', 'term');
INSERT INTO "public"."requests" VALUES ('110', 'sd', 'sgfgsgg', null, '0', '', '', 'Michael Dorf', 'OPTIMAL', null, null, '2019-04-23 18:45:58.335468', '2019-04-23 18:45:58.335468', 'submitted', null, '', '', '0', 'term');
INSERT INTO "public"."requests" VALUES ('111', 'et', 'eyrey', null, '0', '', '', 'Michael Dorf', 'OPTIMAL', null, null, '2019-04-23 18:49:36.58215', '2019-04-23 18:49:36.58215', 'submitted', null, '', '', '0', 'term');
INSERT INTO "public"."requests" VALUES ('124', 'my term', 'term description', null, '0', 'http://www.yahoo.com', '', 'Michael Dorf', 'OPTIMAL', null, null, '2019-04-25 17:12:58.918584', '2019-04-25 17:12:58.918584', 'submitted', null, '', '', '0', 'term');
INSERT INTO "public"."requests" VALUES ('125', 'ThisIsTest1', 'Hande''s test ', null, '0', 'test', 'test', 'Hande McGinty', 'BAO', null, null, '2019-04-30 14:23:15.604454', '2019-04-30 14:23:15.604454', 'submitted', null, '', '', '0', 'term');
INSERT INTO "public"."requests" VALUES ('131', 'Am2.2-beta2AR cell', 'U937 cells stably transfected with and expressing the surface displayed fusion protein of FAP (fluorogen activating protein) AM2.2 to the extracellular N-terminus of the human Î²2AR gene  (thus, ''AM2.2-beta2AR''). See PMCID: PMC3621705', 'BAO', '3048', '', 'UCSF term', 'Hande McGinty', 'BAO', null, null, '2019-05-02 22:45:03.726069', '2019-05-02 22:45:03.726069', 'submitted', null, '', '', '0', 'term');
INSERT INTO "public"."requests" VALUES ('132', 'beta-arrestin clustering assay', 'The Transfluor assay employs a a beta-arrestin-GFP fusion protein to detect activation of GPCRs: Upon ligand binding the GPCR, cytosolic arrestin-GFP quickly translocates to the cell membrane and then to endocytic vesicles, where it ''clusters'' and can be detected by fluorescent image analysis. (Developed / commercialized by Norak, then Molecular Devices)', null, '0', '', 'UCSF term, (aka ''Transfluor'' assay)', 'Hande McGinty', 'BAO', null, null, '2019-05-03 13:17:14.731653', '2019-05-03 13:17:14.731653', 'submitted', null, '', '', '0', 'term');

-- ----------------------------
-- Table structure for requestsStatus
-- ----------------------------
DROP TABLE IF EXISTS "public"."requestsStatus";
CREATE TABLE "public"."requestsStatus" (
"status" varchar(255) COLLATE "default",
"timestamp" date,
"requestID" int4
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of requestsStatus
-- ----------------------------
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '64');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '65');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '66');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '67');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '68');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '69');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '70');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '71');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '72');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '73');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '74');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '75');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '76');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '77');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '78');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '79');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '80');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '81');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '82');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '83');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '84');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '85');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '86');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '87');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '88');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '89');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '90');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '91');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '92');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '93');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '94');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '95');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '96');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '97');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '98');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '99');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '100');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '101');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '102');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '103');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '104');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '105');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '106');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '107');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '108');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '109');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '110');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '111');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '112');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '113');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '114');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '115');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '116');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '117');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '118');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '119');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '120');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '121');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '122');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '123');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '124');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '125');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '126');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '127');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '128');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '129');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '130');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '131');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '132');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '133');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '134');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '135');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '136');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '137');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '138');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '139');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '140');
INSERT INTO "public"."requestsStatus" VALUES ('submitted', null, '141');

-- ----------------------------
-- Alter Sequences Owned By 
-- ----------------------------
ALTER SEQUENCE "public"."maintainers_id_seq" OWNED BY "maintainers"."id";
ALTER SEQUENCE "public"."ontologies_id_seq" OWNED BY "ontologies"."id";
ALTER SEQUENCE "public"."ontology_to_maintainer_id_seq" OWNED BY "ontology_to_maintainer"."id";
ALTER SEQUENCE "public"."requests_id_seq" OWNED BY "requests"."id";

-- ----------------------------
-- Primary Key structure for table maintainers
-- ----------------------------
ALTER TABLE "public"."maintainers" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table notifications
-- ----------------------------
ALTER TABLE "public"."notifications" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table ontologies
-- ----------------------------
ALTER TABLE "public"."ontologies" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table ontology_to_maintainer
-- ----------------------------
ALTER TABLE "public"."ontology_to_maintainer" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table requests
-- ----------------------------
ALTER TABLE "public"."requests" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Foreign Key structure for table "public"."ontology_to_maintainer"
-- ----------------------------
ALTER TABLE "public"."ontology_to_maintainer" ADD FOREIGN KEY ("maintainer_id") REFERENCES "public"."maintainers" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE "public"."ontology_to_maintainer" ADD FOREIGN KEY ("ontology_id") REFERENCES "public"."ontologies" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
