CREATE TABLE crypto_member.member
(
    id                SERIAL PRIMARY KEY,
    email             CHARACTER VARYING(255) NOT NULL,
    password          CHARACTER VARYING(255) NOT NULL,
    phone             CHARACTER VARYING(255),
    name              CHARACTER VARYING(255),
    surname           CHARACTER VARYING(255),
    activation_token  CHARACTER VARYING(255),
    activation_pin    CHARACTER VARYING(255),
    status            CHARACTER VARYING(255),
    registration_date TIMESTAMP,
    last_logged_in    TIMESTAMP
);
