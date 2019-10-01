CREATE TABLE crypto_member_strategy.member_strategy
(
    id                     SERIAL PRIMARY KEY,
    member_id              BIGINT                 NOT NULL,
    market_id              BIGINT                 NOT NULL,
    strategy_configuration TEXT                   NOT NULL,
    draw_configuration     TEXT                   NOT NULL,
    strategy_type          CHARACTER VARYING(255) NOT NULL,
    time_frame             CHARACTER VARYING(255) NOT NULL,
    update_time_unit       CHARACTER VARYING(255) NOT NULL,
    update_time_value      INT                    NOT NULL,
    market_name            CHARACTER VARYING(255) NOT NULL,
    stock                  CHARACTER VARYING(255) NOT NULL,
    strategy_name          CHARACTER VARYING(255) NOT NULL,
    custom_strategy_name   CHARACTER VARYING(255) NOT NULL,
    status                 CHARACTER VARYING(255) NOT NULL,
    cycle_count            BIGINT                 NOT NULL,
    failed_count           BIGINT                 NOT NULL,
    stopped_reason         CHARACTER VARYING(255),
    next_execution_time    TIMESTAMP,
    last_execution_time    TIMESTAMP,
    last_signal_tick_time  TIMESTAMP,
    last_signal_position   CHARACTER VARYING(255)
);
