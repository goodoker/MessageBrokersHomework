CREATE TABLE IF NOT EXISTS credit_claims
(
    id int8 GENERATED ALWAYS AS IDENTITY NOT NULL,
    amount numeric(16, 2) NOT NULL,
    term int4 NOT NULL,
    income numeric(16, 2) NOT NULL,
    current_credit_load numeric(16, 2) NOT NULL,
    current_credit_rating int4 NOT NULL,
    status SMALLINT NOT NULL,
    CONSTRAINT credit_claims_pkey PRIMARY KEY (id)
);