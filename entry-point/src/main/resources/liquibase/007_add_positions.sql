CREATE TABLE crypto_monitoring.signal_position
(
    signal_id BIGSERIAL NOT NULL,
    position  CHARACTER VARYING(15),
    CONSTRAINT signal_id_fkey FOREIGN KEY (signal_id)
        REFERENCES crypto_monitoring.signal (id) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE CASCADE
);

ALTER TABLE crypto_monitoring.signal
    DROP COLUMN position;

ALTER TABLE crypto_member_strategy.member_strategy
    DROP COLUMN last_signal_position;

ALTER TABLE crypto_member_strategy.member_strategy
    ADD COLUMN last_signal_position_hash INTEGER;
