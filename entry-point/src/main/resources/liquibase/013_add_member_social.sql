ALTER TABLE crypto_member.member
    ALTER COLUMN password DROP NOT NULL;

ALTER TABLE crypto_member.member
    ADD COLUMN avatar_url CHARACTER VARYING;

ALTER TABLE crypto_member.member
    ADD COLUMN register_place CHARACTER VARYING(255) DEFAULT 'WEB';

CREATE TABLE crypto_social.member_social_access
(
    id        SERIAL PRIMARY KEY,
    social_id CHARACTER VARYING(255) NOT NULL,
    provider  CHARACTER VARYING(255) NOT NULL,
    member_id BIGINT
);
