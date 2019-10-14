CREATE TABLE crypto_monitoring.signal
(
    id                   SERIAL PRIMARY KEY,
    position             CHARACTER VARYING(255) NOT NULL,
    member_id            BIGINT                 NOT NULL,
    market_id            BIGINT                 NOT NULL,
    member_strategy_id   BIGINT                 NOT NULL,
    strategy_type        CHARACTER VARYING(255) NOT NULL,
    time_frame           CHARACTER VARYING(255) NOT NULL,
    market_name          CHARACTER VARYING(255) NOT NULL,
    stock                CHARACTER VARYING(255) NOT NULL,
    strategy_name        CHARACTER VARYING(255) NOT NULL,
    custom_strategy_name CHARACTER VARYING(255) NOT NULL,
    tick_time            TIMESTAMP              NOT NULL,
    creation_time        TIMESTAMP              NOT NULL
);
