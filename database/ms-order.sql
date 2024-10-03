--
-- PostgreSQL database dump
--

-- Dumped from database version 14.4
-- Dumped by pg_dump version 14.4

--
-- Name: ms_schema; Type: SCHEMA; Schema: -; Owner: msadm
--


--
-- Name: order_item_tx; Type: TABLE; Schema: ms_schema; Owner: msadm
--

CREATE TABLE ms_schema.order_item_tx (
    uuid uuid NOT NULL,
    createdby character varying(255) NOT NULL,
    createdtime timestamp without time zone NOT NULL,
    isactive boolean,
    updatedby character varying(255) NOT NULL,
    updatedtime timestamp without time zone NOT NULL,
    version integer,
    price numeric(19,2),
    productid character varying(255),
    productname character varying(255),
    quantity numeric(19,2),
    order_id uuid
);


ALTER TABLE ms_schema.order_item_tx OWNER TO msadm;

--
-- Name: order_payment_tx; Type: TABLE; Schema: ms_schema; Owner: msadm
--

CREATE TABLE ms_schema.order_payment_tx (
    uuid uuid NOT NULL,
    createdby character varying(255) NOT NULL,
    createdtime timestamp without time zone NOT NULL,
    isactive boolean,
    updatedby character varying(255) NOT NULL,
    updatedtime timestamp without time zone NOT NULL,
    version integer,
    status character varying(255),
    transaction_id character varying(255)
);


ALTER TABLE ms_schema.order_payment_tx OWNER TO msadm;

--
-- Name: order_state_history_tx; Type: TABLE; Schema: ms_schema; Owner: msadm
--

CREATE TABLE ms_schema.order_state_history_tx (
    uuid uuid NOT NULL,
    createdby character varying(255) NOT NULL,
    createdtime timestamp without time zone NOT NULL,
    isactive boolean,
    updatedby character varying(255) NOT NULL,
    updatedtime timestamp without time zone NOT NULL,
    version integer,
    notes character varying(255),
    sourcestate character varying(255),
    targetstate character varying(255),
    transitionevent character varying(255),
    order_id uuid,
    orderversion integer
);


ALTER TABLE ms_schema.order_state_history_tx OWNER TO msadm;

--
-- Name: order_tx; Type: TABLE; Schema: ms_schema; Owner: msadm
--

CREATE TABLE ms_schema.order_tx (
    uuid uuid NOT NULL,
    createdby character varying(255) NOT NULL,
    createdtime timestamp without time zone NOT NULL,
    isactive boolean,
    updatedby character varying(255) NOT NULL,
    updatedtime timestamp without time zone NOT NULL,
    version integer,
    currency character varying(255),
    customer_id character varying(255),
    city character varying(255),
    country character varying(255),
    landmark character varying(255),
    phone character varying(255),
    state character varying(255),
    street character varying(255),
    zip_code character varying(255),
    totalordervalue numeric(19,2),
    payment_id uuid,
    orderstatus character varying(255),
    result character varying(255)
);


ALTER TABLE ms_schema.order_tx OWNER TO msadm;



--
-- Data for Name: order_item_tx; Type: TABLE DATA; Schema: ms_schema; Owner: msadm
--

COPY ms_schema.order_item_tx (uuid, createdby, createdtime, isactive, updatedby, updatedtime, version, price, productid, productname, quantity, order_id) FROM stdin;
fb19e155-144c-493d-895d-ff7614e7f53f	anonymousUser	2023-07-05 11:24:30.548063	t	anonymousUser	2023-07-07 12:44:27.556828	1	40000.00	5dcb87dd-0480-4e45-be83-ddcba3313c4f	Apple Watch 5	1.00	7a8bc3d8-911f-4aee-bc71-5dfa25a4a3ae
34510d2c-663b-40da-9154-55aa3ee75118	anonymousUser	2023-07-05 11:24:30.547118	t	anonymousUser	2023-07-07 12:44:27.556975	1	45000.00	25886064-2a6d-417a-9040-111166f250f3	Apple Watch 6	1.00	7a8bc3d8-911f-4aee-bc71-5dfa25a4a3ae
33afea94-2cde-40e5-afee-0dd4de32546b	anonymousUser	2023-07-07 11:24:30.547	t	anonymousUser	2023-07-07 12:44:27.556975	1	90000.00	25886064-2a6d-417a-9040-111166f250f3	iPhone 15	1.00	e5a5bb0d-6282-4072-9926-a0653095fd07
3bb8be4a-ca61-4d19-9a7d-5e43c134916a	anonymousUser	2023-07-07 11:24:30.547	t	anonymousUser	2023-07-07 12:44:27.556975	1	80000.00	25886064-2a6d-417a-9040-111166f250f3	iPhone 14	1.00	e5a5bb0d-6282-4072-9926-a0653095fd07
\.


--
-- Data for Name: order_payment_tx; Type: TABLE DATA; Schema: ms_schema; Owner: msadm
--

COPY ms_schema.order_payment_tx (uuid, createdby, createdtime, isactive, updatedby, updatedtime, version, status, transaction_id) FROM stdin;
63fd3531-ee6b-40d8-aed0-2faeebc41c87	anonymousUser	2023-07-05 11:24:30.527592	t	anonymousUser	2023-07-05 11:24:30.527592	0	string	string
999bda45-b1ad-4af3-a2fa-4564cca52ed6	anonymousUser	2023-07-07 12:44:27.541352	t	anonymousUser	2023-07-07 12:44:27.541352	0	string	string
\.


--
-- Data for Name: order_state_history_tx; Type: TABLE DATA; Schema: ms_schema; Owner: msadm
--

COPY ms_schema.order_state_history_tx (uuid, createdby, createdtime, isactive, updatedby, updatedtime, version, notes, sourcestate, targetstate, transitionevent, order_id, orderversion) FROM stdin;
c86ddba1-6869-495d-9e44-3d181ea41f87	anonymousUser	2023-07-13 18:00:32.710834	t	anonymousUser	2023-07-13 18:00:32.710834	0		PAYMENT_CONFIRMED	PACKING_FORK	PACKAGE_FORK_EVENT	e5a5bb0d-6282-4072-9926-a0653095fd07	1605
1f33c58e-ae3d-42bc-a5cf-eb622a888603	anonymousUser	2023-07-13 18:00:32.716818	t	anonymousUser	2023-07-13 18:00:32.716818	0		PACKING_FORK	ORDER_PACKAGING_START	PACKAGE_FORK_EVENT	e5a5bb0d-6282-4072-9926-a0653095fd07	1606
f8d069bc-4f15-4c67-8dd1-ce6e87f4f693	anonymousUser	2023-07-13 18:00:32.721738	t	anonymousUser	2023-07-13 18:00:32.721738	0		ORDER_PACKAGING_START	SEND_BILL_START	PACKAGE_FORK_EVENT	e5a5bb0d-6282-4072-9926-a0653095fd07	1607
b0e36d16-d0a2-4f27-9022-81758040d6ba	anonymousUser	2023-07-13 18:00:32.728011	t	anonymousUser	2023-07-13 18:00:32.728011	0		SEND_BILL_START	ORDER_PACKAGING_DONE	PACKAGE_EVENT	e5a5bb0d-6282-4072-9926-a0653095fd07	1608
1f49f434-a161-4a63-a8fc-5bfb7067e826	anonymousUser	2023-07-13 18:00:32.736294	t	anonymousUser	2023-07-13 18:00:32.736294	0		ORDER_PACKAGING_DONE	SEND_BILL_DONE	ORDER_SEND_BILL_EVENT	e5a5bb0d-6282-4072-9926-a0653095fd07	1609
845665d0-9019-473b-8a97-1975961c67c7	anonymousUser	2023-07-13 18:00:32.744518	t	anonymousUser	2023-07-13 18:00:32.744518	0		PACKING_FORK	SHIPPED	ORDER_SEND_BILL_EVENT	e5a5bb0d-6282-4072-9926-a0653095fd07	1610
83cf194e-84b8-45b7-a591-2d2d422de703	anonymousUser	2023-07-13 18:00:33.835162	t	anonymousUser	2023-07-13 18:00:33.835162	0		SHIPPED	IN_TRANSIT	ORDER_IN_TRANSIT_EVENT	e5a5bb0d-6282-4072-9926-a0653095fd07	1611
016cc36f-0e57-48de-bca4-01d74941dfe3	anonymousUser	2023-07-13 18:00:34.888597	t	anonymousUser	2023-07-13 18:00:34.888597	0		IN_TRANSIT	REACHED_DESTINATION	SEND_FOR_DELIVERY_EVENT	e5a5bb0d-6282-4072-9926-a0653095fd07	1612
6ddd6cd3-26d7-43e2-bb4d-687a6e9ec314	anonymousUser	2023-07-13 18:00:35.968272	t	anonymousUser	2023-07-13 18:00:35.968272	0		REACHED_DESTINATION	DELIVERED	ORDER_DELIVERED_EVENT	e5a5bb0d-6282-4072-9926-a0653095fd07	1613
67883839-bbb7-453c-bf72-b0ffc8424ef9	anonymousUser	2023-07-13 18:00:35.976364	t	anonymousUser	2023-07-13 18:00:35.976364	0		DELIVERED	ORDER_COMPLETED	AUTO_TRANSITION_EVENT	e5a5bb0d-6282-4072-9926-a0653095fd07	1614
dad9efb3-dc2b-4e83-afff-6ad2ecb638a8	anonymousUser	2023-07-13 18:00:28.454872	t	anonymousUser	2023-07-13 18:00:28.454872	0		ORDER_INITIALIZED	CREDIT_CHECKING	CREDIT_CHECKING_EVENT	e5a5bb0d-6282-4072-9926-a0653095fd07	1601
ba4341e4-40db-4628-bd8b-4af8d079ccd4	anonymousUser	2023-07-13 18:00:29.539638	t	anonymousUser	2023-07-13 18:00:29.539638	0		CREDIT_CHECKING	CREDIT_APPROVED	CREDIT_APPROVED_EVENT	e5a5bb0d-6282-4072-9926-a0653095fd07	1602
94420e1c-63ec-418f-9d93-6e0fe20b714c	anonymousUser	2023-07-13 18:00:30.598671	t	anonymousUser	2023-07-13 18:00:30.598671	0		CREDIT_APPROVED	PAYMENT_PROCESSING	PAYMENT_INIT_EVENT	e5a5bb0d-6282-4072-9926-a0653095fd07	1603
37d17669-8bf6-4bfd-acef-ac28bb5d8749	anonymousUser	2023-07-13 18:00:31.657143	t	anonymousUser	2023-07-13 18:00:31.657143	0		PAYMENT_PROCESSING	PAYMENT_CONFIRMED	PAYMENT_APPROVED_EVENT	e5a5bb0d-6282-4072-9926-a0653095fd07	1604
\.


--
-- Data for Name: order_tx; Type: TABLE DATA; Schema: ms_schema; Owner: msadm
--

COPY ms_schema.order_tx (uuid, createdby, createdtime, isactive, updatedby, updatedtime, version, currency, customer_id, city, country, landmark, phone, state, street, zip_code, totalordervalue, payment_id, orderstatus, result) FROM stdin;
7a8bc3d8-911f-4aee-bc71-5dfa25a4a3ae	anonymousUser	2023-07-07 12:44:27.546798	t	anonymousUser	2023-07-11 11:43:12.021425	40	INR	123	Edison	USA	Near Walmart	7321002010	NJ	321 Cobblestone Lane	08110	85000.00	999bda45-b1ad-4af3-a2fa-4564cca52ed6	ORDER_INITIALIZED	IN_PROGRESS
e5a5bb0d-6282-4072-9926-a0653095fd07	anonymousUser	2023-07-05 11:24:30.537602	t	anonymousUser	2023-07-13 18:00:35.97734	1614	INR	123	Edison	USA	Near Walmart	7321002010	NJ	321 Cobblestone Lan	08110	170000.00	63fd3531-ee6b-40d8-aed0-2faeebc41c87	ORDER_COMPLETED	DELIVERED
\.

--
-- Name: order_item_tx order_item_tx_pkey; Type: CONSTRAINT; Schema: ms_schema; Owner: msadm
--

ALTER TABLE ONLY ms_schema.order_item_tx
    ADD CONSTRAINT order_item_tx_pkey PRIMARY KEY (uuid);


--
-- Name: order_state_history_tx order_state_history_tx_pkey; Type: CONSTRAINT; Schema: ms_schema; Owner: msadm
--

ALTER TABLE ONLY ms_schema.order_state_history_tx
    ADD CONSTRAINT order_state_history_tx_pkey PRIMARY KEY (uuid);


--
-- Name: order_tx order_tx_pkey; Type: CONSTRAINT; Schema: ms_schema; Owner: msadm
--

ALTER TABLE ONLY ms_schema.order_tx
    ADD CONSTRAINT order_tx_pkey PRIMARY KEY (uuid);


--
-- Name: order_payment_tx payment_tx_pkey; Type: CONSTRAINT; Schema: ms_schema; Owner: msadm
--

ALTER TABLE ONLY ms_schema.order_payment_tx
    ADD CONSTRAINT payment_tx_pkey PRIMARY KEY (uuid);



ALTER TABLE ONLY ms_schema.order_tx
    ADD CONSTRAINT fk17hk2k77tycbvnfa968wayqhd FOREIGN KEY (payment_id) REFERENCES ms_schema.order_payment_tx(uuid);

--
-- Name: order_item_tx fkb3heb7c2x68gtl42n66w217vl; Type: FK CONSTRAINT; Schema: ms_schema; Owner: msadm
--

ALTER TABLE ONLY ms_schema.order_item_tx
    ADD CONSTRAINT fkb3heb7c2x68gtl42n66w217vl FOREIGN KEY (order_id) REFERENCES ms_schema.order_tx(uuid);


--
-- Name: order_state_history_tx fkqm01yul77rf1ekm5seeqxnaqp; Type: FK CONSTRAINT; Schema: ms_schema; Owner: msadm
--

ALTER TABLE ONLY ms_schema.order_state_history_tx
    ADD CONSTRAINT fkqm01yul77rf1ekm5seeqxnaqp FOREIGN KEY (order_id) REFERENCES ms_schema.order_tx(uuid);


--
-- PostgreSQL database dump complete
--

