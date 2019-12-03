--
-- PostgreSQL database dump
--

-- Dumped from database version 11.5
-- Dumped by pg_dump version 11.5

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: status; Type: TYPE; Schema: public; Owner: ob_user
--

CREATE TYPE public.status AS ENUM (
    'submitted',
    'accepted',
    'requires-response',
    'rejected'
);


ALTER TYPE public.status OWNER TO ob_user;

--
-- Name: update_modified_column(); Type: FUNCTION; Schema: public; Owner: ob_user
--

CREATE FUNCTION public.update_modified_column() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    NEW.modified = now();
    RETURN NEW;   
END;
$$;


ALTER FUNCTION public.update_modified_column() OWNER TO ob_user;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: maintainers; Type: TABLE; Schema: public; Owner: ob_user
--

CREATE TABLE public.maintainers (
    id integer NOT NULL,
    name character varying(255),
    contact_location character varying(255),
    contact_method character varying(255)
);


ALTER TABLE public.maintainers OWNER TO ob_user;

--
-- Name: maintainers_id_seq; Type: SEQUENCE; Schema: public; Owner: ob_user
--

CREATE SEQUENCE public.maintainers_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.maintainers_id_seq OWNER TO ob_user;

--
-- Name: maintainers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ob_user
--

ALTER SEQUENCE public.maintainers_id_seq OWNED BY public.maintainers.id;


--
-- Name: notifications_id_seq; Type: SEQUENCE; Schema: public; Owner: ob_user
--

CREATE SEQUENCE public.notifications_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.notifications_id_seq OWNER TO ob_user;

--
-- Name: notifications; Type: TABLE; Schema: public; Owner: ob_user
--

CREATE TABLE public.notifications (
    id bigint DEFAULT nextval('public.notifications_id_seq'::regclass) NOT NULL,
    notification_method character varying(255),
    address character varying(255),
    message text,
    sent smallint DEFAULT 0 NOT NULL,
    created_date date DEFAULT now(),
    sent_date date,
    title character varying(255)
);


ALTER TABLE public.notifications OWNER TO ob_user;

--
-- Name: ontologies; Type: TABLE; Schema: public; Owner: ob_user
--

CREATE TABLE public.ontologies (
    id integer NOT NULL,
    name character varying(255),
    url character varying(255),
    ontology_short character varying(255) NOT NULL,
    updated_date date DEFAULT ('now'::text)::date,
    created_date date DEFAULT ('now'::text)::date
);


ALTER TABLE public.ontologies OWNER TO ob_user;

--
-- Name: ontologies_id_seq; Type: SEQUENCE; Schema: public; Owner: ob_user
--

CREATE SEQUENCE public.ontologies_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.ontologies_id_seq OWNER TO ob_user;

--
-- Name: ontologies_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ob_user
--

ALTER SEQUENCE public.ontologies_id_seq OWNED BY public.ontologies.id;


--
-- Name: ontology_to_maintainer; Type: TABLE; Schema: public; Owner: ob_user
--

CREATE TABLE public.ontology_to_maintainer (
    id integer NOT NULL,
    ontology_id integer NOT NULL,
    maintainer_id integer NOT NULL
);


ALTER TABLE public.ontology_to_maintainer OWNER TO ob_user;

--
-- Name: ontology_to_maintainer_id_seq; Type: SEQUENCE; Schema: public; Owner: ob_user
--

CREATE SEQUENCE public.ontology_to_maintainer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.ontology_to_maintainer_id_seq OWNER TO ob_user;

--
-- Name: ontology_to_maintainer_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ob_user
--

ALTER SEQUENCE public.ontology_to_maintainer_id_seq OWNED BY public.ontology_to_maintainer.id;


--
-- Name: request_status; Type: TABLE; Schema: public; Owner: ob_user
--

CREATE TABLE public.request_status (
    current_status character varying(255),
    "timestamp" date,
    request_id integer,
    id integer NOT NULL
);


ALTER TABLE public.request_status OWNER TO ob_user;

--
-- Name: request_status_id_seq; Type: SEQUENCE; Schema: public; Owner: ob_user
--

CREATE SEQUENCE public.request_status_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.request_status_id_seq OWNER TO ob_user;

--
-- Name: request_status_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ob_user
--

ALTER SEQUENCE public.request_status_id_seq OWNED BY public.request_status.id;


--
-- Name: requests; Type: TABLE; Schema: public; Owner: ob_user
--

CREATE TABLE public.requests (
    id smallint NOT NULL,
    label character varying(255),
    description text,
    superclass_ontology character varying(64),
    superclass_id integer,
    reference text,
    justification text,
    submitter character varying(255),
    uri_ontology character varying(8),
    uri_identifier character varying(255),
    current_message text,
    created_date timestamp(6) without time zone DEFAULT now(),
    updated_date timestamp(6) without time zone DEFAULT now(),
    submission_status public.status DEFAULT 'submitted'::public.status,
    full_uri character varying(255),
    uri_superclass character varying(255),
    submitter_email character varying(255),
    notify smallint,
    request_type character varying(255)
);


ALTER TABLE public.requests OWNER TO ob_user;

--
-- Name: requests_id_seq; Type: SEQUENCE; Schema: public; Owner: ob_user
--

CREATE SEQUENCE public.requests_id_seq
    START WITH 62
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.requests_id_seq OWNER TO ob_user;

--
-- Name: requests_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ob_user
--

ALTER SEQUENCE public.requests_id_seq OWNED BY public.requests.id;


--
-- Name: maintainers id; Type: DEFAULT; Schema: public; Owner: ob_user
--

ALTER TABLE ONLY public.maintainers ALTER COLUMN id SET DEFAULT nextval('public.maintainers_id_seq'::regclass);


--
-- Name: ontologies id; Type: DEFAULT; Schema: public; Owner: ob_user
--

ALTER TABLE ONLY public.ontologies ALTER COLUMN id SET DEFAULT nextval('public.ontologies_id_seq'::regclass);


--
-- Name: ontology_to_maintainer id; Type: DEFAULT; Schema: public; Owner: ob_user
--

ALTER TABLE ONLY public.ontology_to_maintainer ALTER COLUMN id SET DEFAULT nextval('public.ontology_to_maintainer_id_seq'::regclass);


--
-- Name: request_status id; Type: DEFAULT; Schema: public; Owner: ob_user
--

ALTER TABLE ONLY public.request_status ALTER COLUMN id SET DEFAULT nextval('public.request_status_id_seq'::regclass);


--
-- Name: requests id; Type: DEFAULT; Schema: public; Owner: ob_user
--

ALTER TABLE ONLY public.requests ALTER COLUMN id SET DEFAULT nextval('public.requests_id_seq'::regclass);


--
-- Data for Name: maintainers; Type: TABLE DATA; Schema: public; Owner: ob_user
--

COPY public.maintainers (id, name, contact_location, contact_method) FROM stdin;
1	John Turner		email
2	Danniel Cooper		email
3	Hande 		email
\.


--
-- Data for Name: notifications; Type: TABLE DATA; Schema: public; Owner: ob_user
--

COPY public.notifications (id, notification_method, address, message, sent, created_date, sent_date, title) FROM stdin;
\.


--
-- Data for Name: ontologies; Type: TABLE DATA; Schema: public; Owner: ob_user
--

COPY public.ontologies (id, name, url, ontology_short, updated_date, created_date) FROM stdin;
1	BioAssay Ontology	\N	BAO	2019-03-12	2019-11-08
2	Drug Target Ontology	\N	DTO	2019-03-12	2019-11-08
0	Unknown Ontology	\N	???	2019-03-12	2019-11-08
\.


--
-- Data for Name: ontology_to_maintainer; Type: TABLE DATA; Schema: public; Owner: ob_user
--

COPY public.ontology_to_maintainer (id, ontology_id, maintainer_id) FROM stdin;
1	0	1
2	0	2
3	0	3
4	1	1
5	1	2
6	1	3
7	2	1
8	2	2
9	2	3
\.


--
-- Data for Name: request_status; Type: TABLE DATA; Schema: public; Owner: ob_user
--

COPY public.request_status (current_status, "timestamp", request_id, id) FROM stdin;
submitted	\N	67	4
submitted	\N	70	7
submitted	\N	64	1
submitted	\N	69	6
submitted	\N	73	10
submitted	\N	66	3
submitted	\N	72	9
submitted	\N	65	2
submitted	\N	71	8
submitted	\N	68	5
submitted	\N	98	35
submitted	\N	89	26
submitted	\N	91	28
submitted	\N	102	39
submitted	\N	79	16
submitted	\N	87	24
submitted	\N	97	34
submitted	\N	77	14
submitted	\N	78	15
submitted	\N	86	23
submitted	\N	94	31
submitted	\N	81	18
submitted	\N	95	32
submitted	\N	82	19
submitted	\N	84	21
submitted	\N	100	37
submitted	\N	92	29
submitted	\N	85	22
submitted	\N	96	33
submitted	\N	101	38
submitted	\N	80	17
submitted	\N	90	27
submitted	\N	75	12
submitted	\N	83	20
submitted	\N	93	30
submitted	\N	103	40
submitted	\N	76	13
submitted	\N	99	36
submitted	\N	74	11
submitted	\N	88	25
submitted	\N	120	57
submitted	\N	115	52
submitted	\N	105	42
submitted	\N	110	47
submitted	\N	118	55
submitted	\N	119	56
submitted	\N	111	48
submitted	\N	113	50
submitted	\N	104	41
submitted	\N	108	45
submitted	\N	116	53
submitted	\N	109	46
submitted	\N	117	54
submitted	\N	106	43
submitted	\N	107	44
submitted	\N	112	49
submitted	\N	114	51
submitted	\N	131	68
submitted	\N	128	65
submitted	\N	121	58
submitted	\N	129	66
submitted	\N	139	76
submitted	\N	124	61
submitted	\N	137	74
submitted	\N	125	62
submitted	\N	127	64
submitted	\N	132	69
submitted	\N	134	71
submitted	\N	140	77
submitted	\N	122	59
submitted	\N	123	60
submitted	\N	130	67
submitted	\N	136	73
submitted	\N	133	70
submitted	\N	138	75
submitted	\N	141	78
submitted	\N	135	72
submitted	\N	126	63
\.


--
-- Data for Name: requests; Type: TABLE DATA; Schema: public; Owner: ob_user
--

COPY public.requests (id, label, description, superclass_ontology, superclass_id, reference, justification, submitter, uri_ontology, uri_identifier, current_message, created_date, updated_date, submission_status, full_uri, uri_superclass, submitter_email, notify, request_type) FROM stdin;
21	test	testClass	\N	45	\N		johgn	???	\N	\N	2017-12-12 11:16:56.079845	2017-12-12 11:16:56.079845	submitted	\N	\N		\N	term
22	test	test	est	1	test	test	test	???	\N	\N	2018-01-08 11:29:49.457237	2018-01-08 11:29:49.457237	submitted	\N	\N		\N	term
23	test	test	est	2	test	test	test	???	\N		2018-01-08 11:34:00.710691	2018-01-08 11:34:00.710691	submitted	\N	\N		\N	term
24	test	test	est	1	test	test	test	???	\N		2018-01-08 11:34:10.06363	2018-01-08 11:34:10.06363	accepted	\N	\N		\N	term
25	test2	test	est	1	test	test	test	???	\N		2018-01-08 11:34:19.414569	2018-01-08 11:34:19.414569	rejected	\N	\N		\N	term
26	Test Term	This is a Test	est	1000	none	N/A	John	???	\N	test	2018-01-18 12:11:49.138629	2018-01-18 12:11:49.138629	submitted	\N	\N		\N	term
89	dd		\N	0			Michael Dorf	OPTIMAL	\N	\N	2019-04-19 16:39:29.02824	2019-04-19 16:39:29.02824	submitted	\N	http://webprotege.stanford.edu/RB7q67Gf4JtjKTY4hLsKWOU		0	term
107	ÑÐ´ÑÐ³	Ð´ÑÑ	\N	0			Michael Dorf	OPTIMAL	\N	\N	2019-04-23 18:35:45.489955	2019-04-23 18:35:45.489955	submitted	\N			0	term
108	qwe	qrqr	\N	0			Michael Dorf	OPTIMAL	\N	\N	2019-04-23 18:40:49.447171	2019-04-23 18:40:49.447171	submitted	\N			0	term
109	asfd	asf	\N	0			Michael Dorf	OPTIMAL	\N	\N	2019-04-23 18:44:18.484043	2019-04-23 18:44:18.484043	submitted	\N			0	term
110	sd	sgfgsgg	\N	0			Michael Dorf	OPTIMAL	\N	\N	2019-04-23 18:45:58.335468	2019-04-23 18:45:58.335468	submitted	\N			0	term
124	my term	term description	\N	0	http://www.yahoo.com		Michael Dorf	OPTIMAL	\N	\N	2019-04-25 17:12:58.918584	2019-04-25 17:12:58.918584	submitted	\N			0	term
131	Am2.2-beta2AR cell	U937 cells stably transfected with and expressing the surface displayed fusion protein of FAP (fluorogen activating protein) AM2.2 to the extracellular N-terminus of the human Î²2AR gene  (thus, 'AM2.2-beta2AR'). See PMCID: PMC3621705	BAO	3048		UCSF term	Hande McGinty	BAO	\N	\N	2019-05-02 22:45:03.726069	2019-05-02 22:45:03.726069	submitted	\N			0	term
90			\N	0			Michael Dorf	OPTIMAL	\N	\N	2019-04-19 16:43:05.058351	2019-04-19 16:43:05.058351	submitted	\N	http://webprotege.stanford.edu/RB7q67Gf4JtjKTY4hLsKWOU		0	term
111	et	eyrey	\N	0			Michael Dorf	OPTIMAL	\N	\N	2019-04-23 18:49:36.58215	2019-04-23 18:49:36.58215	submitted	\N			0	term
125	ThisIsTest1	Hande's test 	\N	0	test	test	Hande McGinty	BAO	\N	\N	2019-04-30 14:23:15.604454	2019-04-30 14:23:15.604454	submitted	\N			0	term
27	Test BAO Term	Some description	AO	1			John	???	\N	\N	2018-01-26 15:08:12.526242	2018-01-26 15:08:12.526242	submitted	\N			\N	term
28	Test Bao Term 2	Another Description	AO	3112				???	\N	\N	2018-01-26 15:09:28.63297	2018-01-26 15:09:28.63297	submitted	\N	http://www.bioassayontology.org/bao#BAO_0003112		\N	term
29	test ter m3	why 	BAO	3112			john	BAO	\N	\N	2018-01-26 15:18:45.952151	2018-01-26 15:18:45.952151	submitted	\N	http://www.bioassayontology.org/bao#BAO_0003112		\N	term
30	mish test term	great new term	\N	20	\N	\N	\N	???	\N	\N	2018-01-31 20:29:41.331944	2018-01-31 20:29:41.331944	submitted	\N	http://dev3.ccs.miami.edu:8080/ontolobridge#ONTB_000000020		\N	term
31	Misha Test Term	This is a new term requested by misha	\N	3				???	\N	\N	2018-02-01 19:54:17.40911	2018-02-01 19:54:17.40911	submitted	\N	http://dev3.ccs.miami.edu:8080/ontolobridge#ONTB_000000003		\N	term
132	beta-arrestin clustering assay	The Transfluor assay employs a a beta-arrestin-GFP fusion protein to detect activation of GPCRs: Upon ligand binding the GPCR, cytosolic arrestin-GFP quickly translocates to the cell membrane and then to endocytic vesicles, where it 'clusters' and can be detected by fluorescent image analysis. (Developed / commercialized by Norak, then Molecular Devices)	\N	0		UCSF term, (aka 'Transfluor' assay)	Hande McGinty	BAO	\N	\N	2019-05-03 13:17:14.731653	2019-05-03 13:17:14.731653	submitted	\N			0	term
91	dd		\N	0			Michael Dorf	OPTIMAL	\N	\N	2019-04-19 16:50:41.486485	2019-04-19 16:50:41.486485	submitted	\N	http://webprotege.stanford.edu/RB7q67Gf4JtjKTY4hLsKWOU		0	term
92			\N	0			Michael Dorf	OPTIMAL	\N	\N	2019-04-19 16:52:50.992632	2019-04-19 16:52:50.992632	submitted	\N	http://webprotege.stanford.edu/RB7q67Gf4JtjKTY4hLsKWOU		0	term
93	ss		\N	0			Michael Dorf	OPTIMAL	\N	\N	2019-04-19 16:52:54.133975	2019-04-19 16:52:54.133975	submitted	\N	http://webprotege.stanford.edu/RB7q67Gf4JtjKTY4hLsKWOU		0	term
94			\N	0			Michael Dorf	OPTIMAL	\N	\N	2019-04-19 16:59:25.524996	2019-04-19 16:59:25.524996	submitted	\N	http://webprotege.stanford.edu/RB7q67Gf4JtjKTY4hLsKWOU		0	term
95			\N	0			Michael Dorf	OPTIMAL	\N	\N	2019-04-19 16:59:42.348236	2019-04-19 16:59:42.348236	submitted	\N	http://webprotege.stanford.edu/RB7q67Gf4JtjKTY4hLsKWOU		0	term
96	eee		\N	0			Michael Dorf	OPTIMAL	\N	\N	2019-04-19 16:59:48.323819	2019-04-19 16:59:48.323819	submitted	\N	http://webprotege.stanford.edu/RB7q67Gf4JtjKTY4hLsKWOU		0	term
97			\N	0			Michael Dorf	OPTIMAL	\N	\N	2019-04-19 17:00:39.614335	2019-04-19 17:00:39.614335	submitted	\N	http://webprotege.stanford.edu/RB7q67Gf4JtjKTY4hLsKWOU		0	term
40	Misha Term 6	This is a term 6 description	\N	22	www.yahoo.com	this term is required for our new project		???	\N	\N	2018-02-08 12:34:50.156131	2018-02-08 12:34:50.156131	submitted	\N	http://dev3.ccs.miami.edu:8080/ontolobridge#ONTB_000000022		1	term
41	Misha	test term	\N	5	yahoo.com			???	\N	\N	2018-02-08 16:53:40.505469	2018-02-08 16:53:40.505469	submitted	\N	http://dev3.ccs.miami.edu:8080/ontolobridge#ONTB_000000005		1	term
42	11	test term	\N	5	yahoo.com			???	\N	\N	2018-02-08 17:00:28.436835	2018-02-08 17:00:28.436835	submitted	\N	http://dev3.ccs.miami.edu:8080/ontolobridge#ONTB_000000005		1	term
43	Misha's new term	this is my new term	\N	3	google.com	need this for my projecdt		???	\N		2018-02-09 13:21:42.305877	2018-02-09 13:21:42.305877	rejected	\N	http://dev3.ccs.miami.edu:8080/ontolobridge#ONTB_000000003		1	term
44	has positive control	The positive control for an assay.	bao:BAO	740			Alex Clark	???	\N	\N	2018-03-09 15:50:09.875016	2018-03-09 15:50:09.875016	submitted	\N			0	term
45	has negative control	The negative control for an assay.	bao	740			Alex Clark	BAO	\N	\N	2018-03-09 15:51:06.107176	2018-03-09 15:51:06.107176	submitted	\N	\N		0	term
46	Test Class 2	test	BAO	740	test	test	test	BAO	\N	\N	2018-05-31 14:48:25.279981	2018-05-31 14:48:25.279981	submitted	\N	\N		0	term
70	apr17 term	Term of April 17	\N	0				???	\N	\N	2019-04-17 19:22:11.40765	2019-04-17 19:22:11.40765	submitted	\N	http://www.yahoo.com		0	term
71	apr17 20term	Term of April 17	\N	0				???	\N	\N	2019-04-17 19:22:53.182586	2019-04-17 19:22:53.182586	submitted	\N	http://www.yahoo.com		0	term
72	apr17 20term	Term 20of 20Apri 2017	\N	0				???	\N	\N	2019-04-17 19:23:17.803182	2019-04-17 19:23:17.803182	submitted	\N	http://www.yahoo.com		0	term
73	apr17 20term	Term 20of 20Apri 2017	\N	0				???	\N	\N	2019-04-17 19:23:44.394167	2019-04-17 19:23:44.394167	submitted	\N	http://www.yahoo.com		0	term
74	apr17 20term	Term 20of 20Apri 2017	\N	0				???	\N	\N	2019-04-17 20:23:16.243623	2019-04-17 20:23:16.243623	submitted	\N	http://www.yahoo.com		0	term
75	apr17 20term	Term 20of 20Apri 2017	\N	0				???	\N	\N	2019-04-17 20:27:09.397955	2019-04-17 20:27:09.397955	submitted	\N			0	term
76	term apr 14 15:30	term apr 14 15:30	\N	0				???	\N	\N	2019-04-18 17:55:04.208383	2019-04-18 17:55:04.208383	submitted	\N	http://www.yahoo.com		0	term
77	term apr 14 15:30	term apr 14 15:30	\N	0				???	\N	\N	2019-04-18 17:55:16.660597	2019-04-18 17:55:16.660597	submitted	\N	http://www.yahoo.com		0	term
142	Testing Mysql	mysql test	BAO	3112				???	\N	\N	2019-11-26 10:59:42.613994	2019-11-26 10:59:42.613994	submitted	\N	http://www.bioassayontology.org/bao#BAO_0003112		1	term
143	Testing Mysql	mysql test	BAO	3112				???	\N	\N	2019-11-26 11:05:37.5	2019-11-26 11:05:37.5	submitted	\N	http://www.bioassayontology.org/bao#BAO_0003112		1	term
\.


--
-- Name: maintainers_id_seq; Type: SEQUENCE SET; Schema: public; Owner: ob_user
--

SELECT pg_catalog.setval('public.maintainers_id_seq', 3, true);


--
-- Name: notifications_id_seq; Type: SEQUENCE SET; Schema: public; Owner: ob_user
--

SELECT pg_catalog.setval('public.notifications_id_seq', 70, true);


--
-- Name: ontologies_id_seq; Type: SEQUENCE SET; Schema: public; Owner: ob_user
--

SELECT pg_catalog.setval('public.ontologies_id_seq', 2, true);


--
-- Name: ontology_to_maintainer_id_seq; Type: SEQUENCE SET; Schema: public; Owner: ob_user
--

SELECT pg_catalog.setval('public.ontology_to_maintainer_id_seq', 10, true);


--
-- Name: request_status_id_seq; Type: SEQUENCE SET; Schema: public; Owner: ob_user
--

SELECT pg_catalog.setval('public.request_status_id_seq', 79, true);


--
-- Name: requests_id_seq; Type: SEQUENCE SET; Schema: public; Owner: ob_user
--

SELECT pg_catalog.setval('public.requests_id_seq', 143, true);


--
-- Name: maintainers maintainers_pkey; Type: CONSTRAINT; Schema: public; Owner: ob_user
--

ALTER TABLE ONLY public.maintainers
    ADD CONSTRAINT maintainers_pkey PRIMARY KEY (id);


--
-- Name: notifications notifications_pkey; Type: CONSTRAINT; Schema: public; Owner: ob_user
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_pkey PRIMARY KEY (id);


--
-- Name: ontologies ontologies_pkey; Type: CONSTRAINT; Schema: public; Owner: ob_user
--

ALTER TABLE ONLY public.ontologies
    ADD CONSTRAINT ontologies_pkey PRIMARY KEY (id);


--
-- Name: ontology_to_maintainer ontology_to_maintainer_pkey; Type: CONSTRAINT; Schema: public; Owner: ob_user
--

ALTER TABLE ONLY public.ontology_to_maintainer
    ADD CONSTRAINT ontology_to_maintainer_pkey PRIMARY KEY (id);


--
-- Name: request_status request_status_pk; Type: CONSTRAINT; Schema: public; Owner: ob_user
--

ALTER TABLE ONLY public.request_status
    ADD CONSTRAINT request_status_pk PRIMARY KEY (id);


--
-- Name: requests requests_pkey; Type: CONSTRAINT; Schema: public; Owner: ob_user
--

ALTER TABLE ONLY public.requests
    ADD CONSTRAINT requests_pkey PRIMARY KEY (id);


--
-- Name: request_status_id_uindex; Type: INDEX; Schema: public; Owner: ob_user
--

CREATE UNIQUE INDEX request_status_id_uindex ON public.request_status USING btree (id);


--
-- Name: ontology_to_maintainer ontology_to_maintainer_maintainer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ob_user
--

ALTER TABLE ONLY public.ontology_to_maintainer
    ADD CONSTRAINT ontology_to_maintainer_maintainer_id_fkey FOREIGN KEY (maintainer_id) REFERENCES public.maintainers(id) ON DELETE CASCADE;


--
-- Name: ontology_to_maintainer ontology_to_maintainer_ontology_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ob_user
--

ALTER TABLE ONLY public.ontology_to_maintainer
    ADD CONSTRAINT ontology_to_maintainer_ontology_id_fkey FOREIGN KEY (ontology_id) REFERENCES public.ontologies(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

