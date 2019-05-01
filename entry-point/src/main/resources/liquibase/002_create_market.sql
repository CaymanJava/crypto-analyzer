CREATE SCHEMA crypto_market;

CREATE TABLE crypto_market.market
(
    id                   SERIAL PRIMARY KEY,
    market_id            BIGINT,
    stock                CHARACTER VARYING(255),
    market_currency      CHARACTER VARYING(255),
    base_currency        CHARACTER VARYING(255),
    market_currency_long CHARACTER VARYING(255),
    base_currency_long   CHARACTER VARYING(255),
    min_trade_size       NUMERIC,
    market_name          CHARACTER VARYING(255),
    active               BOOLEAN DEFAULT FALSE,
    created              TIMESTAMP,
    monitor              BOOLEAN DEFAULT FALSE,
    notice               TEXT,
    logo_url             CHARACTER VARYING(255),
    status               CHARACTER VARYING(255),
    high                 NUMERIC,
    low                  NUMERIC,
    volume               NUMERIC,
    last                 NUMERIC,
    base_volume          NUMERIC,
    update_time          TIMESTAMP,
    bid                  NUMERIC,
    ask                  NUMERIC,
    open_buy_orders      INTEGER,
    open_sell_orders     INTEGER,
    prev_day             NUMERIC,
    price_diff           NUMERIC
);