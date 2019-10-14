ALTER TABLE crypto_member_strategy.member_strategy
    ADD COLUMN notification_destination CHARACTER VARYING(255) NOT NULL DEFAULT 'ALL';
