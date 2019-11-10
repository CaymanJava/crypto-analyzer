CREATE TABLE crypto_member_strategy.member_strategy_position
(
    member_strategy_id BIGSERIAL NOT NULL,
    position           CHARACTER VARYING(15),
    CONSTRAINT member_strategy_id_fkey FOREIGN KEY (member_strategy_id)
        REFERENCES crypto_member_strategy.member_strategy (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE CASCADE
);

ALTER TABLE crypto_member_strategy.member_strategy
    DROP COLUMN last_signal_position_hash;
