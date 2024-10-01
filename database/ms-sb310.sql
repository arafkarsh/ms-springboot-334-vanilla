--
-- PostgreSQL database dump
--

-- Dumped from database version 14.4
-- Dumped by pg_dump version 14.4

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
-- Name: ms_schema; Type: SCHEMA; Schema: -; Owner: arafkarsh
--

CREATE SCHEMA ms_schema;


ALTER SCHEMA ms_schema OWNER TO arafkarsh;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: carts_tx; Type: TABLE; Schema: ms_schema; Owner: postgres
--

CREATE TABLE ms_schema.carts_tx (
    uuid uuid NOT NULL,
    createdby character varying(255) NOT NULL,
    createdtime timestamp(6) without time zone NOT NULL,
    updatedby character varying(255) NOT NULL,
    updatedtime timestamp(6) without time zone NOT NULL,
    isactive boolean,
    version integer,
    customerid character varying(255) NOT NULL,
    productid character varying(255) NOT NULL,
    productname character varying(32),
    price numeric(38,2) NOT NULL,
    quantity numeric(38,2) NOT NULL
);


ALTER TABLE ms_schema.carts_tx OWNER TO postgres;

--
-- Name: country_m; Type: TABLE; Schema: ms_schema; Owner: postgres
--

CREATE TABLE ms_schema.country_m (
    countryuuid character(36) NOT NULL,
    countrycode character varying(255) NOT NULL,
    countryid integer NOT NULL,
    countryname character varying(255) NOT NULL,
    countryofficialname character varying(255)
);


ALTER TABLE ms_schema.country_m OWNER TO postgres;

--
-- Name: country_t; Type: TABLE; Schema: ms_schema; Owner: arafkarsh
--

CREATE TABLE ms_schema.country_t (
    cid integer NOT NULL,
    countryid integer NOT NULL,
    countrycode character varying(255) NOT NULL,
    countryname character varying(255) NOT NULL,
    countryofficialname character varying(255)
);


ALTER TABLE ms_schema.country_t OWNER TO arafkarsh;

--
-- Name: products_m; Type: TABLE; Schema: ms_schema; Owner: postgres
--

CREATE TABLE ms_schema.products_m (
    uuid bpchar NOT NULL,
    createdby character varying(255) NOT NULL,
    createdtime timestamp(6) without time zone NOT NULL,
    updatedby character varying(255) NOT NULL,
    updatedtime timestamp(6) without time zone NOT NULL,
    isactive boolean,
    version integer,
    productdetails character varying(64) NOT NULL,
    productlocationzipcode character varying(255),
    productname character varying(32),
    price numeric(38,2) NOT NULL
);


ALTER TABLE ms_schema.products_m OWNER TO postgres;

--
-- Data for Name: carts_tx; Type: TABLE DATA; Schema: ms_schema; Owner: postgres
--

COPY ms_schema.carts_tx (uuid, createdby, createdtime, updatedby, updatedtime, isactive, version, customerid, productid, productname, price, quantity) FROM stdin;
22273a09-ee9e-4e5a-98fa-6bcfcfa97b49	john.doe	2023-05-28 20:26:12.919	john.doe	2023-05-28 20:26:12.919	t	0	123	789	Pencil	10.00	3.00
7b54e398-711a-4820-a32c-81c7dfab1ab1	john.doe	2023-05-28 20:26:12.919	john.doe	2023-05-28 20:26:12.919	t	0	123	678	Pen	30.00	2.00
\.


--
-- Data for Name: country_m; Type: TABLE DATA; Schema: ms_schema; Owner: postgres
--

COPY ms_schema.country_m (countryuuid, countrycode, countryid, countryname, countryofficialname) FROM stdin;
\.


--
-- Data for Name: country_t; Type: TABLE DATA; Schema: ms_schema; Owner: arafkarsh
--

COPY ms_schema.country_t (cid, countryid, countrycode, countryname, countryofficialname) FROM stdin;
1	1	USA	America	United States of America
2	250	FRA	France	The French Republic
3	76	BRA	Brazil	The Federative Republic of Brazil
6	124	CAN	Canada	Canada
7	276	DEU	Germany	Federal Republic of Germany
8	724	ESP	Spain	Kingdom of Spain
9	380	ITA	Italy	Italian Republic
10	56	BEL	Belgium	Kingdom of Belgium
11	528	NLD	Netherlands	Kingdom of Netherlands
12	616	POL	Poland	Republic of Poland
13	792	TUR	Turkey	Republic of Turkey
15	40	AUT	Austria	Republic of Austria
14	756	CHE	Switzerland	Swiss Confederation
16	32	ARG	Argentina	Argentine Republic
17	76	BRA	Brazil	Federative Republic of Brazil
18	152	CHL	Chile	Republic of Chile
19	170	COL	Columbia	Republic of Columbia
20	484	MEX	Mexico	United Mexican States
21	858	URY	Uruguay	Oriental Republic of Uruguay
22	604	PER	Peru	Republic of Peru
23	862	VEN	Venezuela	Bolivarin Republic of Venezuela
4	111	ITA	Italy	Italy
5	356	IND	India	Republic of India
\.


--
-- Data for Name: products_m; Type: TABLE DATA; Schema: ms_schema; Owner: postgres
--

COPY ms_schema.products_m (uuid, createdby, createdtime, updatedby, updatedtime, isactive, version, productdetails, productlocationzipcode, productname, price) FROM stdin;
22273a09-ee9e-4e5a-98fa-6bcfcfa97b49	john.doe	2023-05-28 20:26:12.919	john.doe	2023-05-28 20:26:12.919	t	0	iPhone 10, 64 GB	12345	iPhone 10	60000.00
7b54e398-711a-4820-a32c-81c7dfab1ab1	john.doe	2023-05-28 20:26:12.925	john.doe	2023-05-28 20:26:12.925	t	0	iPhone 11, 128 GB	12345	iPhone 11	70000.00
14879f35-9b10-4b75-8641-280731161aee	john.doe	2023-05-28 20:26:12.925	john.doe	2023-05-28 20:26:12.925	t	0	Samsung Galaxy s20, 256 GB	12345	Samsung Galaxy s20	80000.00
2b4d83d6-0063-429e-9d27-94aacb6abba6	john.doe	2023-05-28 20:31:14.267	john.doe	2023-05-28 20:31:14.267	t	0	Google Pixel 7, 128 GB SSD, 16GB RAM	98321	Google Pixel 7	60000.00
273c4812-40cb-4939-ae22-f4ad89d7a0ec	john.doe	2023-05-28 20:32:40.006	john.doe	2023-05-28 20:32:40.006	t	0	Samsung Galaxy S22, 16GB RAM, 128 GB SDD	46123	Samsung Galaxy S22	65000.00
eef67186-ff2a-42b9-809e-93536d0c1076	john.doe	2023-05-28 23:11:41.662	john.doe	2023-05-28 23:11:41.662	t	0	Google Pixel 6, 8GB RAM, 64GB SSD	12345	Google Pixel 6	50000.00
1a9d1e4e-525b-4dc4-b8ad-9a354a7b3418	john.doe	2023-06-25 19:21:01.598	john.doe	2023-06-25 19:21:01.598	t	0	Google Pixel 5 32 GB	12345	Google Pixel 5	35000.00
434ca819-4d11-4965-a88a-c087681414a8	john.doe	2023-06-25 22:26:09.847	john.doe	2023-06-25 22:26:09.847	t	0	Google Pixel 5 32 GB	12345	Google Pixel 5	35000.00
6c4bd74b-fb0a-4988-b83d-531744e4f8bf	john.doe	2023-06-25 22:26:21.592	john.doe	2023-06-25 22:26:21.592	t	0	Google Pixel 5 64 GB	12345	Google Pixel 5	40000.00
fd3288ef-2d49-4ea1-9a58-bb2f280fc2bd	john.doe	2023-06-25 22:44:10.901	john.doe	2023-06-25 22:44:10.901	t	0	Google Pixel 5 128 GB	12345	Google Pixel 5	50000.00
75383383-8ed7-4c28-a79c-7ca1cd6713e8	john.doe	2023-06-25 23:05:11.667	john.doe	2023-06-25 23:05:11.667	t	0	iPhone 10, 128GB BLACK	12345	iPhone 10	65000.00
adff3e12-d019-49fe-bfd1-f8634ccec604	john.doe	2023-06-25 23:05:57.463	john.doe	2023-06-25 23:05:57.463	t	0	iPhone 10, 128GB WHITE	12345	iPhone 10	65000.00
8b1458b1-5aa9-437f-a3de-ea8050ba0e05	john.doe	2023-06-25 23:06:09.566	john.doe	2023-06-25 23:06:09.566	t	0	iPhone 10, 128GB GOLD	12345	iPhone 10	65000.00
d5e3fa1d-86c7-4bf5-985b-af865bea9c07	john.doe	2023-06-25 23:07:23.325	john.doe	2023-06-25 23:07:23.325	t	0	iPhone 11, 256GB BLACK	12345	iPhone 11	75000.00
b7277469-cb79-4746-abae-e595b32a3666	john.doe	2023-06-25 23:07:38.454	john.doe	2023-06-25 23:07:38.454	t	0	iPhone 11, 512GB BLACK	12345	iPhone 11	85000.00
f2cc6216-9d04-4f3f-882f-4f1567a1a449	john.doe	2023-06-25 23:09:04.734	john.doe	2023-06-25 23:09:04.734	t	0	iPhone 12, 512GB BLACK	12345	iPhone 12	95000.00
\.


--
-- Name: carts_tx carts_tx_pkey; Type: CONSTRAINT; Schema: ms_schema; Owner: postgres
--

ALTER TABLE ONLY ms_schema.carts_tx
    ADD CONSTRAINT carts_tx_pkey PRIMARY KEY (uuid);


--
-- Name: country_m country_m_pkey; Type: CONSTRAINT; Schema: ms_schema; Owner: postgres
--

ALTER TABLE ONLY ms_schema.country_m
    ADD CONSTRAINT country_m_pkey PRIMARY KEY (countryuuid);


--
-- Name: country_t country_t_pkey; Type: CONSTRAINT; Schema: ms_schema; Owner: arafkarsh
--

ALTER TABLE ONLY ms_schema.country_t
    ADD CONSTRAINT country_t_pkey PRIMARY KEY (cid);


--
-- Name: products_m products_m_pkey1; Type: CONSTRAINT; Schema: ms_schema; Owner: postgres
--

ALTER TABLE ONLY ms_schema.products_m
    ADD CONSTRAINT products_m_pkey1 PRIMARY KEY (uuid);


--
-- PostgreSQL database dump complete
--

