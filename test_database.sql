--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- Name: status; Type: TYPE; Schema: public; Owner: jpt55
--

CREATE TYPE status AS ENUM (
    'submitted',
    'accepted',
    'requires-response',
    'rejected'
);


ALTER TYPE public.status OWNER TO jpt55;

--
-- Name: update_modified_column(); Type: FUNCTION; Schema: public; Owner: jpt55
--

CREATE FUNCTION update_modified_column() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    NEW.modified = now();
    RETURN NEW;   
END;
$$;


ALTER FUNCTION public.update_modified_column() OWNER TO jpt55;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: maintainers; Type: TABLE; Schema: public; Owner: jpt55; Tablespace: 
--

CREATE TABLE maintainers (
    id integer NOT NULL,
    name character varying(255),
    contact_location character varying(255),
    contact_method character varying(255)
);


ALTER TABLE public.maintainers OWNER TO jpt55;

--
-- Name: maintainers_id_seq; Type: SEQUENCE; Schema: public; Owner: jpt55
--

CREATE SEQUENCE maintainers_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.maintainers_id_seq OWNER TO jpt55;

--
-- Name: maintainers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jpt55
--

ALTER SEQUENCE maintainers_id_seq OWNED BY maintainers.id;


--
-- Name: notifications_id_seq; Type: SEQUENCE; Schema: public; Owner: jpt55
--

CREATE SEQUENCE notifications_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.notifications_id_seq OWNER TO jpt55;

--
-- Name: notifications; Type: TABLE; Schema: public; Owner: jpt55; Tablespace: 
--

CREATE TABLE notifications (
    id bigint DEFAULT nextval('notifications_id_seq'::regclass) NOT NULL,
    type character varying(255),
    address character varying(255),
    message text,
    sent smallint DEFAULT 0 NOT NULL,
    "createDate" date DEFAULT now(),
    "sentDate" date,
    title character varying(255)
);


ALTER TABLE public.notifications OWNER TO jpt55;

--
-- Name: ontologies; Type: TABLE; Schema: public; Owner: jpt55; Tablespace: 
--

CREATE TABLE ontologies (
    id integer NOT NULL,
    name character varying(255),
    url character varying(255),
    ontology_short character varying(255) NOT NULL,
    last_updated date DEFAULT ('now'::text)::date
);


ALTER TABLE public.ontologies OWNER TO jpt55;

--
-- Name: ontologies_id_seq; Type: SEQUENCE; Schema: public; Owner: jpt55
--

CREATE SEQUENCE ontologies_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.ontologies_id_seq OWNER TO jpt55;

--
-- Name: ontologies_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jpt55
--

ALTER SEQUENCE ontologies_id_seq OWNED BY ontologies.id;


--
-- Name: ontology_to_maintainer; Type: TABLE; Schema: public; Owner: jpt55; Tablespace: 
--

CREATE TABLE ontology_to_maintainer (
    id integer NOT NULL,
    ontology_id integer NOT NULL,
    maintainer_id integer NOT NULL
);


ALTER TABLE public.ontology_to_maintainer OWNER TO jpt55;

--
-- Name: ontology_to_maintainer_id_seq; Type: SEQUENCE; Schema: public; Owner: jpt55
--

CREATE SEQUENCE ontology_to_maintainer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.ontology_to_maintainer_id_seq OWNER TO jpt55;

--
-- Name: ontology_to_maintainer_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jpt55
--

ALTER SEQUENCE ontology_to_maintainer_id_seq OWNED BY ontology_to_maintainer.id;


--
-- Name: requests; Type: TABLE; Schema: public; Owner: jpt55; Tablespace: 
--

CREATE TABLE requests (
    id smallint NOT NULL,
    label character varying(255),
    description text,
    superclass_ontology character varying(8),
    superclass_id integer,
    "references" text,
    justification text,
    submitter character varying(255),
    uri_ontology character varying(8),
    uri_identifier character varying(255),
    current_message text,
    date timestamp(6) without time zone DEFAULT now(),
    last_updated timestamp(6) without time zone DEFAULT now(),
    status status DEFAULT 'submitted'::status,
    uri character varying(255),
    superclass_uri character varying(255),
    submitter_email character varying(255),
    notify smallint,
    type character varying(255)
);


ALTER TABLE public.requests OWNER TO jpt55;

--
-- Name: requestsStatus; Type: TABLE; Schema: public; Owner: jpt55; Tablespace: 
--

CREATE TABLE "requestsStatus" (
    status character varying(255),
    "timestamp" date,
    "requestID" integer
);


ALTER TABLE public."requestsStatus" OWNER TO jpt55;

--
-- Name: requests_id_seq; Type: SEQUENCE; Schema: public; Owner: jpt55
--

CREATE SEQUENCE requests_id_seq
    START WITH 62
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.requests_id_seq OWNER TO jpt55;

--
-- Name: requests_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jpt55
--

ALTER SEQUENCE requests_id_seq OWNED BY requests.id;


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: jpt55
--

ALTER TABLE ONLY maintainers ALTER COLUMN id SET DEFAULT nextval('maintainers_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: jpt55
--

ALTER TABLE ONLY ontologies ALTER COLUMN id SET DEFAULT nextval('ontologies_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: jpt55
--

ALTER TABLE ONLY ontology_to_maintainer ALTER COLUMN id SET DEFAULT nextval('ontology_to_maintainer_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: jpt55
--

ALTER TABLE ONLY requests ALTER COLUMN id SET DEFAULT nextval('requests_id_seq'::regclass);


--
-- Data for Name: maintainers; Type: TABLE DATA; Schema: public; Owner: jpt55
--

INSERT INTO maintainers VALUES (1, 'John Turner', '', 'email');
INSERT INTO maintainers VALUES (2, 'Danniel Cooper', '', 'email');
INSERT INTO maintainers VALUES (3, 'Hande ', '', 'email');


--
-- Name: maintainers_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jpt55
--

SELECT pg_catalog.setval('maintainers_id_seq', 3, true);


--
-- Data for Name: notifications; Type: TABLE DATA; Schema: public; Owner: jpt55
--



--
-- Name: notifications_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jpt55
--

SELECT pg_catalog.setval('notifications_id_seq', 70, true);


--
-- Data for Name: ontologies; Type: TABLE DATA; Schema: public; Owner: jpt55
--

INSERT INTO ontologies VALUES (1, 'BioAssay Ontology', NULL, 'BAO', '2019-03-12');
INSERT INTO ontologies VALUES (2, 'Drug Target Ontology', NULL, 'DTO', '2019-03-12');
INSERT INTO ontologies VALUES (0, 'Unknown Ontology', NULL, '???', '2019-03-12');


--
-- Name: ontologies_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jpt55
--

SELECT pg_catalog.setval('ontologies_id_seq', 2, true);


--
-- Data for Name: ontology_to_maintainer; Type: TABLE DATA; Schema: public; Owner: jpt55
--

INSERT INTO ontology_to_maintainer VALUES (1, 0, 1);
INSERT INTO ontology_to_maintainer VALUES (2, 0, 2);
INSERT INTO ontology_to_maintainer VALUES (3, 0, 3);
INSERT INTO ontology_to_maintainer VALUES (4, 1, 1);
INSERT INTO ontology_to_maintainer VALUES (5, 1, 2);
INSERT INTO ontology_to_maintainer VALUES (6, 1, 3);
INSERT INTO ontology_to_maintainer VALUES (7, 2, 1);
INSERT INTO ontology_to_maintainer VALUES (8, 2, 2);
INSERT INTO ontology_to_maintainer VALUES (9, 2, 3);


--
-- Name: ontology_to_maintainer_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jpt55
--

SELECT pg_catalog.setval('ontology_to_maintainer_id_seq', 10, true);


--
-- Data for Name: requests; Type: TABLE DATA; Schema: public; Owner: jpt55
--

INSERT INTO requests VALUES (21, 'test', 'testClass', NULL, 45, NULL, '', 'johgn', '???', NULL, NULL, '2017-12-12 11:16:56.079845', '2017-12-12 11:16:56.079845', 'submitted', NULL, NULL, '', NULL, 'term');
INSERT INTO requests VALUES (22, 'test', 'test', 'est', 1, 'test', 'test', 'test', '???', NULL, NULL, '2018-01-08 11:29:49.457237', '2018-01-08 11:29:49.457237', 'submitted', NULL, NULL, '', NULL, 'term');
INSERT INTO requests VALUES (23, 'test', 'test', 'est', 2, 'test', 'test', 'test', '???', NULL, '', '2018-01-08 11:34:00.710691', '2018-01-08 11:34:00.710691', 'submitted', NULL, NULL, '', NULL, 'term');
INSERT INTO requests VALUES (24, 'test', 'test', 'est', 1, 'test', 'test', 'test', '???', NULL, '', '2018-01-08 11:34:10.06363', '2018-01-08 11:34:10.06363', 'accepted', NULL, NULL, '', NULL, 'term');
INSERT INTO requests VALUES (25, 'test2', 'test', 'est', 1, 'test', 'test', 'test', '???', NULL, '', '2018-01-08 11:34:19.414569', '2018-01-08 11:34:19.414569', 'rejected', NULL, NULL, '', NULL, 'term');
INSERT INTO requests VALUES (26, 'Test Term', 'This is a Test', 'est', 1000, 'none', 'N/A', 'John', '???', NULL, 'test', '2018-01-18 12:11:49.138629', '2018-01-18 12:11:49.138629', 'submitted', NULL, NULL, '', NULL, 'term');
INSERT INTO requests VALUES (70, 'apr17 term', 'Term of April 17', NULL, 0, '', '', '', '', NULL, NULL, '2019-04-17 19:22:11.40765', '2019-04-17 19:22:11.40765', 'submitted', NULL, 'http://www.yahoo.com', '', 0, 'term');
INSERT INTO requests VALUES (71, 'apr17 20term', 'Term of April 17', NULL, 0, '', '', '', '', NULL, NULL, '2019-04-17 19:22:53.182586', '2019-04-17 19:22:53.182586', 'submitted', NULL, 'http://www.yahoo.com', '', 0, 'term');
INSERT INTO requests VALUES (72, 'apr17 20term', 'Term 20of 20Apri 2017', NULL, 0, '', '', '', '', NULL, NULL, '2019-04-17 19:23:17.803182', '2019-04-17 19:23:17.803182', 'submitted', NULL, 'http://www.yahoo.com', '', 0, 'term');
INSERT INTO requests VALUES (73, 'apr17 20term', 'Term 20of 20Apri 2017', NULL, 0, '', '', '', '', NULL, NULL, '2019-04-17 19:23:44.394167', '2019-04-17 19:23:44.394167', 'submitted', NULL, 'http://www.yahoo.com', '', 0, 'term');
INSERT INTO requests VALUES (89, 'dd', '', NULL, 0, '', '', 'Michael Dorf', 'OPTIMAL', NULL, NULL, '2019-04-19 16:39:29.02824', '2019-04-19 16:39:29.02824', 'submitted', NULL, 'http://webprotege.stanford.edu/RB7q67Gf4JtjKTY4hLsKWOU', '', 0, 'term');
INSERT INTO requests VALUES (107, 'ÑÐ´ÑÐ³', 'Ð´ÑÑ', NULL, 0, '', '', 'Michael Dorf', 'OPTIMAL', NULL, NULL, '2019-04-23 18:35:45.489955', '2019-04-23 18:35:45.489955', 'submitted', NULL, '', '', 0, 'term');
INSERT INTO requests VALUES (108, 'qwe', 'qrqr', NULL, 0, '', '', 'Michael Dorf', 'OPTIMAL', NULL, NULL, '2019-04-23 18:40:49.447171', '2019-04-23 18:40:49.447171', 'submitted', NULL, '', '', 0, 'term');
INSERT INTO requests VALUES (109, 'asfd', 'asf', NULL, 0, '', '', 'Michael Dorf', 'OPTIMAL', NULL, NULL, '2019-04-23 18:44:18.484043', '2019-04-23 18:44:18.484043', 'submitted', NULL, '', '', 0, 'term');
INSERT INTO requests VALUES (110, 'sd', 'sgfgsgg', NULL, 0, '', '', 'Michael Dorf', 'OPTIMAL', NULL, NULL, '2019-04-23 18:45:58.335468', '2019-04-23 18:45:58.335468', 'submitted', NULL, '', '', 0, 'term');
INSERT INTO requests VALUES (124, 'my term', 'term description', NULL, 0, 'http://www.yahoo.com', '', 'Michael Dorf', 'OPTIMAL', NULL, NULL, '2019-04-25 17:12:58.918584', '2019-04-25 17:12:58.918584', 'submitted', NULL, '', '', 0, 'term');
INSERT INTO requests VALUES (131, 'Am2.2-beta2AR cell', 'U937 cells stably transfected with and expressing the surface displayed fusion protein of FAP (fluorogen activating protein) AM2.2 to the extracellular N-terminus of the human Î²2AR gene  (thus, ''AM2.2-beta2AR''). See PMCID: PMC3621705', 'BAO', 3048, '', 'UCSF term', 'Hande McGinty', 'BAO', NULL, NULL, '2019-05-02 22:45:03.726069', '2019-05-02 22:45:03.726069', 'submitted', NULL, '', '', 0, 'term');
INSERT INTO requests VALUES (74, 'apr17 20term', 'Term 20of 20Apri 2017', NULL, 0, '', '', '', '', NULL, NULL, '2019-04-17 20:23:16.243623', '2019-04-17 20:23:16.243623', 'submitted', NULL, 'http://www.yahoo.com', '', 0, 'term');
INSERT INTO requests VALUES (75, 'apr17 20term', 'Term 20of 20Apri 2017', NULL, 0, '', '', '', '', NULL, NULL, '2019-04-17 20:27:09.397955', '2019-04-17 20:27:09.397955', 'submitted', NULL, '', '', 0, 'term');
INSERT INTO requests VALUES (90, '', '', NULL, 0, '', '', 'Michael Dorf', 'OPTIMAL', NULL, NULL, '2019-04-19 16:43:05.058351', '2019-04-19 16:43:05.058351', 'submitted', NULL, 'http://webprotege.stanford.edu/RB7q67Gf4JtjKTY4hLsKWOU', '', 0, 'term');
INSERT INTO requests VALUES (111, 'et', 'eyrey', NULL, 0, '', '', 'Michael Dorf', 'OPTIMAL', NULL, NULL, '2019-04-23 18:49:36.58215', '2019-04-23 18:49:36.58215', 'submitted', NULL, '', '', 0, 'term');
INSERT INTO requests VALUES (125, 'ThisIsTest1', 'Hande''s test ', NULL, 0, 'test', 'test', 'Hande McGinty', 'BAO', NULL, NULL, '2019-04-30 14:23:15.604454', '2019-04-30 14:23:15.604454', 'submitted', NULL, '', '', 0, 'term');
INSERT INTO requests VALUES (27, 'Test BAO Term', 'Some description', 'AO', 1, '', '', 'John', '???', NULL, NULL, '2018-01-26 15:08:12.526242', '2018-01-26 15:08:12.526242', 'submitted', NULL, '', '', NULL, 'term');
INSERT INTO requests VALUES (28, 'Test Bao Term 2', 'Another Description', 'AO', 3112, '', '', '', '???', NULL, NULL, '2018-01-26 15:09:28.63297', '2018-01-26 15:09:28.63297', 'submitted', NULL, 'http://www.bioassayontology.org/bao#BAO_0003112', '', NULL, 'term');
INSERT INTO requests VALUES (29, 'test ter m3', 'why ', 'BAO', 3112, '', '', 'john', 'BAO', NULL, NULL, '2018-01-26 15:18:45.952151', '2018-01-26 15:18:45.952151', 'submitted', NULL, 'http://www.bioassayontology.org/bao#BAO_0003112', '', NULL, 'term');
INSERT INTO requests VALUES (30, 'mish test term', 'great new term', NULL, 20, NULL, NULL, NULL, '???', NULL, NULL, '2018-01-31 20:29:41.331944', '2018-01-31 20:29:41.331944', 'submitted', NULL, 'http://dev3.ccs.miami.edu:8080/ontolobridge#ONTB_000000020', '', NULL, 'term');
INSERT INTO requests VALUES (31, 'Misha Test Term', 'This is a new term requested by misha', NULL, 3, '', '', '', '???', NULL, NULL, '2018-02-01 19:54:17.40911', '2018-02-01 19:54:17.40911', 'submitted', NULL, 'http://dev3.ccs.miami.edu:8080/ontolobridge#ONTB_000000003', '', NULL, 'term');
INSERT INTO requests VALUES (132, 'beta-arrestin clustering assay', 'The Transfluor assay employs a a beta-arrestin-GFP fusion protein to detect activation of GPCRs: Upon ligand binding the GPCR, cytosolic arrestin-GFP quickly translocates to the cell membrane and then to endocytic vesicles, where it ''clusters'' and can be detected by fluorescent image analysis. (Developed / commercialized by Norak, then Molecular Devices)', NULL, 0, '', 'UCSF term, (aka ''Transfluor'' assay)', 'Hande McGinty', 'BAO', NULL, NULL, '2019-05-03 13:17:14.731653', '2019-05-03 13:17:14.731653', 'submitted', NULL, '', '', 0, 'term');
INSERT INTO requests VALUES (76, 'term apr 14 15:30', 'term apr 14 15:30', NULL, 0, '', '', '', '', NULL, NULL, '2019-04-18 17:55:04.208383', '2019-04-18 17:55:04.208383', 'submitted', NULL, 'http://www.yahoo.com', '', 0, 'term');
INSERT INTO requests VALUES (77, 'term apr 14 15:30', 'term apr 14 15:30', NULL, 0, '', '', '', '', NULL, NULL, '2019-04-18 17:55:16.660597', '2019-04-18 17:55:16.660597', 'submitted', NULL, 'http://www.yahoo.com', '', 0, 'term');
INSERT INTO requests VALUES (91, 'dd', '', NULL, 0, '', '', 'Michael Dorf', 'OPTIMAL', NULL, NULL, '2019-04-19 16:50:41.486485', '2019-04-19 16:50:41.486485', 'submitted', NULL, 'http://webprotege.stanford.edu/RB7q67Gf4JtjKTY4hLsKWOU', '', 0, 'term');
INSERT INTO requests VALUES (92, '', '', NULL, 0, '', '', 'Michael Dorf', 'OPTIMAL', NULL, NULL, '2019-04-19 16:52:50.992632', '2019-04-19 16:52:50.992632', 'submitted', NULL, 'http://webprotege.stanford.edu/RB7q67Gf4JtjKTY4hLsKWOU', '', 0, 'term');
INSERT INTO requests VALUES (93, 'ss', '', NULL, 0, '', '', 'Michael Dorf', 'OPTIMAL', NULL, NULL, '2019-04-19 16:52:54.133975', '2019-04-19 16:52:54.133975', 'submitted', NULL, 'http://webprotege.stanford.edu/RB7q67Gf4JtjKTY4hLsKWOU', '', 0, 'term');
INSERT INTO requests VALUES (94, '', '', NULL, 0, '', '', 'Michael Dorf', 'OPTIMAL', NULL, NULL, '2019-04-19 16:59:25.524996', '2019-04-19 16:59:25.524996', 'submitted', NULL, 'http://webprotege.stanford.edu/RB7q67Gf4JtjKTY4hLsKWOU', '', 0, 'term');
INSERT INTO requests VALUES (95, '', '', NULL, 0, '', '', 'Michael Dorf', 'OPTIMAL', NULL, NULL, '2019-04-19 16:59:42.348236', '2019-04-19 16:59:42.348236', 'submitted', NULL, 'http://webprotege.stanford.edu/RB7q67Gf4JtjKTY4hLsKWOU', '', 0, 'term');
INSERT INTO requests VALUES (96, 'eee', '', NULL, 0, '', '', 'Michael Dorf', 'OPTIMAL', NULL, NULL, '2019-04-19 16:59:48.323819', '2019-04-19 16:59:48.323819', 'submitted', NULL, 'http://webprotege.stanford.edu/RB7q67Gf4JtjKTY4hLsKWOU', '', 0, 'term');
INSERT INTO requests VALUES (97, '', '', NULL, 0, '', '', 'Michael Dorf', 'OPTIMAL', NULL, NULL, '2019-04-19 17:00:39.614335', '2019-04-19 17:00:39.614335', 'submitted', NULL, 'http://webprotege.stanford.edu/RB7q67Gf4JtjKTY4hLsKWOU', '', 0, 'term');
INSERT INTO requests VALUES (40, 'Misha Term 6', 'This is a term 6 description', NULL, 22, 'www.yahoo.com', 'this term is required for our new project', '', '???', NULL, NULL, '2018-02-08 12:34:50.156131', '2018-02-08 12:34:50.156131', 'submitted', NULL, 'http://dev3.ccs.miami.edu:8080/ontolobridge#ONTB_000000022', '', 1, 'term');
INSERT INTO requests VALUES (41, 'Misha', 'test term', NULL, 5, 'yahoo.com', '', '', '???', NULL, NULL, '2018-02-08 16:53:40.505469', '2018-02-08 16:53:40.505469', 'submitted', NULL, 'http://dev3.ccs.miami.edu:8080/ontolobridge#ONTB_000000005', '', 1, 'term');
INSERT INTO requests VALUES (42, '11', 'test term', NULL, 5, 'yahoo.com', '', '', '???', NULL, NULL, '2018-02-08 17:00:28.436835', '2018-02-08 17:00:28.436835', 'submitted', NULL, 'http://dev3.ccs.miami.edu:8080/ontolobridge#ONTB_000000005', '', 1, 'term');
INSERT INTO requests VALUES (43, 'Misha''s new term', 'this is my new term', NULL, 3, 'google.com', 'need this for my projecdt', '', '???', NULL, '', '2018-02-09 13:21:42.305877', '2018-02-09 13:21:42.305877', 'rejected', NULL, 'http://dev3.ccs.miami.edu:8080/ontolobridge#ONTB_000000003', '', 1, 'term');
INSERT INTO requests VALUES (44, 'has positive control', 'The positive control for an assay.', 'bao:BAO', 740, '', '', 'Alex Clark', '???', NULL, NULL, '2018-03-09 15:50:09.875016', '2018-03-09 15:50:09.875016', 'submitted', NULL, '', '', 0, 'term');
INSERT INTO requests VALUES (45, 'has negative control', 'The negative control for an assay.', 'bao', 740, '', '', 'Alex Clark', 'BAO', NULL, NULL, '2018-03-09 15:51:06.107176', '2018-03-09 15:51:06.107176', 'submitted', NULL, NULL, '', 0, 'term');
INSERT INTO requests VALUES (46, 'Test Class 2', 'test', 'BAO', 740, 'test', 'test', 'test', 'BAO', NULL, NULL, '2018-05-31 14:48:25.279981', '2018-05-31 14:48:25.279981', 'submitted', NULL, NULL, '', 0, 'term');


--
-- Data for Name: requestsStatus; Type: TABLE DATA; Schema: public; Owner: jpt55
--

INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 64);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 65);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 66);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 67);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 68);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 69);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 70);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 71);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 72);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 73);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 74);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 75);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 76);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 77);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 78);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 79);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 80);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 81);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 82);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 83);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 84);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 85);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 86);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 87);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 88);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 89);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 90);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 91);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 92);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 93);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 94);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 95);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 96);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 97);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 98);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 99);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 100);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 101);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 102);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 103);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 104);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 105);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 106);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 107);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 108);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 109);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 110);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 111);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 112);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 113);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 114);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 115);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 116);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 117);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 118);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 119);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 120);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 121);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 122);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 123);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 124);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 125);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 126);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 127);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 128);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 129);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 130);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 131);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 132);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 133);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 134);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 135);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 136);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 137);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 138);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 139);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 140);
INSERT INTO "requestsStatus" VALUES ('submitted', NULL, 141);


--
-- Name: requests_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jpt55
--

SELECT pg_catalog.setval('requests_id_seq', 141, true);


--
-- Name: maintainers_pkey; Type: CONSTRAINT; Schema: public; Owner: jpt55; Tablespace: 
--

ALTER TABLE ONLY maintainers
    ADD CONSTRAINT maintainers_pkey PRIMARY KEY (id);


--
-- Name: notifications_pkey; Type: CONSTRAINT; Schema: public; Owner: jpt55; Tablespace: 
--

ALTER TABLE ONLY notifications
    ADD CONSTRAINT notifications_pkey PRIMARY KEY (id);


--
-- Name: ontologies_pkey; Type: CONSTRAINT; Schema: public; Owner: jpt55; Tablespace: 
--

ALTER TABLE ONLY ontologies
    ADD CONSTRAINT ontologies_pkey PRIMARY KEY (id);


--
-- Name: ontology_to_maintainer_pkey; Type: CONSTRAINT; Schema: public; Owner: jpt55; Tablespace: 
--

ALTER TABLE ONLY ontology_to_maintainer
    ADD CONSTRAINT ontology_to_maintainer_pkey PRIMARY KEY (id);


--
-- Name: requests_pkey; Type: CONSTRAINT; Schema: public; Owner: jpt55; Tablespace: 
--

ALTER TABLE ONLY requests
    ADD CONSTRAINT requests_pkey PRIMARY KEY (id);


--
-- Name: ontology_to_maintainer_maintainer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: jpt55
--

ALTER TABLE ONLY ontology_to_maintainer
    ADD CONSTRAINT ontology_to_maintainer_maintainer_id_fkey FOREIGN KEY (maintainer_id) REFERENCES maintainers(id) ON DELETE CASCADE;


--
-- Name: ontology_to_maintainer_ontology_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: jpt55
--

ALTER TABLE ONLY ontology_to_maintainer
    ADD CONSTRAINT ontology_to_maintainer_ontology_id_fkey FOREIGN KEY (ontology_id) REFERENCES ontologies(id) ON DELETE CASCADE;


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- Name: maintainers; Type: ACL; Schema: public; Owner: jpt55
--

REVOKE ALL ON TABLE maintainers FROM PUBLIC;
REVOKE ALL ON TABLE maintainers FROM jpt55;
GRANT ALL ON TABLE maintainers TO jpt55;
GRANT SELECT,REFERENCES ON TABLE maintainers TO ontolobridge;


--
-- Name: maintainers_id_seq; Type: ACL; Schema: public; Owner: jpt55
--

REVOKE ALL ON SEQUENCE maintainers_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE maintainers_id_seq FROM jpt55;
GRANT ALL ON SEQUENCE maintainers_id_seq TO jpt55;
GRANT ALL ON SEQUENCE maintainers_id_seq TO ontolobridge;


--
-- Name: notifications_id_seq; Type: ACL; Schema: public; Owner: jpt55
--

REVOKE ALL ON SEQUENCE notifications_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE notifications_id_seq FROM jpt55;
GRANT ALL ON SEQUENCE notifications_id_seq TO jpt55;
GRANT ALL ON SEQUENCE notifications_id_seq TO ontolobridge;


--
-- Name: notifications; Type: ACL; Schema: public; Owner: jpt55
--

REVOKE ALL ON TABLE notifications FROM PUBLIC;
REVOKE ALL ON TABLE notifications FROM jpt55;
GRANT ALL ON TABLE notifications TO jpt55;
GRANT ALL ON TABLE notifications TO ontolobridge;


--
-- Name: ontologies; Type: ACL; Schema: public; Owner: jpt55
--

REVOKE ALL ON TABLE ontologies FROM PUBLIC;
REVOKE ALL ON TABLE ontologies FROM jpt55;
GRANT ALL ON TABLE ontologies TO jpt55;
GRANT SELECT,REFERENCES ON TABLE ontologies TO ontolobridge;


--
-- Name: ontologies_id_seq; Type: ACL; Schema: public; Owner: jpt55
--

REVOKE ALL ON SEQUENCE ontologies_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE ontologies_id_seq FROM jpt55;
GRANT ALL ON SEQUENCE ontologies_id_seq TO jpt55;
GRANT ALL ON SEQUENCE ontologies_id_seq TO ontolobridge;


--
-- Name: ontology_to_maintainer; Type: ACL; Schema: public; Owner: jpt55
--

REVOKE ALL ON TABLE ontology_to_maintainer FROM PUBLIC;
REVOKE ALL ON TABLE ontology_to_maintainer FROM jpt55;
GRANT ALL ON TABLE ontology_to_maintainer TO jpt55;
GRANT SELECT ON TABLE ontology_to_maintainer TO ontolobridge;


--
-- Name: ontology_to_maintainer_id_seq; Type: ACL; Schema: public; Owner: jpt55
--

REVOKE ALL ON SEQUENCE ontology_to_maintainer_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE ontology_to_maintainer_id_seq FROM jpt55;
GRANT ALL ON SEQUENCE ontology_to_maintainer_id_seq TO jpt55;
GRANT ALL ON SEQUENCE ontology_to_maintainer_id_seq TO ontolobridge;


--
-- Name: requests; Type: ACL; Schema: public; Owner: jpt55
--

REVOKE ALL ON TABLE requests FROM PUBLIC;
REVOKE ALL ON TABLE requests FROM jpt55;
GRANT ALL ON TABLE requests TO jpt55;
GRANT ALL ON TABLE requests TO ontolobridge;


--
-- Name: requestsStatus; Type: ACL; Schema: public; Owner: jpt55
--

REVOKE ALL ON TABLE "requestsStatus" FROM PUBLIC;
REVOKE ALL ON TABLE "requestsStatus" FROM jpt55;
GRANT ALL ON TABLE "requestsStatus" TO jpt55;
GRANT ALL ON TABLE "requestsStatus" TO ontolobridge;


--
-- Name: requests_id_seq; Type: ACL; Schema: public; Owner: jpt55
--

REVOKE ALL ON SEQUENCE requests_id_seq FROM PUBLIC;
REVOKE ALL ON SEQUENCE requests_id_seq FROM jpt55;
GRANT ALL ON SEQUENCE requests_id_seq TO jpt55;
GRANT ALL ON SEQUENCE requests_id_seq TO ontolobridge;


--
-- PostgreSQL database dump complete
--

